package github.com.gengyoubo.MPG.block;

import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.common.block.MPGBrewingStandBlockBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MPBrewingStandBlock extends MPGBrewingStandBlockBase {
    // Block codecs (MapCodec/simpleCodec/codec()) are gone in 26.3.

    public MPBrewingStandBlock(BlockBehaviour.Properties properties) {
        super(properties, () -> MPGBlockCore.HookBlockItem.get());
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new MPBrewingStandBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return level.isClientSide() ? null : createTickerHelper(
                type, MPGBlockEntityCore.BREWING_BLOCK_ENTITY.get(), MPBrewingStandBlockEntity::serverTick);
    }
}
