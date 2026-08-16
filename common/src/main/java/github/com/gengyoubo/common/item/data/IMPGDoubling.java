package github.com.gengyoubo.common.item.data;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;

public interface IMPGDoubling {
    default boolean isDoubling(ItemStack itemStack) {
        return MPGItemStackData.getBoolean(itemStack, MPGNBTData.Doubling);
    }

    default void setDoubling(ItemStack itemStack, boolean doubling) {
        MPGItemStackData.putBoolean(itemStack, MPGNBTData.Doubling, doubling);
    }

    default boolean toggleDoubling(ItemStack itemStack) {
        boolean doubling = !isDoubling(itemStack);
        setDoubling(itemStack, doubling);
        return doubling;
    }

    default boolean shouldMultiplyDrops(ItemStack itemStack, Player player) {
        return isDoubling(itemStack);
    }

    default boolean shouldMultiplyExperience(ItemStack itemStack, Player player) {
        return isDoubling(itemStack);
    }
}
