package github.com.gengyoubo.common.integration.jei;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class MPGJeiRecipeFactory {
    private MPGJeiRecipeFactory() {
    }

    public static List<MPGSourceCopyRecipe> createSourceCopyRecipes(
            Item source, int multiplier, Collection<Item> excludedItems, Collection<ItemStack> typedInputs) {
        Set<Item> excluded = new HashSet<>(excludedItems);
        excluded.add(Items.AIR);
        excluded.add(source);

        List<ItemStack> inputs = new ArrayList<>();
        inputs.add(source.getDefaultInstance());
        BuiltInRegistries.ITEM.stream()
                .filter(item -> !excluded.contains(item))
                .sorted(Comparator.comparing(item -> BuiltInRegistries.ITEM.getKey(item).toString()))
                .map(Item::getDefaultInstance)
                .filter(stack -> !stack.isEmpty())
                .forEach(inputs::add);
        typedInputs.stream().map(ItemStack::copy).forEach(inputs::add);

        List<ItemStack> normalized = inputs.stream().map(MPGJeiRecipeFactory::normalizeInput).toList();
        List<ItemStack> outputs = normalized.stream().map(stack -> createOutput(stack, multiplier)).toList();
        List<ItemStack> sources = new ArrayList<>(normalized.size());
        for (int i = 0; i < normalized.size(); i++) {
            sources.add(source.getDefaultInstance());
        }
        return List.of(new MPGSourceCopyRecipe(sources, normalized, outputs, multiplier));
    }

    public static List<ItemStack> createTypedStacks(Item item, int maxType) {
        List<ItemStack> stacks = new ArrayList<>(maxType + 1);
        for (int type = 0; type <= maxType; type++) {
            ItemStack stack = item.getDefaultInstance();
            MPGItemStackData.putInt(stack, MPGNBTData.ItemType, type);
            stacks.add(stack);
        }
        return stacks;
    }

    private static ItemStack normalizeInput(ItemStack input) {
        ItemStack result = input.copy();
        result.setCount(1);
        return result;
    }

    private static ItemStack createOutput(ItemStack input, int multiplier) {
        ItemStack result = input.copy();
        result.setCount(Math.max(1, multiplier));
        return result;
    }
}
