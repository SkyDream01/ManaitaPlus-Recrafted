package github.com.gengyoubo.gui;

import github.com.gengyoubo.common.client.gui.MPGCraftingScreenBase;
import github.com.gengyoubo.menu.MPCraftingMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class MPCraftingScreen extends MPGCraftingScreenBase<MPCraftingMenu> {
    public MPCraftingScreen(MPCraftingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
