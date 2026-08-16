package github.com.gengyoubo.MPG.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.menu.MPGBaublesMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class BaublesScreen extends AbstractContainerScreen<MPGBaublesMenu> {
    private static final ResourceLocation INVENTORY_TEXTURE = ResourceLocation.fromNamespaceAndPath(MPG.MODID, "textures/gui/expanded_inventory.png");

    public BaublesScreen(MPGBaublesMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = 8;
        this.titleLabelY = -1000;
        this.inventoryLabelX = 8;
        this.inventoryLabelY = -1000;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        int left = this.leftPos;
        int top = this.topPos;
        guiGraphics.blit(INVENTORY_TEXTURE, left, top, 0, 0, this.imageWidth, this.imageHeight);
        if (this.minecraft != null && this.minecraft.player != null) {
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics,
                    left + 26,
                    top + 8,
                    left + 77,
                    top + 79,
                    30,
                    0.0F,
                    mouseX,
                    mouseY,
                    this.minecraft.player
            );
        }
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
