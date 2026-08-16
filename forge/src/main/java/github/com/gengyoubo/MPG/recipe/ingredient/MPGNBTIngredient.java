package github.com.gengyoubo.MPG.recipe.ingredient;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class MPGNBTIngredient {
    private MPGNBTIngredient() {
    }

    public static boolean matchesType(ItemStack expected, @Nullable ItemStack actual) {
        if (actual == null || actual.isEmpty()) {
            return false;
        }
        if (!expected.is(actual.getItem())) {
            return false;
        }

        int expectedType = readType(expected);
        return expectedType == 0 || expectedType == readType(actual);
    }

    public static int readType(ItemStack stack) {
        return MPGItemStackData.getInt(stack, MPGNBTData.ItemType);
    }
}
