package github.com.gengyoubo.MPG.block;

import com.google.common.collect.Lists;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.block.data.MPGBlockData;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;

import java.util.List;

@SuppressWarnings("deprecation")
public class MPHookBlock extends Block {
    public MPHookBlock() {
        super(Properties.of().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(MPGBlockData.FACING, Direction.NORTH).setValue(MPGBlockData.TYPES,0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        Direction direction = p_60555_.getValue(MPGBlockData.FACING);
        return direction == Direction.EAST ? MPGBlockData.shapeE : direction == Direction.SOUTH ? MPGBlockData.shapeS : direction == Direction.NORTH ? MPGBlockData.shapeN : MPGBlockData.shapeW;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState p_287732_, LootParams.@NotNull Builder p_287596_) {
        ItemStack itemStack = new ItemStack(p_287732_.getBlock());
        int type = p_287732_.getValue(MPGBlockData.TYPES);
        MPGItemStackData.putInt(itemStack, MPGNBTData.ItemType, type);
        return Lists.newArrayList(itemStack);
    }


    public BlockState getStateForPlacement(BlockPlaceContext p_48689_) {
        return this.defaultBlockState().setValue(MPGBlockData.FACING, p_48689_.getClickedFace());
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_48727_) {
        return RenderShape.MODEL;
    }

    public @NotNull BlockState rotate(BlockState p_48722_, Rotation p_48723_) {
        return p_48722_.setValue(MPGBlockData.FACING, p_48723_.rotate(p_48722_.getValue(MPGBlockData.FACING)));
    }

    public @NotNull BlockState mirror(BlockState p_48719_, Mirror p_48720_) {
        return p_48719_.rotate(p_48720_.getRotation(p_48719_.getValue(MPGBlockData.FACING)));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48725_) {
        p_48725_.add(MPGBlockData.FACING, MPGBlockData.TYPES);
    }
}
