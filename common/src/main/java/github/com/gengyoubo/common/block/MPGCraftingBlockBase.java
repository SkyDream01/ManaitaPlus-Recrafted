package github.com.gengyoubo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/** Loader-independent behavior shared by all three crafting blocks. */
@SuppressWarnings("deprecation")
public abstract class MPGCraftingBlockBase extends BaseEntityBlock {
    private final Supplier<? extends Item> hookItem;

    protected MPGCraftingBlockBase(Supplier<? extends Item> hookItem) {
        this(BlockBehaviour.Properties.of().noOcclusion(), hookItem);
    }

    protected MPGCraftingBlockBase(BlockBehaviour.Properties properties, Supplier<? extends Item> hookItem) {
        super(properties);
        this.hookItem = hookItem;
        registerDefaultState(stateDefinition.any()
                .setValue(MPGBlockData.HOOK, 8)
                .setValue(MPGBlockData.FACING, Direction.NORTH)
                .setValue(MPGBlockData.WALL, Direction.DOWN)
                .setValue(MPGBlockData.TYPES, 0));
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return MPGBlockData.getWallMountedShape(
                state.getValue(MPGBlockData.WALL),
                state.getValue(MPGBlockData.FACING),
                state.getValue(MPGBlockData.HOOK) != 8);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, LootParams.@NotNull Builder params) {
        return MPGTypedBlockDrops.create(state, hookItem);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                                         @NotNull BlockPos pos, @NotNull Player player,
                                                         @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        player.openMenu(state.getMenuProvider(level, pos));
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(MPGBlockData.WALL, context.getClickedFace().getOpposite())
                .setValue(MPGBlockData.FACING, context.getHorizontalDirection().getOpposite());
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
        builder.add(MPGBlockData.FACING, MPGBlockData.TYPES, MPGBlockData.WALL, MPGBlockData.HOOK);
    }
}
