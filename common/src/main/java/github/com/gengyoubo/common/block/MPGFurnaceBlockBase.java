package github.com.gengyoubo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
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

/** Loader-independent behavior shared by all three furnace blocks. */
@SuppressWarnings("deprecation")
public abstract class MPGFurnaceBlockBase extends AbstractFurnaceBlock {
    private final Supplier<? extends Item> hookItem;

    protected MPGFurnaceBlockBase(Supplier<? extends Item> hookItem) {
        this(BlockBehaviour.Properties.of().noOcclusion(), hookItem);
    }

    protected MPGFurnaceBlockBase(BlockBehaviour.Properties properties, Supplier<? extends Item> hookItem) {
        super(properties);
        this.hookItem = hookItem;
        registerDefaultState(stateDefinition.any()
                .setValue(MPGBlockData.HOOK, 8)
                .setValue(FACING, Direction.NORTH)
                .setValue(MPGBlockData.WALL, Direction.DOWN)
                .setValue(LIT, Boolean.FALSE)
                .setValue(MPGBlockData.TYPES, 0));
    }

    @Override
    protected void openContainer(Level level, @NotNull BlockPos pos, @NotNull Player player) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MenuProvider menuProvider) {
            player.openMenu(menuProvider);
            player.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, @NotNull Level level,
                                                         @NotNull BlockPos pos, @NotNull Player player,
                                                         @NotNull BlockHitResult hitResult) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        openContainer(level, pos, player);
        return InteractionResult.CONSUME;
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
    public @NotNull BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return defaultBlockState()
                .setValue(FACING, facing)
                .setValue(MPGBlockData.WALL, context.getClickedFace().getOpposite())
                .setValue(MPGBlockData.FACING, facing);
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
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT, MPGBlockData.TYPES, MPGBlockData.WALL, MPGBlockData.HOOK);
    }
}
