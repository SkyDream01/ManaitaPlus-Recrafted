package github.com.gengyoubo.MPG.gui;

import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.common.client.gui.MPGBrewingStandScreenBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
public class BrewingStandScreen extends MPGBrewingStandScreenBase<MPGBrewingStandMenu> {
    public BrewingStandScreen(MPGBrewingStandMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
