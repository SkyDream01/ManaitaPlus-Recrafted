package github.com.gengyoubo.MPG.menu;

import github.com.gengyoubo.MPG.core.MPGMenuCore;
import github.com.gengyoubo.common.menu.MPGFurnaceMenuBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;

public class MPGFurnaceMenu extends MPGFurnaceMenuBase {
    @SuppressWarnings("unused")
    public MPGFurnaceMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData) {
        super(MPGMenuCore.FurnaceManaita.get(), containerId, inventory);
    }

    public MPGFurnaceMenu(int containerId, Inventory inventory, Container container, ContainerData data) {
        super(MPGMenuCore.FurnaceManaita.get(), containerId, inventory, container, data);
    }
}
