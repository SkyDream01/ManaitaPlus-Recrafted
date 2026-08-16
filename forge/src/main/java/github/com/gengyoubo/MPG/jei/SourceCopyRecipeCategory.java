package github.com.gengyoubo.MPG.jei;

import github.com.gengyoubo.MPG.core.MPGItemCore;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SourceCopyRecipeCategory implements IRecipeCategory<SourceCopyJeiRecipe> {
    public static final RecipeType<SourceCopyJeiRecipe> TYPE =
            RecipeType.create("manaita_plus_general", "source_copying", SourceCopyJeiRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;

    public SourceCopyRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(118, 54);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(MPGItemCore.ManaitaSource.get()));
    }

    @Override
    public @NotNull RecipeType<SourceCopyJeiRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.manaita_plus_general.source_copying");
    }

    @Override
    public int getWidth() {
        return background.getWidth();
    }

    @Override
    public int getHeight() {
        return background.getHeight();
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull SourceCopyJeiRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 4, 18).addItemStack(recipe.source());
        builder.addSlot(RecipeIngredientRole.INPUT, 40, 18).addItemStack(recipe.input());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 94, 18).addItemStack(recipe.output());
    }

    @Override
    public void draw(@NotNull SourceCopyJeiRecipe recipe, @NotNull IRecipeSlotsView recipeSlotsView, @NotNull GuiGraphics guiGraphics, double mouseX, double mouseY) {
        background.draw(guiGraphics, 0, 0);
        guiGraphics.drawString(net.minecraft.client.Minecraft.getInstance().font, ">", 72, 22, 0x404040, false);
        guiGraphics.drawString(
                net.minecraft.client.Minecraft.getInstance().font,
                Component.translatable("jei.manaita_plus_general.source.info.2", recipe.output().getCount()),
                4,
                42,
                0x404040,
                false
        );
    }
}
