package github.com.gengyoubo.MPG.core;

import github.com.gengyoubo.MPG.recipe.MPGCraftingRecipe;
import github.com.gengyoubo.common.recipe.MPNBTCraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.RegistryObject;

import static github.com.gengyoubo.MPG.MPG.RECIPE_SERIALIZER_DEFERRED_REGISTER;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.CRAFTING_RECIPE;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.NBT_CRAFTING_RECIPE;

public class MPGRecipeSerializerCore {
    public static final RegistryObject<RecipeSerializer<?>> CraftingRecipe =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register(CRAFTING_RECIPE, MPGCraftingRecipe.Serializer::new);
    public static final RegistryObject<RecipeSerializer<?>> NBTCraftingRecipe =
            RECIPE_SERIALIZER_DEFERRED_REGISTER.register(NBT_CRAFTING_RECIPE, MPNBTCraftingRecipe.Serializer::new);

    public static void init() {
    }
}
