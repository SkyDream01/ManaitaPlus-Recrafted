package github.com.gengyoubo.item.tool.base;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import github.com.gengyoubo.common.util.MPText;

import java.util.List;
import java.util.function.IntConsumer;

public final class MPToolActionHelper {
    private static final HoeItem VANILLA_HOE = new HoeItem(Tiers.WOOD, new Item.Properties());
    private static final AxeItem VANILLA_AXE = new AxeItem(Tiers.WOOD, new Item.Properties());
    private static final ShovelItem VANILLA_SHOVEL = new ShovelItem(Tiers.WOOD, new Item.Properties());

    private MPToolActionHelper() {
    }

    @FunctionalInterface
    public interface RangeBlockAction {
        boolean apply(BlockPos.MutableBlockPos mutableBlockPos, BlockState blockState);
    }

    public static boolean applyInRange(UseOnContext context, int range, RangeBlockAction action) {
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        boolean changed = false;

        for (int first = -range; first <= range; first++) {
            for (int second = -range; second <= range; second++) {
                switch (context.getClickedFace().getAxis()) {
                    case X -> mutableBlockPos.set(center.getX(), center.getY() + first, center.getZ() + second);
                    case Y -> mutableBlockPos.set(center.getX() + first, center.getY(), center.getZ() + second);
                    case Z -> mutableBlockPos.set(center.getX() + first, center.getY() + second, center.getZ());
                }
                changed |= action.apply(mutableBlockPos, level.getBlockState(mutableBlockPos));
            }
        }
        return changed;
    }

    public static boolean applyHoeTillAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        return VANILLA_HOE.useOn(contextAt(context, pos)) != net.minecraft.world.InteractionResult.PASS;
    }

    public static void handleRangeOrEnchantmentUse(Level level, Player player, ItemStack itemInHand, int nextRange, IntConsumer rangeSetter) {
        if (level.isClientSide) {
            return;
        }
        if (player.isShiftKeyDown()) {
            rangeSetter.accept(nextRange);
            return;
        }

        var enchantmentRegistry = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var fortune = enchantmentRegistry.getOrThrow(Enchantments.FORTUNE);
        var silkTouch = enchantmentRegistry.getOrThrow(Enchantments.SILK_TOUCH);

        itemInHand.enchant(fortune, 10);
        String enchantName = Component.translatable("enchantments.fortune").getString();
        if (EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemInHand) <= 0) {
            itemInHand.enchant(silkTouch, 1);
            enchantName = Component.translatable("enchantments.silktouch").getString();
        }
        player.displayClientMessage(Component.literal(MPText.manaita_enchantment.formatting(itemInHand.getDisplayName().getString() + Component.translatable("info.enchantment").getString() + ": " + enchantName)), true);
    }

    public static void appendRangeAndDoublingTooltip(List<Component> tooltip, int range, boolean doubling) {
        String rangeValue = String.valueOf(range);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(Component.translatable("mode.manaita_tool").getString() + ": " + rangeValue + "x" + rangeValue + "x" + rangeValue)));
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(Component.translatable("mode.doubling").getString() + ":" + (doubling ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))));
    }

    public static boolean applyAxeActions(UseOnContext context, BlockPos pos, BlockState blockState) {
        return VANILLA_AXE.useOn(contextAt(context, pos)) != net.minecraft.world.InteractionResult.PASS;
    }

    public static boolean applyGrowPlantAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        if (!(blockState.getBlock() instanceof GrowingPlantHeadBlock growingPlantHeadBlock) || growingPlantHeadBlock.isMaxAge(blockState)) {
            return false;
        }

        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
        }
        level.playSound(player, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
        BlockState maxAgeState = growingPlantHeadBlock.getMaxAgeState(blockState);
        level.setBlockAndUpdate(pos, maxAgeState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, maxAgeState));
        damageHeldItem(context, player, itemStack);
        return true;
    }

    public static boolean applyShovelAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        return VANILLA_SHOVEL.useOn(contextAt(context, pos)) != net.minecraft.world.InteractionResult.PASS;
    }

    private static UseOnContext contextAt(UseOnContext original, BlockPos pos) {
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(pos), original.getClickedFace(), pos,
                original.isInside());
        return new UseOnContext(original.getLevel(), original.getPlayer(), original.getHand(),
                original.getItemInHand(), hit);
    }

    private static void damageHeldItem(UseOnContext context, Player player, ItemStack itemStack) {
        if (player != null) {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
        }
    }
}
