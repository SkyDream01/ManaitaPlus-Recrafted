package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.menu.MPGCraftingMenuBase;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.CraftingRecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.display.RecipeDisplay;
import org.jetbrains.annotations.NotNull;

/** Shared crafting screen and recipe-book behavior for all loaders. */
public class MPGCraftingScreenBase<M extends MPGCraftingMenuBase> extends AbstractContainerScreen<M>
        implements RecipeUpdateListener {
    private static final Identifier CRAFTING_TABLE_LOCATION =
            Identifier.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private final RecipeBookComponent<?> recipeBookComponent;
    private final String doublingText;
    private boolean widthTooNarrow;

    public MPGCraftingScreenBase(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        recipeBookComponent = new CraftingRecipeBookComponent(menu);
        doublingText = MPGConfigValues.crafting_doubling_value + "x";
    }

    @Override
    protected void init() {
        super.init();
        widthTooNarrow = width < 379;
        recipeBookComponent.init(width, height, minecraft, widthTooNarrow);
        leftPos = recipeBookComponent.updateScreenPosition(width, imageWidth);
        addRenderableWidget(new ImageButton(leftPos + 5, height / 2 - 49, 20, 18,
                RecipeBookComponent.RECIPE_BUTTON_SPRITES, button -> {
                    recipeBookComponent.toggleVisibility();
                    leftPos = recipeBookComponent.updateScreenPosition(width, imageWidth);
                    button.setPosition(leftPos + 5, height / 2 - 49);
                }));
        addWidget(recipeBookComponent);
        titleLabelX = 29;
    }

    @Override
    public void containerTick() {
        super.containerTick();
        recipeBookComponent.tick();
    }

    @Override
    public void extractRenderState(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        if (recipeBookComponent.isVisible() && widthTooNarrow) {
            extractBackground(graphics, mouseX, mouseY, partialTick);
        } else {
            super.extractContents(graphics, mouseX, mouseY, partialTick);
        }

        graphics.nextStratum();
        recipeBookComponent.extractRenderState(graphics, mouseX, mouseY, partialTick);
        graphics.nextStratum();
        extractCarriedItem(graphics, mouseX, mouseY);
        extractTooltip(graphics, mouseX, mouseY);
        recipeBookComponent.extractTooltip(graphics, mouseX, mouseY, hoveredSlot);
    }

    @Override
    protected void extractSlots(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        super.extractSlots(graphics, mouseX, mouseY);
        recipeBookComponent.extractGhostRecipe(graphics, true);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        graphics.text(font, title, titleLabelX, titleLabelY, -12566464, false);
        graphics.text(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, -12566464, false);
        graphics.text(font, doublingText, 126, 22, -12566464, false);
    }

    @Override
    public void extractBackground(@NotNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        super.extractBackground(graphics, mouseX, mouseY, partialTick);
        graphics.blit(RenderPipelines.GUI_TEXTURED, CRAFTING_TABLE_LOCATION, leftPos, (height - imageHeight) / 2,
                0.0F, 0.0F, imageWidth, imageHeight, 256, 256);
    }

    @Override
    public boolean keyPressed(@NotNull KeyEvent event) {
        return recipeBookComponent.keyPressed(event) || super.keyPressed(event);
    }

    @Override
    public boolean charTyped(@NotNull CharacterEvent event) {
        return recipeBookComponent.charTyped(event) || super.charTyped(event);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!widthTooNarrow || !recipeBookComponent.isVisible())
                && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(@NotNull MouseButtonEvent event, boolean doubleClick) {
        if (recipeBookComponent.mouseClicked(event, doubleClick)) {
            setFocused(recipeBookComponent);
            return true;
        }
        return widthTooNarrow && recipeBookComponent.isVisible() || super.mouseClicked(event, doubleClick);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top) {
        boolean outside = mouseX < left || mouseY < top
                || mouseX >= left + imageWidth || mouseY >= top + imageHeight;
        return recipeBookComponent.hasClickedOutside(mouseX, mouseY, leftPos, topPos,
                imageWidth, imageHeight) && outside;
    }

    @Override
    protected void slotClicked(@NotNull Slot slot, int slotId, int mouseButton, @NotNull ContainerInput containerInput) {
        super.slotClicked(slot, slotId, mouseButton, containerInput);
        recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        recipeBookComponent.recipesUpdated();
    }

    @Override
    public void fillGhostRecipe(@NotNull RecipeDisplay display) {
        recipeBookComponent.fillGhostRecipe(display);
    }

    public @NotNull RecipeBookComponent<?> getRecipeBookComponent() {
        return recipeBookComponent;
    }
}
