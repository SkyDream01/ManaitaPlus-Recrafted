package github.com.gengyoubo.block;

import com.google.common.collect.Lists;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.block.data.MPBlockData;
import github.com.gengyoubo.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.common.util.MPGNBTData;

import org.jetbrains.annotations.Nullable;
import java.util.List;

@SuppressWarnings("deprecation")
public class MPBrewingStandBlock extends BaseEntityBlock {
    private static final MapCodec<MPBrewingStandBlock> CODEC = MapCodec.unit(MPBrewingStandBlock::new);
    public static final BooleanProperty[] HAS_BOTTLE = new BooleanProperty[]{BlockStateProperties.HAS_BOTTLE_0, BlockStateProperties.HAS_BOTTLE_1, BlockStateProperties.HAS_BOTTLE_2};

    public MPBrewingStandBlock() {
        super(Properties.of().noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(MPBlockData.HOOK, 8).setValue(MPBlockData.FACING, Direction.NORTH).setValue(MPBlockData.WALL,Direction.DOWN).setValue(MPBlockData.TYPES,0).setValue(HAS_BOTTLE[0], Boolean.FALSE).setValue(HAS_BOTTLE[1], Boolean.FALSE).setValue(HAS_BOTTLE[2], Boolean.FALSE));
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Deprecated
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
        return getItemStacks(p_287732_);
    }

    static @NotNull List<ItemStack> getItemStacks(BlockState p_287732_) {
        List<ItemStack> list = Lists.newArrayList();
        ItemStack itemStack = new ItemStack(p_287732_.getBlock());
        github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, MPGNBTData.ItemType, p_287732_.getValue(MPBlockData.TYPES));
        list.add(itemStack);
        int hook = p_287732_.getValue(MPBlockData.HOOK);
        if (hook != 8) {
            itemStack = new ItemStack(MPBlockCore.HookBlockItem.get());
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, MPGNBTData.ItemType, hook);
            list.add(itemStack);
        }
        return list;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(@NotNull BlockState p_50930_, Level p_50931_, @NotNull BlockPos p_50932_, @NotNull Player p_50933_, @NotNull BlockHitResult p_50935_) {
        if (p_50931_.isClientSide) {
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockentity = p_50931_.getBlockEntity(p_50932_);
            if (blockentity instanceof MPBrewingStandBlockEntity) {
                p_50933_.openMenu((MPBrewingStandBlockEntity)blockentity);
                p_50933_.awardStat(Stats.INTERACT_WITH_BREWINGSTAND);
            }

            return InteractionResult.CONSUME;
        }
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
        p_48725_.add(MPBlockData.FACING, MPBlockData.TYPES,HAS_BOTTLE[0], HAS_BOTTLE[1], HAS_BOTTLE[2], MPBlockData.WALL, MPBlockData.HOOK);
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos p_153277_, @NotNull BlockState p_153278_) {
        return new MPBrewingStandBlockEntity(p_153277_, p_153278_);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_152694_, @NotNull BlockState p_152695_, @NotNull BlockEntityType<T> p_152696_) {
        return p_152694_.isClientSide ? null : createTickerHelper(p_152696_, MPBlockEntityCore.BREWING_BLOCK_ENTITY.get(), MPBrewingStandBlockEntity::serverTick);
    }


    public void onRemove(BlockState p_50937_, @NotNull Level p_50938_, @NotNull BlockPos p_50939_, BlockState p_50940_, boolean p_50941_) {
        if (!p_50937_.is(p_50940_.getBlock())) {
            BlockEntity blockentity = p_50938_.getBlockEntity(p_50939_);
            if (blockentity instanceof BrewingStandBlockEntity) {
                Containers.dropContents(p_50938_, p_50939_, (BrewingStandBlockEntity)blockentity);
            }

            super.onRemove(p_50937_, p_50938_, p_50939_, p_50940_, p_50941_);
        }
    }

    public boolean hasAnalogOutputSignal(@NotNull BlockState p_50919_) {
        return true;
    }

    public int getAnalogOutputSignal(@NotNull BlockState p_50926_, Level p_50927_, @NotNull BlockPos p_50928_) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(p_50927_.getBlockEntity(p_50928_));
    }

    public boolean isPathfindable(@NotNull BlockState p_50921_, @NotNull BlockGetter p_50922_, @NotNull BlockPos p_50923_, @NotNull PathComputationType p_50924_) {
        return false;
    }
}



