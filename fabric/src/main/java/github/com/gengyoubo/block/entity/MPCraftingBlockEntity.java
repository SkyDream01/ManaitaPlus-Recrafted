package github.com.gengyoubo.block.entity;

import github.com.gengyoubo.common.block.entity.MPGCraftingBlockEntityBase;
import github.com.gengyoubo.core.MPBlockEntityCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class MPCraftingBlockEntity extends MPGCraftingBlockEntityBase {
    public MPCraftingBlockEntity(BlockPos pos, BlockState state) {
        super(MPBlockEntityCore.CRAFTING_BLOCK_ENTITY.get(), pos, state);
    }
}
