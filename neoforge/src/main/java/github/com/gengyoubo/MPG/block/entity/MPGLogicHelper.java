package github.com.gengyoubo.MPG.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public final class MPGLogicHelper {
    private MPGLogicHelper() {
    }

    public static boolean canBurn(RegistryAccess registryAccess, @Nullable RecipeHolder<?> recipe, NonNullList<ItemStack> items, int maxStackSize, AbstractFurnaceBlockEntity furnace) {
        if (items.getFirst().isEmpty() || recipe == null) {
            return false;
        }
        ItemStack assembled = assembleResult(recipe, furnace.getItem(0), registryAccess);
        if (assembled.isEmpty()) {
            return false;
        }
        ItemStack output = items.get(2);
        if (output.isEmpty()) {
            return true;
        }
        if (!ItemStack.isSameItemSameComponents(output, assembled)) {
            return false;
        }
        return output.getCount() + assembled.getCount() <= maxStackSize && output.getCount() + assembled.getCount() <= output.getMaxStackSize();
    }

    public static ItemStack assembleResult(RecipeHolder<?> recipe, ItemStack input, RegistryAccess registryAccess) {
        return ((RecipeHolder<? extends AbstractCookingRecipe>) recipe).value().assemble(new SingleRecipeInput(input), registryAccess);
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
