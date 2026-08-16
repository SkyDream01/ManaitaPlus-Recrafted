package github.com.gengyoubo.common.event;

import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/** Loader-independent range/depth block breaking used by all three platforms. */
public final class MPGToolMiningLogic {
    public enum Result {
        PASS,
        HANDLED,
        DENIED
    }

    private MPGToolMiningLogic() {
    }

    public static Result destroyBlocks(ServerLevel level, ServerPlayer player, ItemStack stack,
                                       BlockPos origin, Direction hitFace, int doublingMultiplier,
                                       boolean creativeRangeDestroy) {
        if (!(stack.getItem() instanceof IMPGDestroy destroyItem)) {
            return Result.PASS;
        }
        if (!destroyItem.canHarvest(stack)) {
            return Result.DENIED;
        }

        int range = clamp(destroyItem.getRange(stack), 1, 27);
        int depth = clamp(destroyItem.getDepth(stack), 1, 27);
        if (player.getAbilities().instabuild && !creativeRangeDestroy && (range > 1 || depth > 1)) {
            return Result.PASS;
        }

        Direction face = hitFace == null ? Direction.UP : hitFace;
        Direction inward = face.getOpposite();
        int radius = range / 2;
        boolean multiplyDrops = stack.getItem() instanceof IMPGDoubling doubling
                && doubling.isDoubling(stack)
                && (!destroyItem.requiresShiftForDoubling() || player.isShiftKeyDown());
        int multiplier = multiplyDrops ? Math.max(1, doublingMultiplier) : 1;
        boolean handled = false;

        for (int deep = 0; deep < depth; deep++) {
            BlockPos layerCenter = origin.relative(inward, deep);
            for (int first = -radius; first <= radius; first++) {
                for (int second = -radius; second <= radius; second++) {
                    BlockPos target = offsetInFacePlane(layerCenter, face, first, second);
                    if (!destroyItem.canDigUnderPlayer(stack) && isUnderPlayer(player, target)) {
                        continue;
                    }
                    BlockState state = level.getBlockState(target);
                    if (state.isAir() || destroyItem.accept(state)
                            || state.getDestroySpeed(level, target) < 0.0F) {
                        continue;
                    }
                    handled |= destroyBlock(level, player, stack, target, state, multiplier);
                }
            }
        }
        return handled ? Result.HANDLED : Result.PASS;
    }

    private static boolean destroyBlock(ServerLevel level, ServerPlayer player, ItemStack tool,
                                        BlockPos pos, BlockState state, int multiplier) {
        Block block = state.getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean drops = !player.getAbilities().instabuild;
        List<ItemStack> itemDrops = drops
                ? Block.getDrops(state, level, pos, blockEntity, player, tool)
                : List.of();

        block.playerWillDestroy(level, pos, state, player);
        if (!level.destroyBlock(pos, false, player)) {
            return false;
        }

        player.awardStat(Stats.BLOCK_MINED.get(block));
        player.awardStat(Stats.ITEM_USED.get(tool.getItem()));
        if (drops) {
            player.causeFoodExhaustion(0.005F);
            state.spawnAfterBreak(level, pos, tool, true);
            for (ItemStack drop : itemDrops) {
                dropMultiplied(level, pos, drop, multiplier);
            }
        }
        return true;
    }

    private static void dropMultiplied(ServerLevel level, BlockPos pos, ItemStack original, int multiplier) {
        if (original.isEmpty()) {
            return;
        }
        int count = (int) Math.min(Integer.MAX_VALUE, (long) original.getCount() * multiplier);
        ItemStack multiplied = original.copyWithCount(count);
        ItemEntity entity = new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D,
                pos.getZ() + 0.5D, multiplied);
        entity.setDefaultPickUpDelay();
        level.addFreshEntity(entity);
    }

    private static BlockPos offsetInFacePlane(BlockPos center, Direction face, int first, int second) {
        return switch (face.getAxis()) {
            case X -> center.offset(0, first, second);
            case Y -> center.offset(first, 0, second);
            case Z -> center.offset(first, second, 0);
        };
    }

    private static boolean isUnderPlayer(Player player, BlockPos pos) {
        double minX = player.getBoundingBox().minX;
        double maxX = player.getBoundingBox().maxX;
        double minZ = player.getBoundingBox().minZ;
        double maxZ = player.getBoundingBox().maxZ;
        boolean overlapsHorizontally = pos.getX() + 1.0D > minX && pos.getX() < maxX
                && pos.getZ() + 1.0D > minZ && pos.getZ() < maxZ;
        return overlapsHorizontally && pos.getY() < player.getY();
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
