package github.com.gengyoubo.MPG.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;

@OnlyIn(Dist.CLIENT)
public class CraftingManaitaScreen extends AbstractContainerScreen<MPGCraftingMenu> {
    private static final ResourceLocation CRAFTING_TABLE_LOCATION = ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private static final ResourceLocation UNIFORM_FONT = ResourceLocation.withDefaultNamespace("uniform");
    private final String doubling_text;

    public CraftingManaitaScreen(MPGCraftingMenu p_98448_, Inventory p_98449_, Component p_98450_) {
        super(p_98448_, p_98449_, p_98450_);
        doubling_text = MPGConfig.crafting_doubling_value + "x";
    }

    protected void init() {
        super.init();
        this.titleLabelX = 29;
    }

    public void render(@NotNull GuiGraphics p_282508_, int p_98480_, int p_98481_, float p_98482_) {
        this.renderBackground(p_282508_, p_98480_, p_98481_, p_98482_);
        super.render(p_282508_, p_98480_, p_98481_, p_98482_);
        this.renderTooltip(p_282508_, p_98480_, p_98481_);
    }

    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
        p_281635_.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 4210752, false);
        p_281635_.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 4210752, false);
        p_281635_.drawString(this.font, Component.literal(doubling_text).withStyle(style -> style.withFont(UNIFORM_FONT)), 126, 22, 4210752, false);
    }


    protected void renderBg(GuiGraphics p_283540_, float p_282132_, int p_283078_, int p_283647_) {
        int i = this.leftPos;
        int j = (this.height - this.imageHeight) / 2;
        p_283540_.blit(CRAFTING_TABLE_LOCATION, i, j, 0, 0, this.imageWidth, this.imageHeight);
    }
}
