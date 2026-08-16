package github.com.gengyoubo.MPG.jei;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SourceCopyRecipeCategory implements IRecipeCategory<SourceCopyJeiRecipe> {
    public static final RecipeType<SourceCopyJeiRecipe> TYPE =
            RecipeType.create("manaita_plus_general", "source_copying", SourceCopyJeiRecipe.class);
    private static final int GRID_X = 1;
    private static final int GRID_Y = 1;
    private static final int SLOT_SIZE = 18;
    private static final int ARROW_X = 66;
    private static final int ARROW_Y = 20;
    private static final int OUTPUT_X = 96;
    private static final int OUTPUT_Y = 19;
    private static final int RESULT_COUNT_X = 101;
    private static final int RESULT_COUNT_Y = 7;
    private static final int SHAPELESS_X = 58;
    private static final int SHAPELESS_Y = 1;
    private static final int HIDDEN_SLOT_X = -1000;
    private static final int HIDDEN_SLOT_Y = -1000;

    private final IDrawable background;
    private final IDrawable icon;

    public SourceCopyRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(118, 58);
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
        builder.setShapeless(SHAPELESS_X, SHAPELESS_Y);
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addItemStacks(recipe.sources())
                .addItemStacks(recipe.inputs());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT)
                .addItemStacks(recipe.outputs());

        builder.addSlot(RecipeIngredientRole.INPUT, GRID_X, GRID_Y)
                .addItemStack(new ItemStack(MPGItemCore.ManaitaSource.get()))
                .setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.INPUT, HIDDEN_SLOT_X, HIDDEN_SLOT_Y)
                .addItemStacks(recipe.inputs());
        builder.addSlot(RecipeIngredientRole.OUTPUT, HIDDEN_SLOT_X, HIDDEN_SLOT_Y)
                .addItemStacks(recipe.outputs());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, GRID_X + SLOT_SIZE, GRID_Y)
                .setStandardSlotBackground()
                .addItemStacks(recipe.inputs());

        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if ((x == 0 && y == 0) || (x == 1 && y == 0)) {
                    continue;
                }
                addEmptyCraftingGridSlot(builder, x, y);
            }
        }

        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, OUTPUT_X, OUTPUT_Y)
                .setOutputSlotBackground()
                .addItemStacks(recipe.outputs());
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull SourceCopyJeiRecipe recipe, @NotNull IFocusGroup focuses) {
        builder.addRecipeArrow().setPosition(ARROW_X, ARROW_Y);
        builder.addText(Component.literal("x" + recipe.multiplier()), RESULT_COUNT_X, RESULT_COUNT_Y);
    }

    private static void addEmptyCraftingGridSlot(IRecipeLayoutBuilder builder, int x, int y) {
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, GRID_X + x * SLOT_SIZE, GRID_Y + y * SLOT_SIZE)
                .setStandardSlotBackground();
    }

    @Override
    public ResourceLocation getRegistryName(SourceCopyJeiRecipe recipe) {
        return ResourceLocation.fromNamespaceAndPath(MPG.MODID, "source_copying");
    }
}
