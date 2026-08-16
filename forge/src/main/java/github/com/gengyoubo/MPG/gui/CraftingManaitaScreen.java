package github.com.gengyoubo.MPG.gui;

import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.common.client.gui.MPGCraftingScreenBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CraftingManaitaScreen extends MPGCraftingScreenBase<MPGCraftingMenu> {
    public CraftingManaitaScreen(MPGCraftingMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }
}
