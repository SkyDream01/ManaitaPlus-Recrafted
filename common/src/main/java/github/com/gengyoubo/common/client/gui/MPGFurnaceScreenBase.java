package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.menu.MPGFurnaceMenuBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.SmeltingRecipeBookComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class MPGFurnaceScreenBase<M extends MPGFurnaceMenuBase> extends AbstractFurnaceScreen<M> {
    private static final ResourceLocation TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/gui/container/furnace.png");
    private static final ResourceLocation LIT_PROGRESS_SPRITE =
            ResourceLocation.withDefaultNamespace("container/furnace/lit_progress");
    private static final ResourceLocation BURN_PROGRESS_SPRITE =
            ResourceLocation.withDefaultNamespace("container/furnace/burn_progress");

    private final String doublingText;

    public MPGFurnaceScreenBase(M menu, Inventory inventory, Component title, int doublingValue) {
        super(menu, new SmeltingRecipeBookComponent(), inventory, title,
                TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE);
        this.doublingText = doublingValue + "x";
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
        guiGraphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
        guiGraphics.drawString(font, getDoublingComponent(), 118, 22, 4210752, false);
    }

    protected Component getDoublingComponent() {
        return Component.literal(doublingText);
    }
}
