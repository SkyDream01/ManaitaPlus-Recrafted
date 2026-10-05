package github.com.gengyoubo.common.block.entity;

import github.com.gengyoubo.common.config.MPGConfigValues;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

final class MPGFurnaceLogic {
    private MPGFurnaceLogic() {
    }

    static ItemStack assemble(@Nullable RecipeHolder<?> recipe, ItemStack input) {
        if (recipe == null || !(recipe.value() instanceof AbstractCookingRecipe cookingRecipe)) {
            return ItemStack.EMPTY;
        }
        return cookingRecipe.assemble(new SingleRecipeInput(input));
    }

    static void awardRecipesAndExperience(ServerPlayer player, List<ItemStack> items,
                                          Object2IntMap<ResourceKey<Recipe<?>>> recipesUsed) {
        List<RecipeHolder<?>> recipes = recipesAndExperience(player.level(), player.position(), recipesUsed);
        player.awardRecipes(recipes);
        for (RecipeHolder<?> recipe : recipes) {
            player.triggerRecipeCrafted(recipe, items);
        }
        recipesUsed.clear();
    }

    static List<RecipeHolder<?>> recipesAndExperience(ServerLevel level, Vec3 pos,
                                                      Object2IntMap<ResourceKey<Recipe<?>>> recipesUsed) {
        List<RecipeHolder<?>> recipes = new ArrayList<>();
        for (Object2IntMap.Entry<ResourceKey<Recipe<?>>> entry : recipesUsed.object2IntEntrySet()) {
            level.recipeAccess().byKey(entry.getKey()).ifPresent(recipe -> {
                recipes.add(recipe);
                if (recipe.value() instanceof AbstractCookingRecipe cookingRecipe) {
                    createExperience(level, pos, entry.getIntValue(), cookingRecipe.experience());
                }
            });
        }
        return recipes;
    }

    private static void createExperience(ServerLevel level, Vec3 pos, int craftedCount, float experiencePerItem) {
        float rawExperience = craftedCount * experiencePerItem;
        int experience = Mth.floor(rawExperience);
        if (Mth.frac(rawExperience) != 0.0F && Math.random() < Mth.frac(rawExperience)) {
            experience++;
        }
        ExperienceOrb.award(level, pos, experience * MPGConfigValues.furnace_doubling_value);
    }
}
