package github.com.gengyoubo.MPG.gui;

import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.client.gui.MPGFurnaceScreenBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public class FurnaceManaitaScreen extends MPGFurnaceScreenBase<MPGFurnaceMenu> {
    public FurnaceManaitaScreen(MPGFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title, MPGConfig.furnace_doubling_value);
    }
}
