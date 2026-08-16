package github.com.gengyoubo.MPG.block.entity;

import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.common.block.entity.MPGCraftingBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MPCraftingBlockEntity extends MPGCraftingBlockEntityBase {
    public MPCraftingBlockEntity(BlockPos pos, BlockState state) {
        super(MPGBlockEntityCore.CRAFTING_BLOCK_ENTITY.get(), pos, state);
    }
}
