package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.menu.MPGFurnaceMenuBase;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractFurnaceScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.SearchRecipeBookCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeBookCategories;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MPGFurnaceScreenBase<M extends MPGFurnaceMenuBase> extends AbstractFurnaceScreen<M> {
    private static final Identifier TEXTURE =
            Identifier.withDefaultNamespace("textures/gui/container/furnace.png");
    private static final Identifier LIT_PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/furnace/lit_progress");
    private static final Identifier BURN_PROGRESS_SPRITE =
            Identifier.withDefaultNamespace("container/furnace/burn_progress");
    private static final Component FILTER_NAME = Component.translatable("gui.recipebook.toggleRecipes.smeltable");
    private static final List<RecipeBookComponent.TabInfo> TABS = List.of(
            new RecipeBookComponent.TabInfo(SearchRecipeBookCategory.FURNACE),
            new RecipeBookComponent.TabInfo(Items.PORKCHOP, RecipeBookCategories.FURNACE_FOOD),
            new RecipeBookComponent.TabInfo(Items.STONE, RecipeBookCategories.FURNACE_BLOCKS),
            new RecipeBookComponent.TabInfo(Items.LAVA_BUCKET, Items.EMERALD, RecipeBookCategories.FURNACE_MISC));

    private final String doublingText;

    public MPGFurnaceScreenBase(M menu, Inventory inventory, Component title, int doublingValue) {
        super(menu, inventory, title, FILTER_NAME, TEXTURE, LIT_PROGRESS_SPRITE, BURN_PROGRESS_SPRITE, TABS);
        this.doublingText = doublingValue + "x";
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        guiGraphics.text(font, title, titleLabelX, titleLabelY, -12566464, false);
        guiGraphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -12566464, false);
        guiGraphics.text(font, getDoublingComponent(), 118, 22, -12566464, false);
    }

    protected Component getDoublingComponent() {
        return Component.literal(doublingText);
    }
}
