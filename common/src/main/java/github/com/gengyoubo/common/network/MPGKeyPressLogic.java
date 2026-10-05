package github.com.gengyoubo.common.network;

import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

/** Server-side key action shared by all networking implementations. */
public final class MPGKeyPressLogic {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private MPGKeyPressLogic() {
    }

    public static void handle(ServerPlayer player, byte keyCode) {
        switch (keyCode) {
            case 0 -> invokeStandardMainHand(player);
            case 1 -> invokeArmor(player);
            case 2 -> invokePaxel(player);
            default -> {
            }
        }
    }

    private static void invokeArmor(ServerPlayer player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            invoke(player.getItemBySlot(slot), player);
        }
    }

    private static void invokeStandardMainHand(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IMPGKey keyItem) || keyItem.usesStandardModeKey()) {
            invoke(stack, player);
        }
    }

    private static void invokePaxel(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof IMPGKey keyItem && keyItem.usesDedicatedDoublingKey()) {
            keyItem.onDedicatedDoublingKey(stack, player);
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
