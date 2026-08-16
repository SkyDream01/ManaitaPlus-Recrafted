package github.com.gengyoubo.MPG.recipe;

import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.core.MPGRecipeSerializerCore;
import github.com.gengyoubo.common.recipe.MPGSourceCopyRecipeBase;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class MPGCraftingRecipe extends MPGSourceCopyRecipeBase {
    public MPGCraftingRecipe(CraftingBookCategory category) {
        super(category, MPGItemCore.ManaitaSource, () -> MPGConfig.source_doubling_value,
                () -> MPGRecipeSerializerCore.CraftingRecipe.get());
    }

    public static class Serializer extends SimpleCraftingRecipeSerializer<MPGCraftingRecipe> {
        public Serializer() {
            super(MPGCraftingRecipe::new);
        }
    }
}
