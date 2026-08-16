package github.com.gengyoubo.common.item.data;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IMPGKey {
    default void onManaitaKeyPress(ItemStack itemStack) {
    }

    default void onManaitaKeyPress(ItemStack itemStack, Player player) {
        onManaitaKeyPress(itemStack);
    }

    default void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
    }
}
