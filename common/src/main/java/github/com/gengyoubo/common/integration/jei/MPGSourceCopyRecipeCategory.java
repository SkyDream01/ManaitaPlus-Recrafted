package github.com.gengyoubo.common.integration.jei;

import github.com.gengyoubo.common.MPGCommon;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IRecipeExtrasBuilder;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.types.IRecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class MPGSourceCopyRecipeCategory implements IRecipeCategory<MPGSourceCopyRecipe> {
    public static final IRecipeType<MPGSourceCopyRecipe> TYPE =
            IRecipeType.create(MPGCommon.MOD_ID, "source_copying", MPGSourceCopyRecipe.class);
    private static final int GRID_X = 1;
    private static final int GRID_Y = 1;
    private static final int SLOT_SIZE = 18;
    private static final int HIDDEN_SLOT = -1000;

    private final IDrawable background;
    private final IDrawable icon;

    public MPGSourceCopyRecipeCategory(IGuiHelper guiHelper, ItemStack iconStack) {
        background = guiHelper.createBlankDrawable(118, 58);
        icon = guiHelper.createDrawableItemStack(iconStack);
    }

    @Override
    public @NotNull IRecipeType<MPGSourceCopyRecipe> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("jei.manaita_plus_recrafted.source_copying");
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
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull MPGSourceCopyRecipe recipe,
                          @NotNull IFocusGroup focuses) {
        builder.setShapeless(58, 1);
        builder.addInvisibleIngredients(RecipeIngredientRole.INPUT)
                .addItemStacks(recipe.sources()).addItemStacks(recipe.inputs());
        builder.addInvisibleIngredients(RecipeIngredientRole.OUTPUT).addItemStacks(recipe.outputs());
        builder.addSlot(RecipeIngredientRole.INPUT, GRID_X, GRID_Y)
                .addItemStacks(recipe.sources()).setStandardSlotBackground();
        builder.addSlot(RecipeIngredientRole.INPUT, HIDDEN_SLOT, HIDDEN_SLOT).addItemStacks(recipe.inputs());
        builder.addSlot(RecipeIngredientRole.OUTPUT, HIDDEN_SLOT, HIDDEN_SLOT).addItemStacks(recipe.outputs());
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, GRID_X + SLOT_SIZE, GRID_Y)
                .setStandardSlotBackground().addItemStacks(recipe.inputs());
        for (int y = 0; y < 3; y++) {
            for (int x = 0; x < 3; x++) {
                if ((x == 0 || x == 1) && y == 0) {
                    continue;
                }
                builder.addSlot(RecipeIngredientRole.RENDER_ONLY,
                        GRID_X + x * SLOT_SIZE, GRID_Y + y * SLOT_SIZE).setStandardSlotBackground();
            }
        }
        builder.addSlot(RecipeIngredientRole.RENDER_ONLY, 96, 19)
                .setOutputSlotBackground().addItemStacks(recipe.outputs());
    }

    @Override
    public void createRecipeExtras(@NotNull IRecipeExtrasBuilder builder, @NotNull MPGSourceCopyRecipe recipe,
                                   @NotNull IFocusGroup focuses) {
        builder.addRecipeArrowWidget().setPosition(66, 20);
    }

    @Override
    public Identifier getIdentifier(MPGSourceCopyRecipe recipe) {
        return Identifier.fromNamespaceAndPath(MPGCommon.MOD_ID, "source_copying");
    }
}
