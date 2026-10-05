package github.com.gengyoubo.MPG.block;

import github.com.gengyoubo.common.block.MPGFurnaceBlockBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.block.entity.MPFurnaceBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;

import org.jetbrains.annotations.Nullable;

public class MPFurnaceBlock extends MPGFurnaceBlockBase {
    // Block codecs (MapCodec/simpleCodec/codec()) are gone in 26.3.

    public MPFurnaceBlock(BlockBehaviour.Properties properties) {
        super(properties, () -> MPGBlockCore.HookBlockItem.get());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos p_153277_, @NotNull BlockState p_153278_) {
        return new MPFurnaceBlockEntity(p_153277_, p_153278_);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153273_, @NotNull BlockState p_153274_, @NotNull BlockEntityType<T> p_153275_) {
        return p_153273_.isClientSide() ? null : createTickerHelper(p_153275_, MPGBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), MPFurnaceBlockEntity::serverTick);
    }
}
