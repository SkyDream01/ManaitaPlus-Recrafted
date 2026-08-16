package github.com.gengyoubo.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public final class MPGFurnaceLogicHelper {
    private MPGFurnaceLogicHelper() {
    }

    public static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> items, WorldlyContainer container) {
        if (items.get(0).isEmpty() || recipe == null) {
            return false;
        }
        ItemStack assembled = assembleResult(recipe, container, registryAccess);
        if (assembled.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(2);
        return output.isEmpty() || ItemStack.isSameItem(output, assembled);
    }

    public static ItemStack assembleResult(RecipeHolder<?> recipe, WorldlyContainer container, RegistryAccess registryAccess) {
        return ((AbstractCookingRecipe) recipe.value()).assemble(new SingleRecipeInput(container.getItem(0)), registryAccess);
    }

    public static void awardUsedRecipesAndPopExperience(ServerPlayer player, NonNullList<ItemStack> items, Object2IntMap<ResourceLocation> recipesUsed) {
        List<RecipeHolder<?>> recipes = getRecipesToAwardAndPopExperience(player.serverLevel(), player.position(), recipesUsed);
        player.awardRecipes(recipes);
        for (RecipeHolder<?> recipe : recipes) {
            if (recipe != null) {
                player.triggerRecipeCrafted(recipe, items);
            }
        }
        recipesUsed.clear();
    }

    public static List<RecipeHolder<?>> getRecipesToAwardAndPopExperience(ServerLevel level, Vec3 pos, Object2IntMap<ResourceLocation> recipesUsed) {
        List<RecipeHolder<?>> recipes = Lists.newArrayList();
        for (Object2IntMap.Entry<ResourceLocation> entry : recipesUsed.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent(recipe -> {
                recipes.add(recipe);
                createExperience(level, pos, entry.getIntValue(), ((AbstractCookingRecipe) recipe.value()).getExperience());
            });
        }
        return recipes;
    }

    private static void createExperience(ServerLevel level, Vec3 pos, int craftedCount, float xpPerItem) {
        int totalXp = Mth.floor((float) craftedCount * xpPerItem);
        float fractional = Mth.frac((float) craftedCount * xpPerItem);
        if (fractional != 0.0F && Math.random() < (double) fractional) {
            ++totalXp;
        }
        ExperienceOrb.award(level, pos, totalXp * 64);
    }
}


