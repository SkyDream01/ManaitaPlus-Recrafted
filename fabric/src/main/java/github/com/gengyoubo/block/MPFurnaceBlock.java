package github.com.gengyoubo.block;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.common.block.MPGFurnaceBlockBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.block.entity.MPFurnaceBlockEntity;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPBlockEntityCore;

import org.jetbrains.annotations.Nullable;

public class MPFurnaceBlock extends MPGFurnaceBlockBase {
    private static final MapCodec<MPFurnaceBlock> CODEC = MapCodec.unit(MPFurnaceBlock::new);

    public MPFurnaceBlock() {
        super(() -> MPBlockCore.HookBlockItem.get());
    }

    @Override
    protected @NotNull MapCodec<? extends AbstractFurnaceBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos p_153277_, @NotNull BlockState p_153278_) {
        return new MPFurnaceBlockEntity(p_153277_, p_153278_);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153273_, @NotNull BlockState p_153274_, @NotNull BlockEntityType<T> p_153275_) {
        return p_153273_.isClientSide ? null : createTickerHelper(p_153275_, MPBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), MPFurnaceBlockEntity::serverTick);
    }
}


