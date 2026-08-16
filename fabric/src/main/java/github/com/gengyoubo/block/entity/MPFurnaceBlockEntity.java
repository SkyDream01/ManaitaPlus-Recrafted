package github.com.gengyoubo.block.entity;

import github.com.gengyoubo.common.block.entity.MPGFurnaceBlockEntityBase;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.menu.MPFurnaceMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MPFurnaceBlockEntity extends MPGFurnaceBlockEntityBase
        implements ExtendedScreenHandlerFactory<BlockPos> {
    public MPFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(MPBlockEntityCore.FURNACE_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new MPFurnaceMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MPFurnaceBlockEntity entity) {
        MPGFurnaceBlockEntityBase.serverTick(level, pos, state, entity);
    }
}
