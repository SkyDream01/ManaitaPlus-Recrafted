package github.com.gengyoubo.MPG.recipe;

import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.core.MPGRecipeSerializerCore;
import github.com.gengyoubo.MPG.item.MPGSourceItem;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGCraftingRecipe extends CustomRecipe {
    private static final ItemStack SOURCE_RESULT = new ItemStack(MPGItemCore.ManaitaSource.get());

    public MPGCraftingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, @NotNull Level level) {
        int sourceCount = 0;
        int nonEmptyCount = 0;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            nonEmptyCount++;
            if (stack.getItem() instanceof MPGSourceItem) {
                sourceCount++;
            }
        }

        return nonEmptyCount == 2 && sourceCount >= 1;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput container, @NotNull HolderLookup.Provider provider) {
        ItemStack copyTarget = ItemStack.EMPTY;
        boolean hasSource = false;

        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.getItem() instanceof MPGSourceItem && !hasSource) {
                hasSource = true;
                continue;
            }
            if (stack.isEmpty()) {
                continue;
            }
            if (isCopyableStack(stack)) {
                copyTarget = stack;
            }
        }

        if (hasSource && !copyTarget.isEmpty()) {
            ItemStack copied = copyTarget.copy();
            copied.setCount(MPGConfig.source_doubling_value);
            return copied;
        }
        return ItemStack.EMPTY;
    }

    private static boolean isCopyableStack(ItemStack stack) {
        return !stack.isEmpty();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return SOURCE_RESULT.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return MPGRecipeSerializerCore.CraftingRecipe.get();
    }

    public static class Serializer extends SimpleCraftingRecipeSerializer<MPGCraftingRecipe> {
        public Serializer() {
            super(MPGCraftingRecipe::new);
        }
    }
}
