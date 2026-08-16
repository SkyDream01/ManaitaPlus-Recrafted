package github.com.gengyoubo.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.block.item.MPHookBlockItem;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPItemCore;
import github.com.gengyoubo.core.MPRecipeSerializerCore;
import github.com.gengyoubo.item.MPSourceItem;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPCraftingRecipe implements CraftingRecipe {
    private static final ItemStack SOURCE_RESULT = new ItemStack(MPItemCore.ManaitaSource.get());
    private static final StringRepresentable.EnumCodec<CraftingBookCategory> CATEGORY_CODEC =
            StringRepresentable.fromEnum(CraftingBookCategory::values);

    private final CraftingBookCategory category;

    public MPCraftingRecipe(CraftingBookCategory category) {
        this.category = category;
    }

    @Override
    public @NotNull CraftingBookCategory category() {
        return category;
    }

    @Override
    public boolean matches(CraftingInput container, @NotNull Level level) {
        int sourceStacks = 0;
        int nonEmptyCount = 0;
        ItemStack otherStack = ItemStack.EMPTY;
        for (ItemStack stack : container.items()) {
            if (stack.isEmpty()) {
                continue;
            }
            nonEmptyCount++;
            if (stack.getItem() instanceof MPSourceItem) {
                sourceStacks++;
            } else {
                otherStack = stack;
            }
        }

        if (nonEmptyCount != 2 || sourceStacks == 0) {
            return false;
        }

        if (sourceStacks == 2) {
            return true;
        }

        if (!otherStack.isEmpty() && !(otherStack.getItem() instanceof MPSourceItem)) {
            if (!(otherStack.getItem() instanceof MPCraftingBlockItem)
                    && !(otherStack.getItem() instanceof MPFurnaceBlockItem)
                    && !(otherStack.getItem() instanceof MPBrewingBlockItem)
                    && !(otherStack.getItem() instanceof MPHookBlockItem)) {
                return true;
            }
        }

        boolean hasUpgradeableBlock = false;
        boolean hasPortableBlock = false;
        boolean hasMaterial = false;
        boolean hasHook = false;
        for (ItemStack stack : container.items()) {
            Item item = stack.getItem();
            if (item instanceof MPHookBlockItem) {
                hasHook = true;
            } else if (item instanceof MPFurnaceBlockItem || item instanceof MPBrewingBlockItem) {
                hasUpgradeableBlock = true;
                hasPortableBlock = true;
            } else if (item instanceof MPCraftingBlockItem) {
                hasPortableBlock = true;
            } else if (item == Items.OAK_PLANKS || item == Items.COBBLESTONE || item == Items.IRON_BLOCK || item == Items.REDSTONE_BLOCK || item == Items.GOLD_BLOCK || item == Items.DIAMOND_BLOCK || item == Items.EMERALD_BLOCK || item == Items.NETHERITE_BLOCK) {
                hasMaterial = true;
            }
        }
        return (hasUpgradeableBlock && hasMaterial) || (hasPortableBlock && hasHook);
    }

    @Override
    public @NotNull ItemStack assemble(CraftingInput container, @NotNull HolderLookup.Provider provider) {
        ItemStack material = ItemStack.EMPTY;
        ItemStack blockInput = ItemStack.EMPTY;
        ItemStack hookInput = ItemStack.EMPTY;
        boolean hasSource = false;
        int sourceStacks = 0;
        ItemStack sourceTarget = ItemStack.EMPTY;

        for (ItemStack stack : container.items()) {
            Item item = stack.getItem();
            if (item instanceof MPSourceItem) {
                hasSource = true;
                sourceStacks++;
                if (sourceTarget.isEmpty()) {
                    sourceTarget = stack;
                }
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

        if (sourceStacks == 2 && material.isEmpty() && blockInput.isEmpty() && hookInput.isEmpty()) {
            ItemStack copied = sourceTarget.copy();
            copied.setCount(sourceTarget.getCount() * MPGConfig.source_doubling_value);
            return copied;
        }

        if (hasSource && !material.isEmpty() && blockInput.isEmpty() && hookInput.isEmpty()) {
            ItemStack copied = material.copy();
            copied.setCount(material.getCount() * MPGConfig.source_doubling_value);
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
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(result, MPGNBTData.ItemType, type);
            return result;
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack assemblePortable(ItemStack blockInput, ItemStack hookInput) {
        if (github.com.gengyoubo.common.util.MPGItemStackData.getInt(blockInput, MPGNBTData.ItemType) != 0) {
            return ItemStack.EMPTY;
        }

        ItemStack portable;
        Item item = blockInput.getItem();
        if (item instanceof MPCraftingBlockItem) {
            portable = new ItemStack(MPItemCore.ManaitaCraftingPortable.get());
        } else if (item instanceof MPFurnaceBlockItem) {
            portable = new ItemStack(MPItemCore.ManaitaFurnacePortable.get());
        } else if (item instanceof MPBrewingBlockItem) {
            portable = new ItemStack(MPItemCore.ManaitaBrewingPortable.get());
        } else {
            return ItemStack.EMPTY;
        }

        int hookType = github.com.gengyoubo.common.util.MPGItemStackData.getInt(hookInput, MPGNBTData.ItemType);
        github.com.gengyoubo.common.util.MPGItemStackData.putInt(portable, MPGNBTData.ItemType, hookType + 1);
        return portable;
    }

    private static ItemStack createBlockResult(Item blockItem) {
        if (blockItem instanceof MPCraftingBlockItem) {
            return new ItemStack(MPBlockCore.CraftingBlockItem.get());
        }
        if (blockItem instanceof MPFurnaceBlockItem) {
            return new ItemStack(MPBlockCore.FurnaceBlockItem.get());
        }
        if (blockItem instanceof MPBrewingBlockItem) {
            return new ItemStack(MPBlockCore.BrewingBlockItem.get());
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
        return SOURCE_RESULT;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return MPRecipeSerializerCore.CraftingRecipe.get();
    }

    public static class Serializer implements RecipeSerializer<MPCraftingRecipe> {
        private static final MapCodec<MPCraftingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
                instance.group(
                        CATEGORY_CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(MPCraftingRecipe::category)
                ).apply(instance, MPCraftingRecipe::new)
        );
        private static final StreamCodec<RegistryFriendlyByteBuf, MPCraftingRecipe> STREAM_CODEC = StreamCodec.of(
                (buf, recipe) -> CraftingBookCategory.STREAM_CODEC.encode(buf, recipe.category),
                buf -> new MPCraftingRecipe(CraftingBookCategory.STREAM_CODEC.decode(buf))
        );

        @Override
        public @NotNull MapCodec<MPCraftingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, MPCraftingRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
