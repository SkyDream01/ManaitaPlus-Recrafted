package github.com.gengyoubo.block.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.block.data.MPBlockData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPGTypeHelper;

import java.util.List;

public abstract class MPTypedBlockItem extends BlockItem {
    private final String translationPrefix;
    private final Class<? extends Block> typedBlockClass;

    protected MPTypedBlockItem(Block block, Properties properties, String translationPrefix, Class<? extends Block> typedBlockClass) {
        super(block, properties);
        this.translationPrefix = translationPrefix;
        this.typedBlockClass = typedBlockClass;
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        return Component.translatable(
                translationPrefix + MPGTypeHelper.getTypes(github.com.gengyoubo.common.util.MPGItemStackData.getOrCreateTag(stack).getInt(MPGNBTData.ItemType)) + "name");
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        if (!this.getBlock().isEnabled(context.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!context.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockPlaceContext = this.updatePlacementContext(context);
            if (blockPlaceContext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockState = this.getPlacementState(blockPlaceContext);
                if (blockState == null) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockPos = blockPlaceContext.getClickedPos();
                    Level level = blockPlaceContext.getLevel();
                    Player player = blockPlaceContext.getPlayer();
                    ItemStack itemStack = blockPlaceContext.getItemInHand();
                    BlockPos relative = blockPos.relative(blockPlaceContext.getClickedFace().getOpposite());
                    BlockState relativeState = level.getBlockState(relative);
                    if (relativeState.getBlock() instanceof MPHookBlock) {
                        blockState = blockState
                                .setValue(MPBlockData.HOOK, relativeState.getValue(MPBlockData.TYPES))
                                .setValue(MPBlockData.WALL, relativeState.getValue(MPBlockData.FACING))
                                .setValue(MPBlockData.FACING, relativeState.getValue(MPBlockData.FACING));
                        blockPos = relative;
                    } else if (blockPlaceContext.getClickedFace() != Direction.UP
                            && blockPlaceContext.getClickedFace() != Direction.DOWN
                            || !context.getLevel().isUnobstructed(relativeState, context.getClickedPos(),
                            player == null ? CollisionContext.empty() : CollisionContext.of(player))) {
                        return InteractionResult.FAIL;
                    }
                    if (!level.setBlock(blockPos, blockState, 11)) {
                        return InteractionResult.FAIL;
                    } else {
                        BlockState placedState = level.getBlockState(blockPos);

                        if (placedState.is(blockState.getBlock())) {
                            placedState = this.updateBlockStateFromTag(blockPos, level, itemStack, placedState);
                            this.updateCustomBlockEntityTag(blockPos, level, player, itemStack, placedState);
                            placedState.getBlock().setPlacedBy(level, blockPos, placedState, player, itemStack);
                            if (player instanceof ServerPlayer serverPlayer) {
                                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, blockPos, itemStack);
                            }
                        }

                        SoundType soundType = placedState.getSoundType();
                        SoundEvent placeSound = player != null
                                ? this.getPlaceSound(placedState)
                                : soundType.getPlaceSound();
                        level.playSound(player, blockPos, placeSound, SoundSource.BLOCKS,
                                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                        level.gameEvent(GameEvent.BLOCK_PLACE, blockPos, GameEvent.Context.of(player, placedState));
                        if (player == null || !player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }

                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }
    }

    @Override
    protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
        return !this.mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos());
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
        if (typedBlockClass.isInstance(state.getBlock()) && github.com.gengyoubo.common.util.MPGItemStackData.getTag(stack) != null) {
            BlockState typedState = state.setValue(MPBlockData.TYPES, github.com.gengyoubo.common.util.MPGItemStackData.getTag(stack).getInt(MPGNBTData.ItemType));
            level.setBlock(pos, typedState, 2);
            return typedState;
        }

        return state;
    }
}


