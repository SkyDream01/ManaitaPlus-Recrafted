package github.com.gengyoubo.gui;

import github.com.gengyoubo.common.client.gui.MPGBrewingStandScreenBase;
import github.com.gengyoubo.menu.MPBrewingStandMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MPBrewingStandScreen extends MPGBrewingStandScreenBase<MPBrewingStandMenu> {
    public MPBrewingStandScreen(MPBrewingStandMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
