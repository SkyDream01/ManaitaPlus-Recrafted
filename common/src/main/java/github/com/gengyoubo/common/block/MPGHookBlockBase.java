package github.com.gengyoubo.common.block;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
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

import java.util.List;

@SuppressWarnings("deprecation")
public class MPGHookBlockBase extends Block {
    public MPGHookBlockBase() {
        super(Properties.of().noOcclusion());
        registerDefaultState(stateDefinition.any()
                .setValue(MPGBlockData.FACING, Direction.NORTH)
                .setValue(MPGBlockData.TYPES, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction direction = state.getValue(MPGBlockData.FACING);
        return switch (direction) {
            case EAST -> MPGBlockData.shapeE;
            case SOUTH -> MPGBlockData.shapeS;
            case NORTH -> MPGBlockData.shapeN;
            default -> MPGBlockData.shapeW;
        };
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.@NotNull Builder params) {
        ItemStack itemStack = new ItemStack(state.getBlock());
        MPGItemStackData.putInt(itemStack, MPGNBTData.ItemType, state.getValue(MPGBlockData.TYPES));
        return List.of(itemStack);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(MPGBlockData.FACING, context.getClickedFace());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(MPGBlockData.FACING, rotation.rotate(state.getValue(MPGBlockData.FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(MPGBlockData.FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(MPGBlockData.FACING, MPGBlockData.TYPES);
    }
}
