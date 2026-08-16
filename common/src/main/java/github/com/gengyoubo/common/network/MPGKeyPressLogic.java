package github.com.gengyoubo.common.network;

import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

/** Server-side key action shared by all networking implementations. */
public final class MPGKeyPressLogic {
    private MPGKeyPressLogic() {
    }

    public static void handle(ServerPlayer player, byte keyCode) {
        switch (keyCode) {
            case 0 -> invoke(player.getMainHandItem(), player);
            case 1 -> player.getInventory().armor.forEach(stack -> invoke(stack, player));
            default -> {
            }
        }
    }

    public static boolean invoke(ItemStack stack, ServerPlayer player) {
        if (!stack.isEmpty() && stack.getItem() instanceof IMPGKey keyItem) {
            keyItem.onManaitaKeyPress(stack, player);
            return true;
        }
        return false;
    }
}
