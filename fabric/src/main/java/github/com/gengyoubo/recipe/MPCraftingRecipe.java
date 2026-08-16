package github.com.gengyoubo.recipe;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.common.recipe.MPGSourceCopyRecipeBase;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.core.MPRecipeSerializerCore;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;

public class MPCraftingRecipe extends MPGSourceCopyRecipeBase {
    public MPCraftingRecipe(CraftingBookCategory category) {
        super(category, MPItemCore.ManaitaSource, () -> MPGConfig.source_doubling_value,
                () -> MPRecipeSerializerCore.CraftingRecipe.get());
    }

    public static class Serializer extends SimpleCraftingRecipeSerializer<MPCraftingRecipe> {
        public Serializer() {
            super(MPCraftingRecipe::new);
        }
    }
}
