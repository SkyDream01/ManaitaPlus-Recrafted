package github.com.gengyoubo.MPG.jei;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.core.MPGMenuCore;
import github.com.gengyoubo.MPG.gui.BrewingStandScreen;
import github.com.gengyoubo.MPG.gui.CraftingManaitaScreen;
import github.com.gengyoubo.MPG.gui.FurnaceManaitaScreen;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.integration.jei.MPGJeiRecipeFactory;
import github.com.gengyoubo.common.integration.jei.MPGJeiSubtypeInterpreter;
import github.com.gengyoubo.common.integration.jei.MPGSourceCopyRecipe;
import github.com.gengyoubo.common.integration.jei.MPGSourceCopyRecipeCategory;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(MPG.MODID, "jei_plugin");

    @Override
    public @NotNull Identifier getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new MPGSourceCopyRecipeCategory(
                registration.getJeiHelpers().getGuiHelper(), MPGItemCore.ManaitaSource.get().getDefaultInstance()));
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
        List<MPGSourceCopyRecipe> recipes = createSourceCopyRecipes();
        registration.addRecipes(MPGSourceCopyRecipeCategory.TYPE, recipes);
        registration.addItemStackInfo(MPGItemCore.ManaitaSource.get().getDefaultInstance(),
                Component.translatable("jei.manaita_plus_general.source.info.1"),
                Component.translatable("jei.manaita_plus_general.source.info.2", MPGConfig.source_doubling_value));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CraftingManaitaScreen.class, 88, 32, 28, 23, RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(FurnaceManaitaScreen.class, 78, 32, 28, 23, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(BrewingStandScreen.class, 97, 16, 14, 30, RecipeTypes.BREWING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        // JEI 31.9 deprecates addRecipeCatalyst for removal; addCraftingStation is the
        // replacement (same behavior, recipe type now comes first).
        registration.addCraftingStation(RecipeTypes.CRAFTING, MPGBlockCore.CraftingBlockItem.get());
        registration.addCraftingStation(RecipeTypes.CRAFTING, MPGItemCore.ManaitaCraftingPortable.get());
        registration.addCraftingStation(MPGSourceCopyRecipeCategory.TYPE, MPGItemCore.ManaitaSource.get());
        registration.addCraftingStation(RecipeTypes.SMELTING, MPGBlockCore.FurnaceBlockItem.get());
        registration.addCraftingStation(RecipeTypes.SMELTING, MPGItemCore.ManaitaFurnacePortable.get());
        registration.addCraftingStation(RecipeTypes.BREWING, MPGBlockCore.BrewingBlockItem.get());
        registration.addCraftingStation(RecipeTypes.BREWING, MPGItemCore.ManaitaBrewingPortable.get());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(MPGCraftingMenu.class, MPGMenuCore.CraftingManaita.get(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
        registration.addRecipeTransferHandler(MPGFurnaceMenu.class, MPGMenuCore.FurnaceManaita.get(), RecipeTypes.SMELTING, 0, 1, 3, 36);
        registration.addRecipeTransferHandler(MPGBrewingStandMenu.class, MPGMenuCore.BrewingStandManaita.get(), RecipeTypes.BREWING, 1, 3, 5, 36);
    }

    private static List<MPGSourceCopyRecipe> createSourceCopyRecipes() {
        return MPGJeiRecipeFactory.createSourceCopyRecipes(MPGItemCore.ManaitaSource.get(),
                MPGConfig.source_doubling_value, typedItems(), typedStacks());
    }

    private static List<Item> typedItems() {
        List<Item> items = new ArrayList<>(List.of(
                MPGBlockCore.CraftingBlockItem.get(), MPGBlockCore.FurnaceBlockItem.get(),
                MPGBlockCore.BrewingBlockItem.get(), MPGBlockCore.HookBlockItem.get(),
                MPGItemCore.ManaitaCraftingPortable.get(), MPGItemCore.ManaitaFurnacePortable.get(),
                MPGItemCore.ManaitaBrewingPortable.get()));
        if (MPGItemCore.isCuriosLoaded()) {
            items.add(MPGItemCore.ManaitaCraftingRing.get());
            items.add(MPGItemCore.ManaitaFurnaceRing.get());
            items.add(MPGItemCore.ManaitaBrewingRing.get());
        }
        return items;
    }

    private static List<ItemStack> typedStacks() {
        List<ItemStack> stacks = new ArrayList<>();
        typedItems().forEach(item -> stacks.addAll(MPGJeiRecipeFactory.createTypedStacks(item, 8)));
        return stacks;
    }
}
