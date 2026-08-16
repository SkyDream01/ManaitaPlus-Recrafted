package github.com.gengyoubo.block;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.common.block.MPGBrewingStandBlockBase;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MPBrewingStandBlock extends MPGBrewingStandBlockBase {
    private static final MapCodec<MPBrewingStandBlock> CODEC = MapCodec.unit(MPBrewingStandBlock::new);

    public MPBrewingStandBlock() {
        super(() -> MPBlockCore.HookBlockItem.get());
    }

    @Override
    protected @NotNull MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MPBrewingStandBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : createTickerHelper(
                type, MPBlockEntityCore.BREWING_BLOCK_ENTITY.get(), MPBrewingStandBlockEntity::serverTick);
    }
}
