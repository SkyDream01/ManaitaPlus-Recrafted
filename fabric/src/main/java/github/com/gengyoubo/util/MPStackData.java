package github.com.gengyoubo.util;

import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public final class MPStackData {
    private MPStackData() {
    }

    public static CompoundTag getTag(ItemStack stack) {
        return stack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
    }

    public static boolean hasTag(ItemStack stack) {
        return !stack.getComponents().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).isEmpty();
    }

    public static void setTag(ItemStack stack, CompoundTag tag) {
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
    }

    public static int getInt(ItemStack stack, String key) {
        return getTag(stack).getInt(key);
    }

    public static int getInt(ItemStack stack, String key, int fallback) {
        CompoundTag tag = getTag(stack);
        return tag.contains(key) ? tag.getInt(key) : fallback;
    }

    public static void putInt(ItemStack stack, String key, int value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putInt(key, value));
    }

    public static boolean getBoolean(ItemStack stack, String key) {
        return getTag(stack).getBoolean(key);
    }

    public static boolean getBoolean(ItemStack stack, String key, boolean fallback) {
        CompoundTag tag = getTag(stack);
        return tag.contains(key) ? tag.getBoolean(key) : fallback;
    }

    public static void putBoolean(ItemStack stack, String key, boolean value) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(key, value));
    }
}
