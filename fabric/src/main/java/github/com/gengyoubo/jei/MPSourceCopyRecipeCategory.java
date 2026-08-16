package github.com.gengyoubo.jei;

import github.com.gengyoubo.core.MPItemCore;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MPSourceCopyRecipeCategory implements IRecipeCategory<MPSourceCopyJeiRecipe> {
    public static final RecipeType<MPSourceCopyJeiRecipe> TYPE =
            RecipeType.create("manaita_plus_general", "source_copying", MPSourceCopyJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable arrow;

    public MPSourceCopyRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(118, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MPItemCore.ManaitaSource.get()));
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public @NotNull RecipeType<MPSourceCopyJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.manaita_plus_general.source_copying");
    }

    @Override
    @SuppressWarnings("removal")
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, MPSourceCopyJeiRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 8, 18)
                .addItemStack(new ItemStack(MPItemCore.ManaitaSource.get()))
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.INPUT, 32, 18)
                .addItemStack(recipe.input())
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.OUTPUT, 86, 18)
                .addItemStack(recipe.output())
                .setOutputSlotBackground();
    }

    @Override
    public void draw(MPSourceCopyJeiRecipe recipe, @NotNull mezz.jei.api.gui.ingredient.IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        arrow.draw(guiGraphics, 56, 19);
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return;
        }
        guiGraphics.drawString(minecraft.font, Component.translatable("jei.manaita_plus_general.source_copying.source_result", recipe.sourceResultCount()), 86, 2, 0x404040, false);
    }

    @Override
    public ResourceLocation getRegistryName(MPSourceCopyJeiRecipe recipe) {
        return recipe.input().getItemHolder().unwrapKey().orElseThrow().location();
    }
}
