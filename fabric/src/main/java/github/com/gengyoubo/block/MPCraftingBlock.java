package github.com.gengyoubo.block;

import com.mojang.serialization.MapCodec;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.block.data.MPBlockData;
import github.com.gengyoubo.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.menu.MPCraftingMenu;

import java.util.List;

import static github.com.gengyoubo.block.MPBrewingStandBlock.getItemStacks;

@SuppressWarnings("deprecation")
public class MPCraftingBlock extends BaseEntityBlock {
    private static final MapCodec<MPCraftingBlock> CODEC = MapCodec.unit(MPCraftingBlock::new);

    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting_manaita");


    public MPCraftingBlock() {
        super(BlockBehaviour.Properties.of().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(MPBlockData.HOOK, 8).setValue(MPBlockData.FACING, Direction.NORTH).setValue(MPBlockData.WALL,Direction.DOWN).setValue(MPBlockData.TYPES,0));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        Direction wall = p_60555_.getValue(MPBlockData.WALL);
        Direction facing = p_60555_.getValue(MPBlockData.FACING);
        boolean hasHook = p_60555_.getValue(MPBlockData.HOOK) != 8;
        return MPBlockData.getWallMountedShape(wall, facing, hasHook);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState p_287732_, LootParams.@NotNull Builder p_287596_) {
        List<ItemStack> list = com.google.common.collect.Lists.newArrayList();
        ItemStack mainBlock = new ItemStack(this);
        github.com.gengyoubo.common.util.MPGItemStackData.putInt(mainBlock, github.com.gengyoubo.common.util.MPGNBTData.ItemType, p_287732_.getValue(MPBlockData.TYPES));
        list.add(mainBlock);
        int hook = p_287732_.getValue(MPBlockData.HOOK);
        if (hook != 8) {
            ItemStack itemStack = new ItemStack(github.com.gengyoubo.core.MPBlockCore.HookBlockItem.get());
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, github.com.gengyoubo.common.util.MPGNBTData.ItemType, hook);
            list.add(itemStack);
        }
        return list;
    }


    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState p_52233_, @NotNull Level p_52234_, @NotNull BlockPos p_52235_, @NotNull Player p_52236_, @NotNull BlockHitResult p_52238_) {
        if (p_52234_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            p_52236_.openMenu(p_52233_.getMenuProvider(p_52234_, p_52235_));
            return InteractionResult.CONSUME;
        }
    }

    @Override
    public MenuProvider getMenuProvider(@NotNull BlockState p_52240_, @NotNull Level p_52241_, @NotNull BlockPos p_52242_) {
        return new ExtendedScreenHandlerFactory<BlockPos>() {
            @Override
            public @NotNull Component getDisplayName() {
                return CONTAINER_TITLE;
            }

            @Override
            public BlockPos getScreenOpeningData(net.minecraft.server.level.ServerPlayer player) {
                return p_52242_;
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull net.minecraft.world.entity.player.Inventory inventory, @NotNull Player player) {
                return new MPCraftingMenu(containerId, inventory, p_52242_);
            }
        };
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_48689_) {
        return this.defaultBlockState().setValue(MPBlockData.WALL,p_48689_.getClickedFace().getOpposite()).setValue(MPBlockData.FACING, p_48689_.getHorizontalDirection().getOpposite());
    }


    public @NotNull BlockState rotate(BlockState p_48722_, Rotation p_48723_) {
        return p_48722_.setValue(MPBlockData.FACING, p_48723_.rotate(p_48722_.getValue(MPBlockData.FACING)));
    }

    public @NotNull BlockState mirror(BlockState p_48719_, Mirror p_48720_) {
        return p_48719_.rotate(p_48720_.getRotation(p_48719_.getValue(MPBlockData.FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48725_) {
        p_48725_.add(MPBlockData.FACING, MPBlockData.TYPES, MPBlockData.WALL, MPBlockData.HOOK);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos p_153277_, @NotNull BlockState p_153278_) {
        return new MPCraftingBlockEntity(p_153277_, p_153278_);
    }
}


