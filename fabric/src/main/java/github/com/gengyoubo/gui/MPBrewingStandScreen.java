package github.com.gengyoubo.gui;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.menu.MPBrewingStandMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MPBrewingStandScreen extends AbstractContainerScreen<MPBrewingStandMenu> {
    private static final ResourceLocation BREWING_STAND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
    private static final int BREW_TIME = 1;
    private static final int[] BUBBLE_LENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};
    private final String doublingText;

    public MPBrewingStandScreen(MPBrewingStandMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        doublingText = MPGConfig.brewing_doubling_value + "x";
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        guiGraphics.drawString(this.font, doublingText, this.titleLabelX + this.font.width(this.title), this.titleLabelY, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int left = (this.width - this.imageWidth) / 2;
        int top = (this.height - this.imageHeight) / 2;
        guiGraphics.blit(BREWING_STAND_LOCATION, left, top, 0, 0, this.imageWidth, this.imageHeight);
        int fuel = this.menu.getFuel();
        int fuelWidth = Mth.clamp((18 * fuel + 20 - 1) / 20, 0, 18);
        if (fuelWidth > 0) {
            guiGraphics.blit(BREWING_STAND_LOCATION, left + 60, top + 44, 176, 29, fuelWidth, 4);
        }

        int brewingTicks = this.menu.getBrewingTicks();
        if (brewingTicks > 0) {
            int progress = (int) (28.0F * (1.0F - (float) brewingTicks / BREW_TIME));
            if (progress > 0) {
                guiGraphics.blit(BREWING_STAND_LOCATION, left + 97, top + 16, 176, 0, 9, progress);
            }

            progress = BUBBLE_LENGTHS[Math.max(0, brewingTicks - 1) % 7];
            if (progress > 0) {
                guiGraphics.blit(BREWING_STAND_LOCATION, left + 63, top + 14 + 29 - progress, 185, 29 - progress, 12, progress);
            }
        }
    }
}
