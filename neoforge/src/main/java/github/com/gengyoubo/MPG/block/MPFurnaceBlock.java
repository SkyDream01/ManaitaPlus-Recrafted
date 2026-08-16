package github.com.gengyoubo.MPG.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.stats.Stats;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.block.data.MPGBlockData;
import github.com.gengyoubo.MPG.block.entity.MPFurnaceBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;

import javax.annotation.Nullable;
import java.util.List;

import static github.com.gengyoubo.MPG.block.MPBrewingStandBlock.getItemStacks;

@SuppressWarnings("deprecation")
public class MPFurnaceBlock extends AbstractFurnaceBlock {
    public static final MapCodec<MPFurnaceBlock> CODEC = simpleCodec(MPFurnaceBlock::new);

    public MPFurnaceBlock() {
        this(BlockBehaviour.Properties.of().noOcclusion());
    }

    private MPFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MPGBlockData.HOOK, 8).setValue(FACING, Direction.NORTH).setValue(MPGBlockData.WALL,Direction.DOWN).setValue(LIT, Boolean.FALSE).setValue(MPGBlockData.TYPES,0));
    }

    @Override
    protected @NotNull MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    protected void openContainer(Level p_53631_, @NotNull BlockPos p_53632_, @NotNull Player p_53633_) {
        BlockEntity blockentity = p_53631_.getBlockEntity(p_53632_);
        if (blockentity instanceof MPFurnaceBlockEntity) {
            p_53633_.openMenu((MenuProvider) blockentity);
            p_53633_.awardStat(Stats.INTERACT_WITH_FURNACE);
        }
    }

    public @NotNull RenderShape getRenderShape(@NotNull BlockState p_48727_) {
        return RenderShape.INVISIBLE;
    }


    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState p_287732_, LootParams.@NotNull Builder p_287596_) {
        return getItemStacks(p_287732_);
    }

    public @NotNull BlockState getStateForPlacement(BlockPlaceContext p_48689_) {
        return this.defaultBlockState().setValue(FACING, p_48689_.getHorizontalDirection().getOpposite()).setValue(MPGBlockData.WALL,p_48689_.getClickedFace().getOpposite()).setValue(MPGBlockData.FACING, p_48689_.getHorizontalDirection().getOpposite());
    }


    @Override
    public @NotNull VoxelShape getShape(BlockState p_60555_, @NotNull BlockGetter p_60556_, @NotNull BlockPos p_60557_, @NotNull CollisionContext p_60558_) {
        Direction wall = p_60555_.getValue(MPGBlockData.WALL);
        Direction facing = p_60555_.getValue(MPGBlockData.FACING);
        boolean hasHook = p_60555_.getValue(MPGBlockData.HOOK) != 8;
        return MPGBlockData.getWallMountedShape(wall, facing, hasHook);
    }

    public @NotNull BlockState mirror(BlockState p_154351_, Mirror p_154352_) {
        return p_154351_.setValue(FACING, p_154352_.mirror(p_154351_.getValue(FACING)));
    }

    public BlockEntity newBlockEntity(@NotNull BlockPos p_153277_, @NotNull BlockState p_153278_) {
        return new MPFurnaceBlockEntity(p_153277_, p_153278_);
    }
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_48725_) {
        p_48725_.add(FACING, LIT, MPGBlockData.TYPES, MPGBlockData.WALL, MPGBlockData.HOOK);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153273_, @NotNull BlockState p_153274_, @NotNull BlockEntityType<T> p_153275_) {
        return p_153273_.isClientSide ? null : createTickerHelper(p_153275_, MPGBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), MPFurnaceBlockEntity::serverTick);
    }
}
