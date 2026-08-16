package github.com.gengyoubo.jei;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.MPG;
import github.com.gengyoubo.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.block.item.MPHookBlockItem;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.core.MPRecipeSerializerCore;
import github.com.gengyoubo.gui.MPBrewingStandScreen;
import github.com.gengyoubo.gui.MPCraftingScreen;
import github.com.gengyoubo.gui.MPFurnaceScreen;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.util.MPStackData;
import net.minecraft.client.Minecraft;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class MPJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = github.com.gengyoubo.util.MPResource.id("manaita_plus_general", "jei_plugin");

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        MPG.LOGGER.info("Registering JEI categories for {}", UID);
        registration.addRecipeCategories(new MPSourceCopyRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        registration.registerSubtypeInterpreter(MPBlockCore.CraftingBlockItem.get(), MPJeiPlugin::getTypedSubtype);
        registration.registerSubtypeInterpreter(MPBlockCore.FurnaceBlockItem.get(), MPJeiPlugin::getTypedSubtype);
        registration.registerSubtypeInterpreter(MPBlockCore.BrewingBlockItem.get(), MPJeiPlugin::getTypedSubtype);
        registration.registerSubtypeInterpreter(MPBlockCore.HookBlockItem.get(), MPJeiPlugin::getTypedSubtype);
        registration.registerSubtypeInterpreter(MPItemCore.ManaitaCraftingPortable.get(), MPJeiPlugin::getTypedSubtype);
        registration.registerSubtypeInterpreter(MPItemCore.ManaitaFurnacePortable.get(), MPJeiPlugin::getTypedSubtype);
        registration.registerSubtypeInterpreter(MPItemCore.ManaitaBrewingPortable.get(), MPJeiPlugin::getTypedSubtype);
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        MPG.LOGGER.info("Registering JEI recipes for source copying");
        List<RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>> manaitaCraftingRecipes = getManaitaCraftingRecipes();
        MPG.LOGGER.info("Registering {} Manaita crafting recipes in JEI", manaitaCraftingRecipes.size());
        registration.addRecipes(RecipeTypes.CRAFTING, manaitaCraftingRecipes);
        registration.addRecipes(MPSourceCopyRecipeCategory.TYPE, createSourceCopyRecipes());
        registration.addItemStackInfo(
                new ItemStack(MPItemCore.ManaitaSource.get()),
                Component.translatable("jei.manaita_plus_general.source.info.1"),
                Component.translatable("jei.manaita_plus_general.source.info.2", MPGConfig.source_doubling_value)
        );
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        registration.addRecipeClickArea(MPCraftingScreen.class, 88, 32, 28, 23, RecipeTypes.CRAFTING);
        registration.addRecipeClickArea(MPFurnaceScreen.class, 78, 32, 28, 23, RecipeTypes.SMELTING);
        registration.addRecipeClickArea(MPBrewingStandScreen.class, 97, 16, 14, 30, RecipeTypes.BREWING);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(typedStack(MPBlockCore.CraftingBlockItem.get(), 0), RecipeTypes.CRAFTING);
        registration.addRecipeCatalyst(typedStack(MPBlockCore.FurnaceBlockItem.get(), 0), RecipeTypes.SMELTING);
        registration.addRecipeCatalyst(typedStack(MPBlockCore.BrewingBlockItem.get(), 0), RecipeTypes.BREWING);

        registration.addRecipeCatalyst(new ItemStack(MPItemCore.ManaitaSource.get()), MPSourceCopyRecipeCategory.TYPE);
    }
    private static ItemStack typedStack(Item item, int type) {
        ItemStack stack = new ItemStack(item);
        MPStackData.getTag(stack).putInt(MPGNBTData.ItemType, type);
        return stack;
    }

    private static List<MPSourceCopyJeiRecipe> createSourceCopyRecipes() {
        return java.util.stream.Stream.concat(
                        java.util.stream.Stream.of(MPItemCore.ManaitaSource.get()),
                        BuiltInRegistries.ITEM.stream()
                )
                .distinct()
                .filter(MPJeiPlugin::isCopyableItem)
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .map(Item::getDefaultInstance)
                .filter(stack -> !stack.isEmpty())
                .map(MPJeiPlugin::createRecipe)
                .toList();
    }

    private static List<RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>> getManaitaCraftingRecipes() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return List.of();
        }

        return minecraft.level.getRecipeManager().getRecipes().stream()
                .filter(recipeHolder -> MPG.MODID.equals(recipeHolder.id().getNamespace()))
                .filter(recipeHolder -> recipeHolder.value() instanceof net.minecraft.world.item.crafting.CraftingRecipe)
                .filter(recipeHolder -> recipeHolder.value().getSerializer() == MPRecipeSerializerCore.NBTCraftingRecipe.get())
                .map(recipeHolder -> (RecipeHolder<net.minecraft.world.item.crafting.CraftingRecipe>) recipeHolder)
                .toList();
    }

    private static MPSourceCopyJeiRecipe createRecipe(ItemStack input) {
        int sourceResultCount = input.getCount() * MPGConfig.source_doubling_value;
        ItemStack output = input.copy();
        output.setCount(Math.max(sourceResultCount, 1));
        return new MPSourceCopyJeiRecipe(input, output, sourceResultCount);
    }

    private static boolean isCopyableItem(Item item) {
        if (item instanceof MPCraftingBlockItem || item instanceof MPFurnaceBlockItem || item instanceof MPBrewingBlockItem || item instanceof MPHookBlockItem) {
            return false;
        }
        return item != net.minecraft.world.item.Items.AIR;
    }

    private static String getTypedSubtype(ItemStack stack, mezz.jei.api.ingredients.subtypes.UidContext context) {
        return MPGNBTData.ItemType + ":" + MPStackData.getInt(stack, MPGNBTData.ItemType, 0);
    }
}
