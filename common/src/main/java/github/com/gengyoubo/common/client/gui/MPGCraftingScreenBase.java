package github.com.gengyoubo.common.client.gui;

import github.com.gengyoubo.common.config.MPGConfigValues;
import github.com.gengyoubo.common.menu.MPGCraftingMenuBase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

/** Shared crafting screen and recipe-book behavior for all loaders. */
public class MPGCraftingScreenBase<M extends MPGCraftingMenuBase> extends AbstractContainerScreen<M>
        implements RecipeUpdateListener {
    private static final ResourceLocation CRAFTING_TABLE_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/gui/container/crafting_table.png");
    private final RecipeBookComponent recipeBookComponent = new RecipeBookComponent();
    private final String doublingText;
    private boolean widthTooNarrow;

    public MPGCraftingScreenBase(M menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        doublingText = MPGConfigValues.crafting_doubling_value + "x";
    }

    @Override
    protected void init() {
        super.init();
        widthTooNarrow = width < 379;
        recipeBookComponent.init(width, height, minecraft, widthTooNarrow, menu);
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
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        if (recipeBookComponent.isVisible() && widthTooNarrow) {
            renderBackground(graphics, mouseX, mouseY, partialTick);
            recipeBookComponent.render(graphics, mouseX, mouseY, partialTick);
        } else {
            super.render(graphics, mouseX, mouseY, partialTick);
            recipeBookComponent.render(graphics, mouseX, mouseY, partialTick);
            recipeBookComponent.renderGhostRecipe(graphics, leftPos, topPos, true, partialTick);
        }

        renderTooltip(graphics, mouseX, mouseY);
        recipeBookComponent.renderTooltip(graphics, leftPos, topPos, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
        graphics.drawString(font, doublingText, 126, 22, 4210752, false);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTick, int mouseX, int mouseY) {
        graphics.blit(CRAFTING_TABLE_LOCATION, leftPos, (height - imageHeight) / 2,
                0, 0, imageWidth, imageHeight);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return recipeBookComponent.keyPressed(keyCode, scanCode, modifiers)
                || super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        return recipeBookComponent.charTyped(codePoint, modifiers) || super.charTyped(codePoint, modifiers);
    }

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!widthTooNarrow || !recipeBookComponent.isVisible())
                && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (recipeBookComponent.mouseClicked(mouseX, mouseY, button)) {
            setFocused(recipeBookComponent);
            return true;
        }
        return widthTooNarrow && recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean outside = mouseX < left || mouseY < top
                || mouseX >= left + imageWidth || mouseY >= top + imageHeight;
        return recipeBookComponent.hasClickedOutside(mouseX, mouseY, leftPos, topPos,
                imageWidth, imageHeight, button) && outside;
    }

    @Override
    protected void slotClicked(@NotNull Slot slot, int slotId, int mouseButton, @NotNull ClickType clickType) {
        super.slotClicked(slot, slotId, mouseButton, clickType);
        recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        recipeBookComponent.recipesUpdated();
    }

    @Override
    public @NotNull RecipeBookComponent getRecipeBookComponent() {
        return recipeBookComponent;
    }
}
