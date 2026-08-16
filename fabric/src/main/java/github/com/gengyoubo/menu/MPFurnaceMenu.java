package github.com.gengyoubo.menu;

import github.com.gengyoubo.common.menu.MPGFurnaceMenuBase;
import github.com.gengyoubo.core.MPMenuCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class MPFurnaceMenu extends MPGFurnaceMenuBase {
    @SuppressWarnings("unused")
    public MPFurnaceMenu(int containerId, Inventory inventory, BlockPos blockPos) {
        super(MPMenuCore.FurnaceManaita.get(), containerId, inventory);
    }

    public MPFurnaceMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(MPMenuCore.FurnaceManaita.get(), containerId, inventory, container, data);
    }
}
