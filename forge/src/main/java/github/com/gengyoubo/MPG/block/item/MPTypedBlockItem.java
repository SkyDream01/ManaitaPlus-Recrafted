package github.com.gengyoubo.MPG.block.item;

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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.block.MPHookBlock;
import github.com.gengyoubo.MPG.block.data.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPGTypeHelper;

public abstract class MPTypedBlockItem extends BlockItem {
    private final String translationPrefix;
    private final Class<? extends Block> typedBlockClass;

    protected MPTypedBlockItem(Block block, Properties properties, String translationPrefix, Class<? extends Block> typedBlockClass) {
        super(block, properties);
        this.translationPrefix = translationPrefix;
        this.typedBlockClass = typedBlockClass;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(
                translationPrefix + MPGTypeHelper.getTypes(MPGItemStackData.getInt(stack, MPGNBTData.ItemType)) + "name");
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
                                .setValue(MPGBlockData.HOOK, relativeState.getValue(MPGBlockData.TYPES))
                                .setValue(MPGBlockData.WALL, relativeState.getValue(MPGBlockData.FACING))
                                .setValue(MPGBlockData.FACING, relativeState.getValue(MPGBlockData.FACING));
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

                        SoundType soundType = placedState.getSoundType(level, blockPos, player);
                        SoundEvent placeSound = player != null
                                ? this.getPlaceSound(placedState, level, blockPos, player)
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
        if (typedBlockClass.isInstance(state.getBlock()) && MPGItemStackData.hasTag(stack)) {
            BlockState typedState = state.setValue(MPGBlockData.TYPES, MPGItemStackData.getInt(stack, MPGNBTData.ItemType));
            level.setBlock(pos, typedState, 2);
            return typedState;
        }

        return state;
    }
}
