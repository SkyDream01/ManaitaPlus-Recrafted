package github.com.gengyoubo.MPG.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;

public class MPCraftingBlockEntity extends BlockEntity {
    public MPCraftingBlockEntity(BlockPos p_155545_, BlockState p_155546_) {
        super(MPGBlockEntityCore.CRAFTING_BLOCK_ENTITY.get(), p_155545_, p_155546_);
    }
}