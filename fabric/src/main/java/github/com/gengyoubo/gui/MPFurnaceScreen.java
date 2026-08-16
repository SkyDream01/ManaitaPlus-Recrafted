package github.com.gengyoubo.gui;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.common.client.gui.MPGFurnaceScreenBase;
import github.com.gengyoubo.menu.MPFurnaceMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MPFurnaceScreen extends MPGFurnaceScreenBase<MPFurnaceMenu> {
    public MPFurnaceScreen(MPFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, MPGConfig.furnace_doubling_value);
    }
}
