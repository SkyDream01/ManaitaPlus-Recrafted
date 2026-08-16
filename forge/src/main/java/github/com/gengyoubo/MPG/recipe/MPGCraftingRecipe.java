package github.com.gengyoubo.MPG.recipe;

import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.MPG.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.MPG.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.MPG.block.item.MPHookBlockItem;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGItemCore;
import github.com.gengyoubo.MPG.core.MPGRecipeSerializerCore;
import github.com.gengyoubo.MPG.item.MPGSourceItem;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGCraftingRecipe extends CustomRecipe {
    private static final ItemStack SOURCE_RESULT = new ItemStack(MPGItemCore.ManaitaSource.get());

    public MPGCraftingRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput container, @NotNull Level level) {
        boolean hasSource = false;
        int nonEmptyCount = 0;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            nonEmptyCount++;
            if (stack.getItem() instanceof MPGSourceItem) {
                hasSource = true;
            }
        }

        if (nonEmptyCount != 2 || !hasSource) {
            return false;
        }

        boolean hasUpgradeableBlock = false;
        boolean hasMaterial = false;
        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            Item item = stack.getItem();
            if (item instanceof MPCraftingBlockItem || item instanceof MPFurnaceBlockItem || item instanceof MPBrewingBlockItem || item instanceof MPHookBlockItem) {
                hasUpgradeableBlock = true;
            } else if (item == Items.OAK_PLANKS || item == Items.COBBLESTONE || item == Items.IRON_BLOCK || item == Items.REDSTONE_BLOCK || item == Items.GOLD_BLOCK || item == Items.DIAMOND_BLOCK || item == Items.EMERALD_BLOCK || item == Items.NETHERITE_BLOCK) {
                hasMaterial = true;
            }
        }
        return hasUpgradeableBlock && hasMaterial;
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput container, @NotNull HolderLookup.Provider provider) {
        ItemStack material = ItemStack.EMPTY;
        ItemStack blockInput = ItemStack.EMPTY;
        ItemStack hookInput = ItemStack.EMPTY;
        boolean hasSource = false;

        for (int i = 0; i < container.size(); i++) {
            ItemStack stack = container.getItem(i);
            Item item = stack.getItem();
            if (item instanceof MPGSourceItem) {
                hasSource = true;
                continue;
            }
            if (stack.isEmpty()) {
                continue;
            }
            if (item instanceof MPHookBlockItem) {
                hookInput = stack;
                continue;
            }
            if (item instanceof MPCraftingBlockItem || item instanceof MPFurnaceBlockItem || item instanceof MPBrewingBlockItem) {
                blockInput = stack;
                continue;
            }
            material = stack;
        }

        if (hasSource && !material.isEmpty() && blockInput.isEmpty() && hookInput.isEmpty()) {
            ItemStack copied = material.copy();
            copied.setCount(MPGConfig.source_doubling_value);
            return copied;
        }

        if (!blockInput.isEmpty() && !hookInput.isEmpty()) {
            return assemblePortable(blockInput, hookInput);
        }

        if (!material.isEmpty() && !blockInput.isEmpty()) {
            int type = resolveMaterialType(material.getItem());
            if (type <= 0) {
                return ItemStack.EMPTY;
            }
            ItemStack result = createBlockResult(blockInput.getItem());
            if (result.isEmpty()) {
                return ItemStack.EMPTY;
            }
            MPGItemStackData.putInt(result, MPGNBTData.ItemType, type);
            return result;
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack assemblePortable(ItemStack blockInput, ItemStack hookInput) {
        if (MPGItemStackData.getInt(blockInput, MPGNBTData.ItemType) != 0) {
            return ItemStack.EMPTY;
        }

        ItemStack portable;
        Item item = blockInput.getItem();
        if (item instanceof MPCraftingBlockItem) {
            portable = new ItemStack(MPGItemCore.ManaitaCraftingPortable.get());
        } else if (item instanceof MPFurnaceBlockItem) {
            portable = new ItemStack(MPGItemCore.ManaitaFurnacePortable.get());
        } else if (item instanceof MPBrewingBlockItem) {
            portable = new ItemStack(MPGItemCore.ManaitaBrewingPortable.get());
        } else {
            return ItemStack.EMPTY;
        }

        int hookType = MPGItemStackData.getInt(hookInput, MPGNBTData.ItemType);
        MPGItemStackData.putInt(portable, MPGNBTData.ItemType, hookType + 1);
        return portable;
    }

    private static ItemStack createBlockResult(Item blockItem) {
        if (blockItem instanceof MPCraftingBlockItem) {
            return new ItemStack(MPGBlockCore.CraftingBlockItem.get());
        }
        if (blockItem instanceof MPFurnaceBlockItem) {
            return new ItemStack(MPGBlockCore.FurnaceBlockItem.get());
        }
        if (blockItem instanceof MPBrewingBlockItem) {
            return new ItemStack(MPGBlockCore.BrewingBlockItem.get());
        }
        return ItemStack.EMPTY;
    }

    private static int resolveMaterialType(Item material) {
        if (material == Items.OAK_PLANKS) return 1;
        if (material == Items.COBBLESTONE) return 2;
        if (material == Items.IRON_BLOCK) return 3;
        if (material == Items.GOLD_BLOCK) return 4;
        if (material == Items.DIAMOND_BLOCK) return 5;
        if (material == Items.EMERALD_BLOCK) return 6;
        if (material == Items.REDSTONE_BLOCK) return 7;
        if (material == Items.NETHERITE_BLOCK) return 8;
        return -1;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return SOURCE_RESULT.copy();
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return MPGRecipeSerializerCore.CraftingRecipe.get();
    }

    public static class Serializer extends SimpleCraftingRecipeSerializer<MPGCraftingRecipe> {
        public Serializer() {
            super(MPGCraftingRecipe::new);
        }
    }
}
