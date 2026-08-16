package github.com.gengyoubo.MPG.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;

@OnlyIn(Dist.CLIENT)
public class BrewingStandScreen extends AbstractContainerScreen<MPGBrewingStandMenu> {
    private static final ResourceLocation BREWING_STAND_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/brewing_stand.png");
    private static final int[] BUBBLELENGTHS = new int[]{29, 24, 20, 16, 11, 6, 0};
    private final String doubling_text;

    public BrewingStandScreen(MPGBrewingStandMenu p_98332_, Inventory p_98333_, Component p_98334_) {
        super(p_98332_, p_98333_, p_98334_);
        doubling_text = MPGConfig.brewing_doubling_value + "x";
    }

    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    public void render(@NotNull GuiGraphics p_283297_, int p_283600_, int p_282033_, float p_283410_) {
        this.renderBackground(p_283297_, p_283600_, p_282033_, p_283410_);
        super.render(p_283297_, p_283600_, p_282033_, p_283410_);
        this.renderTooltip(p_283297_, p_283600_, p_282033_);
    }


    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
        p_281635_.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        p_281635_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        p_281635_.drawString(this.font, doubling_text,titleLabelX + font.width(title), titleLabelY, 4210752);
    }

    protected void renderBg(GuiGraphics p_282963_, float p_282080_, int p_283365_, int p_283150_) {
        int i = (this.width - this.imageWidth) / 2;
        int j = (this.height - this.imageHeight) / 2;
        p_282963_.blit(BREWING_STAND_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
        int k = this.menu.getFuel();
        int l = Mth.clamp((18 * k + 20 - 1) / 20, 0, 18);
        if (l > 0) {
            p_282963_.blit(BREWING_STAND_LOCATION, i + 60, j + 44, 176, 29, l, 4);
        }

        int i1 = this.menu.getBrewingTicks();
        if (i1 > 0) {
            int j1 = (int)(28.0F * (1.0F - (float)i1 / 400.0F));
            if (j1 > 0) {
                p_282963_.blit(BREWING_STAND_LOCATION, i + 97, j + 16, 176, 0, 9, j1);
            }

            j1 = BUBBLELENGTHS[i1 / 2 % 7];
            if (j1 > 0) {
                p_282963_.blit(BREWING_STAND_LOCATION, i + 63, j + 14 + 29 - j1, 185, 29 - j1, 12, j1);
            }
        }

    }
}
