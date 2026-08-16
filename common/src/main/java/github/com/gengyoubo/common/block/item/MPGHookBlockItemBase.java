package github.com.gengyoubo.common.block.item;

import github.com.gengyoubo.common.block.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
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
import org.jetbrains.annotations.NotNull;

public class MPGHookBlockItemBase extends BlockItem {
    private final Class<? extends Block> hookBlockClass;

    public MPGHookBlockItemBase(Block block, Class<? extends Block> hookBlockClass) {
        super(block, new Properties().fireResistant());
        this.hookBlockClass = hookBlockClass;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable("tile.fixed_hook."
                + MPGItemStackData.getInt(stack, MPGNBTData.ItemType) + ".name");
    }

    @Override
    public @NotNull InteractionResult place(BlockPlaceContext context) {
        if (!getBlock().isEnabled(context.getLevel().enabledFeatures()) || !context.canPlace()) {
            return InteractionResult.FAIL;
        }

        BlockPlaceContext placementContext = updatePlacementContext(context);
        if (placementContext == null || placementContext.getClickedFace() == Direction.UP
                || placementContext.getClickedFace() == Direction.DOWN) {
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
        if (player != null) {
            SoundEvent sound = getPlacementSound(placedState, level, pos, player);
            level.playSound(player, pos, sound, SoundSource.BLOCKS,
                    (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
        }
        level.gameEvent(GameEvent.BLOCK_PLACE, pos, GameEvent.Context.of(player, placedState));
        if (player == null || !player.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    protected SoundType getPlacementSoundType(BlockState state, Level level, BlockPos pos, Player player) {
        return state.getSoundType();
    }

    protected SoundEvent getPlacementSound(BlockState state, Level level, BlockPos pos, Player player) {
        return getPlaceSound(state);
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack stack, BlockState state) {
        if (hookBlockClass.isInstance(state.getBlock()) && MPGItemStackData.hasTag(stack)) {
            BlockState typedState = state.setValue(
                    MPGBlockData.TYPES, MPGItemStackData.getInt(stack, MPGNBTData.ItemType));
            level.setBlock(pos, typedState, 2);
            return typedState;
        }
        return state;
    }
}
