package github.com.gengyoubo.MPG.block.entity;

import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.block.entity.MPGFurnaceBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MPFurnaceBlockEntity extends MPGFurnaceBlockEntityBase {
    public MPFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(MPGBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new MPGFurnaceMenu(containerId, inventory, this, dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MPFurnaceBlockEntity entity) {
        MPGFurnaceBlockEntityBase.serverTick(level, pos, state, entity);
    }
}
