package github.com.gengyoubo.gui;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.menu.MPFurnaceMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MPFurnaceScreen extends AbstractFurnaceScreen<MPFurnaceMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");
    private final String doublingText;

    public MPFurnaceScreen(MPFurnaceMenu menu, Inventory inventory, Component title) {
        super(menu, new SmeltingRecipeBookComponent(), inventory, title, TEXTURE, TEXTURE, TEXTURE);
        doublingText = MPGConfig.furnace_doubling_value + "x";
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        guiGraphics.drawString(this.font, doublingText, 118, 22, 4210752, false);
    }
}
