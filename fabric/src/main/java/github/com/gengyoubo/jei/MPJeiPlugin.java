package github.com.gengyoubo.jei;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.common.integration.jei.MPGJeiRecipeFactory;
import github.com.gengyoubo.common.integration.jei.MPGJeiSubtypeInterpreter;
import github.com.gengyoubo.common.integration.jei.MPGSourceCopyRecipe;
import github.com.gengyoubo.common.integration.jei.MPGSourceCopyRecipeCategory;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.core.MPRecipeSerializerCore;
import github.com.gengyoubo.gui.MPBrewingStandScreen;
import github.com.gengyoubo.gui.MPCraftingScreen;
import github.com.gengyoubo.gui.MPFurnaceScreen;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class MPJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(MPG.MODID, "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MPGSourceCopyRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(), MPItemCore.ManaitaSource.get().getDefaultInstance()));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        typedItems().forEach(item -> registration.registerSubtypeInterpreter(item, MPGJeiSubtypeInterpreter.INSTANCE));
    }

    @Override
    public void registerExtraIngredients(IExtraIngredientRegistration registration) {
        registration.addExtraItemStacks(typedStacks());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RecipeTypes.CRAFTING, getManaitaCraftingRecipes());
        registration.addRecipes(MPGSourceCopyRecipeCategory.TYPE, createSourceCopyRecipes());
        registration.addItemStackInfo(MPItemCore.ManaitaSource.get().getDefaultInstance(),
                Component.translatable("jei.manaita_plus_general.source.info.1"),
                Component.translatable("jei.manaita_plus_general.source.info.2", MPGConfig.source_doubling_value));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MPCraftingScreen.class, 88, 32, 28, 23, RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(MPFurnaceScreen.class, 78, 32, 28, 23, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(MPBrewingStandScreen.class, 97, 16, 14, 30, RecipeTypes.BREWING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(typedStack(MPBlockCore.CraftingBlockItem.get()), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(typedStack(MPBlockCore.FurnaceBlockItem.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(typedStack(MPBlockCore.BrewingBlockItem.get()), RecipeTypes.BREWING);
        registration.addRecipeCatalyst(MPItemCore.ManaitaSource.get().getDefaultInstance(), MPGSourceCopyRecipeCategory.TYPE);
    }

    private static List<MPGSourceCopyRecipe> createSourceCopyRecipes() {
        return MPGJeiRecipeFactory.createSourceCopyRecipes(MPItemCore.ManaitaSource.get(),
                MPGConfig.source_doubling_value, typedItems(), typedStacks());
    }

    @SuppressWarnings("unchecked")
    private static List<RecipeHolder<CraftingRecipe>> getManaitaCraftingRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return List.of();
        }
        return minecraft.level.getRecipeManager().getRecipes().stream()
                .filter(holder -> MPG.MODID.equals(holder.id().getNamespace()))
                .filter(holder -> holder.value() instanceof CraftingRecipe)
                .filter(holder -> holder.value().getSerializer() == MPRecipeSerializerCore.NBTCraftingRecipe.get())
                .map(holder -> (RecipeHolder<CraftingRecipe>) holder)
                .toList();
    }

    private static List<Item> typedItems() {
        return List.of(MPBlockCore.CraftingBlockItem.get(), MPBlockCore.FurnaceBlockItem.get(),
                MPBlockCore.BrewingBlockItem.get(), MPBlockCore.HookBlockItem.get(),
                MPItemCore.ManaitaCraftingPortable.get(), MPItemCore.ManaitaFurnacePortable.get(),
                MPItemCore.ManaitaBrewingPortable.get());
    }

    private static List<ItemStack> typedStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        typedItems().forEach(item -> stacks.addAll(MPGJeiRecipeFactory.createTypedStacks(item, 8)));
        return stacks;
    }

    private static ItemStack typedStack(Item item) {
        ItemStack stack = item.getDefaultInstance();
        MPGItemStackData.putInt(stack, MPGNBTData.ItemType, 0);
        return stack;
    }
}
