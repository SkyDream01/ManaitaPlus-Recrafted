package github.com.gengyoubo.common.block.item;

import github.com.gengyoubo.common.block.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPGTypeHelper;
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

public abstract class MPGTypedBlockItemBase extends BlockItem {
    private final String translationPrefix;
    private final Class<? extends Block> typedBlockClass;
    private final Class<? extends Block> hookBlockClass;

    protected MPGTypedBlockItemBase(Block block, Properties properties, String translationPrefix,
                                    Class<? extends Block> typedBlockClass, Class<? extends Block> hookBlockClass) {
        super(block, properties);
        this.translationPrefix = translationPrefix;
        this.typedBlockClass = typedBlockClass;
        this.hookBlockClass = hookBlockClass;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(translationPrefix
                + MPGTypeHelper.getTypes(MPGItemStackData.getInt(stack, MPGNBTData.ItemType)) + "name");
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        if (!getBlock().isEnabled(context.getLevel().enabledFeatures()) || !context.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockPlaceContext placementContext = updatePlacementContext(context);
        if (placementContext == null) {
            return InteractionResult.FAIL;
        }

        BlockState state = getPlacementState(placementContext);
        if (state == null) {
            return InteractionResult.FAIL;
        }

        BlockPos pos = placementContext.getClickedPos();
        Level level = placementContext.getLevel();
        Player player = placementContext.getPlayer();
        ItemStack itemStack = placementContext.getItemInHand();
        BlockPos relativePos = pos.relative(placementContext.getClickedFace().getOpposite());
        BlockState relativeState = level.getBlockState(relativePos);

        if (hookBlockClass.isInstance(relativeState.getBlock())) {
            state = state
                    .setValue(MPGBlockData.HOOK, relativeState.getValue(MPGBlockData.TYPES))
                    .setValue(MPGBlockData.WALL, relativeState.getValue(MPGBlockData.FACING))
                    .setValue(MPGBlockData.FACING, relativeState.getValue(MPGBlockData.FACING));
            pos = relativePos;
        } else if ((placementContext.getClickedFace() != Direction.UP
                && placementContext.getClickedFace() != Direction.DOWN)
                || !level.isUnobstructed(relativeState, context.getClickedPos(),
                player == null ? CollisionContext.empty() : CollisionContext.of(player))) {
            return InteractionResult.FAIL;
        }

        if (!level.setBlock(pos, state, 11)) {
            return InteractionResult.FAIL;
        }

        BlockState placedState = level.getBlockState(pos);
        if (placedState.is(state.getBlock())) {
            placedState = updateBlockStateFromTag(pos, level, itemStack, placedState);
            updateCustomBlockEntityTag(pos, level, player, itemStack, placedState);
            placedState.getBlock().setPlacedBy(level, pos, placedState, player, itemStack);
            if (player instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, pos, itemStack);
            }
        }

        SoundType soundType = getPlacementSoundType(placedState, level, pos, player);
        SoundEvent placeSound = player != null
                ? getPlacementSound(placedState, level, pos, player)
                : soundType.getPlaceSound();
        level.playSound(player, pos, placeSound, SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, placedState));
        if (player == null || !player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
        return !mustSurvive() || state.canSurvive(context.getLevel(), context.getClickedPos());
    }

    protected SoundType getPlacementSoundType(BlockState state, Level level, BlockPos pos, Player player) {
        return state.getSoundType();
    }

    protected SoundEvent getPlacementSound(BlockState state, Level level, BlockPos pos, Player player) {
        return getPlaceSound(state);
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
        if (typedBlockClass.isInstance(state.getBlock()) && MPGItemStackData.hasTag(stack)) {
            BlockState typedState = state.setValue(
                    MPGBlockData.TYPES, MPGItemStackData.getInt(stack, MPGNBTData.ItemType));
            level.setBlock(pos, typedState, 2);
            return typedState;
        }
        return state;
    }
}
