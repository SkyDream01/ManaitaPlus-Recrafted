package github.com.gengyoubo.block.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.block.data.MPBlockData;
import github.com.gengyoubo.common.util.MPGNBTData;

import java.util.List;

import static github.com.gengyoubo.core.MPBlockCore.HookBlock;

public class MPHookBlockItem extends BlockItem {
    public MPHookBlockItem() {
        super(HookBlock.get(), new Properties().fireResistant());
    }

    @Override
    public @NotNull Component getName(ItemStack p_41458_) {
        return Component.translatable("tile.fixed_hook." + github.com.gengyoubo.common.util.MPGItemStackData.getOrCreateTag(p_41458_).getInt(MPGNBTData.ItemType) + ".name");
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
    }

    public @NotNull InteractionResult place(BlockPlaceContext p_40577_) {
        if (!this.getBlock().isEnabled(p_40577_.getLevel().enabledFeatures())) {
            return InteractionResult.FAIL;
        } else if (!p_40577_.canPlace()) {
            return InteractionResult.FAIL;
        } else {
            BlockPlaceContext blockplacecontext = this.updatePlacementContext(p_40577_);
            if (blockplacecontext == null) {
                return InteractionResult.FAIL;
            } else {
                BlockState blockstate = this.getPlacementState(blockplacecontext);
                if (blockstate == null) {
                    return InteractionResult.FAIL;
                } else {
                    BlockPos blockpos = blockplacecontext.getClickedPos();
                    Level level = blockplacecontext.getLevel();
                    Player player = blockplacecontext.getPlayer();
                    ItemStack itemstack = blockplacecontext.getItemInHand();
                    if (blockplacecontext.getClickedFace() == Direction.UP || blockplacecontext.getClickedFace() == Direction.DOWN) {
                        return InteractionResult.FAIL;
                    }
                    if (!level.setBlock(blockpos, blockstate, 11)){
                        return InteractionResult.FAIL;
                    } else {
                        BlockState blockstate1 = level.getBlockState(blockpos);

                        if (blockstate1.is(blockstate.getBlock())) {
                            blockstate1 = this.updateBlockStateFromTag(blockpos, level, itemstack, blockstate1);
                            this.updateCustomBlockEntityTag(blockpos, level, player, itemstack, blockstate1);
                            blockstate1.getBlock().setPlacedBy(level, blockpos, blockstate1, player, itemstack);
                            if (player instanceof ServerPlayer) {
                                CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer) player, blockpos, itemstack);
                            }
                        }

                        SoundType soundtype = blockstate1.getSoundType();
                        if (p_40577_.getPlayer() != null) {
                            level.playSound(player, blockpos, this.getPlaceSound(blockstate1), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F);
                        }
                        level.gameEvent(GameEvent.BLOCK_PLACE, blockpos, GameEvent.Context.of(player, blockstate1));
                        if (player == null || !player.getAbilities().instabuild) {
                            itemstack.shrink(1);
                        }

                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }
        }
    }

    private BlockState updateBlockStateFromTag(BlockPos pos, Level level, ItemStack p_40605_, BlockState p_40606_) {
        if (p_40606_.getBlock() instanceof MPHookBlock && github.com.gengyoubo.common.util.MPGItemStackData.getTag(p_40605_) != null) {
            BlockState manaitaType = p_40606_.setValue(MPBlockData.TYPES, github.com.gengyoubo.common.util.MPGItemStackData.getTag(p_40605_).getInt(MPGNBTData.ItemType));
            level.setBlock(pos, manaitaType, 2);
            return manaitaType;
        }

        return p_40606_;
    }
}


