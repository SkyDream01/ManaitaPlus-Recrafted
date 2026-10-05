package github.com.gengyoubo.MPG.core;

import github.com.gengyoubo.MPG.recipe.MPGCraftingRecipe;
import github.com.gengyoubo.common.recipe.MPNBTCraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;

import static github.com.gengyoubo.MPG.MPG.RECIPE_SERIALIZER_DEFERRED_REGISTER;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.CRAFTING_RECIPE;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.NBT_CRAFTING_RECIPE;

public class MPGRecipeSerializerCore {
    // RecipeSerializer is a record in 26.3, so the factories build the instances.
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> CraftingRecipe =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register(CRAFTING_RECIPE, MPGCraftingRecipe.Serializer::create);
    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> NBTCraftingRecipe =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register(NBT_CRAFTING_RECIPE, MPNBTCraftingRecipe.Serializer::create);

    public static void init() {
    }
}
