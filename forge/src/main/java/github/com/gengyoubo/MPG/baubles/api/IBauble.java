package github.com.gengyoubo.MPG.baubles.api;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IBauble {
    BaubleType getBaubleType(ItemStack itemStack);

    default void onWornTick(ItemStack itemStack, Player player) {
    }

    default void onEquipped(ItemStack itemStack, Player player) {
    }

    default void onUnequipped(ItemStack itemStack, Player player) {
    }

    default boolean canEquip(ItemStack itemStack, Player player) {
        return true;
    }

    default boolean canUnequip(ItemStack itemStack, Player player) {
        return true;
    }
}
