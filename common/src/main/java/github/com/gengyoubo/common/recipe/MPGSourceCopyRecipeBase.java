package github.com.gengyoubo.common.recipe;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.IntSupplier;
import java.util.function.Supplier;

/** Shared source-copy custom recipe. Loader classes only supply registered objects/config. */
public abstract class MPGSourceCopyRecipeBase extends CustomRecipe {
    private final Supplier<? extends Item> sourceItem;
    private final IntSupplier multiplier;
    private final Supplier<? extends RecipeSerializer<?>> serializer;

    protected MPGSourceCopyRecipeBase(CraftingBookCategory category, Supplier<? extends Item> sourceItem,
                                      IntSupplier multiplier, Supplier<? extends RecipeSerializer<?>> serializer) {
        super(category);
        this.sourceItem = sourceItem;
        this.multiplier = multiplier;
        this.serializer = serializer;
    }

    @Override
    public boolean matches(CraftingInput input, @NotNull Level level) {
        int sourceCount = 0;
        int nonEmptyCount = 0;
        for (ItemStack stack : input.items()) {
            if (!stack.isEmpty()) {
                nonEmptyCount++;
                if (stack.is(sourceItem.get())) {
                    sourceCount++;
                }
            }
        }
        return nonEmptyCount == 2 && sourceCount >= 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput input, @NotNull HolderLookup.Provider provider) {
        boolean consumedSource = false;
        for (ItemStack stack : input.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            if (!consumedSource && stack.is(sourceItem.get())) {
                consumedSource = true;
                continue;
            }
            ItemStack output = stack.copy();
            output.setCount(Math.max(1, multiplier.getAsInt()));
            return output;
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return sourceItem.get().getDefaultInstance();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return serializer.get();
    }
}
