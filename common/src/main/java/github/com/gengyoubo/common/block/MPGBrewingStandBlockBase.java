package github.com.gengyoubo.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

/** Loader-independent behavior shared by all three brewing stand blocks. */
@SuppressWarnings("deprecation")
public abstract class MPGBrewingStandBlockBase extends BaseEntityBlock {
    public static final BooleanProperty[] HAS_BOTTLE = {
            BlockStateProperties.HAS_BOTTLE_0,
            BlockStateProperties.HAS_BOTTLE_1,
            BlockStateProperties.HAS_BOTTLE_2
    };

    private final Supplier<? extends Item> hookItem;

    protected MPGBrewingStandBlockBase(Supplier<? extends Item> hookItem) {
        this(BlockBehaviour.Properties.of().noOcclusion(), hookItem);
    }

    protected MPGBrewingStandBlockBase(BlockBehaviour.Properties properties, Supplier<? extends Item> hookItem) {
        super(properties);
        this.hookItem = hookItem;
        registerDefaultState(stateDefinition.any()
                .setValue(MPGBlockData.HOOK, 8)
                .setValue(MPGBlockData.FACING, Direction.NORTH)
                .setValue(MPGBlockData.WALL, Direction.DOWN)
                .setValue(MPGBlockData.TYPES, 0)
                .setValue(HAS_BOTTLE[0], Boolean.FALSE)
                .setValue(HAS_BOTTLE[1], Boolean.FALSE)
                .setValue(HAS_BOTTLE[2], Boolean.FALSE));
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
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState state, Level level,
                                                         @NotNull BlockPos pos, @NotNull Player player,
                                                         @NotNull BlockHitResult hitResult) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof MenuProvider menuProvider) {
            player.openMenu(menuProvider);
            player.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
        }
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
        builder.add(MPGBlockData.FACING, MPGBlockData.TYPES, HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2],
                MPGBlockData.WALL, MPGBlockData.HOOK);
    }

    @Override
    public void onRemove(BlockState oldState, @NotNull Level level, @NotNull BlockPos pos,
                         BlockState newState, boolean movedByPiston) {
        if (!oldState.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof BrewingStandBlockEntity brewingStand) {
                Containers.dropContents(level, pos, brewingStand);
            }
            super.onRemove(oldState, level, pos, newState, movedByPiston);
        }
    }

    @Override
    public boolean hasAnalogOutputSignal(@NotNull BlockState state) {
        return true;
    }

    @Override
    public int getAnalogOutputSignal(@NotNull BlockState state, Level level, @NotNull BlockPos pos) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(pos));
    }
}
