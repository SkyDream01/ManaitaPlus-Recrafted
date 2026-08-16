package github.com.gengyoubo.MPG.jei;

import github.com.gengyoubo.MPG.core.MPGBlockCore;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.ingredients.subtypes.ISubtypeInterpreter;
import mezz.jei.api.ingredients.subtypes.UidContext;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.MPG.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.MPG.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.MPG.block.item.MPHookBlockItem;
import github.com.gengyoubo.MPG.gui.BrewingStandScreen;
import github.com.gengyoubo.MPG.gui.CraftingManaitaScreen;
import github.com.gengyoubo.MPG.gui.FurnaceManaitaScreen;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.core.MPGMenuCore;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;

import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    private static final ISubtypeInterpreter<ItemStack> TYPE_SUBTYPE = new ISubtypeInterpreter<>() {
        @Override
        public Object getSubtypeData(ItemStack ingredient, UidContext context) {
            return MPGItemStackData.getInt(ingredient, MPGNBTData.ItemType);
        }

        @Override
        @Deprecated(since = "19.9.0")
        public String getLegacyStringSubtypeInfo(ItemStack ingredient, UidContext context) {
            return String.valueOf(MPGItemStackData.getInt(ingredient, MPGNBTData.ItemType));
        }
    };

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(MPGBlockCore.CraftingBlockItem.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGBlockCore.FurnaceBlockItem.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGBlockCore.BrewingBlockItem.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGBlockCore.HookBlockItem.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGItemCore.ManaitaCraftingPortable.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGItemCore.ManaitaFurnacePortable.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGItemCore.ManaitaBrewingPortable.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGItemCore.ManaitaCraftingRing.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGItemCore.ManaitaFurnaceRing.get(), TYPE_SUBTYPE);
        registration.registerSubtypeInterpreter(MPGItemCore.ManaitaBrewingRing.get(), TYPE_SUBTYPE);
    }

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return ResourceLocation.fromNamespaceAndPath("manaita_plus_general", "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SourceCopyRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(CraftingManaitaScreen.class, 88, 32, 28, 23, RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(FurnaceManaitaScreen.class, 78, 32, 28, 23, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(BrewingStandScreen.class, 97, 16, 14, 30, RecipeTypes.BREWING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(MPGBlockCore.CraftingBlock.get()), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(new ItemStack(MPGItemCore.ManaitaCraftingPortable.get()), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(new ItemStack(MPGItemCore.ManaitaSource.get()), SourceCopyRecipeCategory.TYPE);
        registration.addRecipeCatalyst(new ItemStack(MPGBlockCore.FurnaceBlock.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MPGItemCore.ManaitaFurnacePortable.get()), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(new ItemStack(MPGBlockCore.BrewingBlock.get()), RecipeTypes.BREWING);
        registration.addRecipeCatalyst(new ItemStack(MPGItemCore.ManaitaBrewingPortable.get()), RecipeTypes.BREWING);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(SourceCopyRecipeCategory.TYPE, createSourceCopyRecipes());
        registration.addItemStackInfo(
                new ItemStack(MPGItemCore.ManaitaSource.get()),
                Component.translatable("jei.manaita_plus_general.source.info.1"),
                Component.translatable("jei.manaita_plus_general.source.info.2", MPGConfig.source_doubling_value),
                Component.translatable("jei.manaita_plus_general.source.info.3"),
                Component.translatable("jei.manaita_plus_general.source.info.4", MPGConfig.source_doubling_value)
        );
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(MPGCraftingMenu.class, MPGMenuCore.CraftingManaita.get(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
        registration.addRecipeTransferHandler(MPGFurnaceMenu.class, MPGMenuCore.FurnaceManaita.get(), RecipeTypes.SMELTING, 0, 1, 3, 36);
        registration.addRecipeTransferHandler(MPGBrewingStandMenu.class, MPGMenuCore.BrewingStandManaita.get(), RecipeTypes.BREWING, 1, 3, 5, 36);
    }

    private static List<SourceCopyJeiRecipe> createSourceCopyRecipes() {
        ItemStack source = new ItemStack(MPGItemCore.ManaitaSource.get());
        return BuiltInRegistries.ITEM.stream()
                .filter(JEIPlugin::isCopyableItem)
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .map(Item::getDefaultInstance)
                .filter(stack -> !stack.isEmpty())
                .map(input -> {
                    ItemStack normalizedInput = input.copy();
                    normalizedInput.setCount(1);
                    ItemStack output = normalizedInput.copy();
                    output.setCount(MPGConfig.source_doubling_value);
                    return new SourceCopyJeiRecipe(source.copy(), normalizedInput, output);
                })
                .toList();
    }

    private static boolean isCopyableItem(Item item) {
        if (item == Items.AIR || item == MPGItemCore.ManaitaSource.get()) {
            return false;
        }
        return !(item instanceof MPCraftingBlockItem)
                && !(item instanceof MPFurnaceBlockItem)
                && !(item instanceof MPBrewingBlockItem)
                && !(item instanceof MPHookBlockItem);
    }
}
