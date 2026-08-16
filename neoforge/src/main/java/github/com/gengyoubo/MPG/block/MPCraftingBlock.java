package github.com.gengyoubo.MPG.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
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
import github.com.gengyoubo.MPG.block.data.MPGBlockData;
import github.com.gengyoubo.MPG.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;

import java.util.List;

import static github.com.gengyoubo.MPG.block.MPBrewingStandBlock.getItemStacks;

@SuppressWarnings("deprecation")
public class MPCraftingBlock extends BaseEntityBlock {
    public static final MapCodec<MPCraftingBlock> CODEC = simpleCodec(MPCraftingBlock::new);

    private static final Component CONTAINER_TITLE = Component.translatable("container.crafting_manaita");


    public MPCraftingBlock() {
        this(BlockBehaviour.Properties.of().noOcclusion());
    }

    private MPCraftingBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MPGBlockData.HOOK, 8).setValue(MPGBlockData.FACING, Direction.NORTH).setValue(MPGBlockData.WALL,Direction.DOWN).setValue(MPGBlockData.TYPES,0));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        Direction wall = p_60555_.getValue(MPGBlockData.WALL);
        Direction facing = p_60555_.getValue(MPGBlockData.FACING);
        boolean hasHook = p_60555_.getValue(MPGBlockData.HOOK) != 8;
        return MPGBlockData.getWallMountedShape(wall, facing, hasHook);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState p_287732_, LootParams.@NotNull Builder p_287596_) {
        return getItemStacks(p_287732_);
    }


    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState p_52233_, @NotNull Level p_52234_, @NotNull BlockPos p_52235_, @NotNull Player p_52236_, @NotNull BlockHitResult p_52237_) {
        if (p_52234_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            p_52236_.openMenu(p_52233_.getMenuProvider(p_52234_, p_52235_));
//            p_52236_.awardStat(((ResourceLocation) ManaitaPlusStarCore.INTERACT_WITH_CRAFTING_MANAITA_TABLE.get()));
            return InteractionResult.CONSUME;
        }

    }
//
    public MenuProvider getMenuProvider(@NotNull BlockState p_52240_, @NotNull Level p_52241_, @NotNull BlockPos p_52242_) {
        return new SimpleMenuProvider((p_52229_, p_52230_, p_52231_) -> new MPGCraftingMenu(p_52229_, p_52230_, ContainerLevelAccess.create(p_52241_, p_52242_)), CONTAINER_TITLE);
    }

    public BlockState getStateForPlacement(BlockPlaceContext p_48689_) {
        return this.defaultBlockState().setValue(MPGBlockData.WALL,p_48689_.getClickedFace().getOpposite()).setValue(MPGBlockData.FACING, p_48689_.getHorizontalDirection().getOpposite());
    }


    public @NotNull BlockState rotate(BlockState p_48722_, Rotation p_48723_) {
        return p_48722_.setValue(MPGBlockData.FACING, p_48723_.rotate(p_48722_.getValue(MPGBlockData.FACING)));
    }

    public @NotNull BlockState mirror(BlockState p_48719_, Mirror p_48720_) {
        return p_48719_.rotate(p_48720_.getRotation(p_48719_.getValue(MPGBlockData.FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48725_) {
        p_48725_.add(MPGBlockData.FACING, MPGBlockData.TYPES, MPGBlockData.WALL, MPGBlockData.HOOK);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos p_153277_, @NotNull BlockState p_153278_) {
        return new MPCraftingBlockEntity(p_153277_, p_153278_);
    }
}
