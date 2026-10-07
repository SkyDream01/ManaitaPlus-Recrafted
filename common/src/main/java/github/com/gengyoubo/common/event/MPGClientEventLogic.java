package github.com.gengyoubo.common.event;

import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.item.data.IMPGOffhandKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Loader-neutral client event actions. */
public final class MPGClientEventLogic {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };

    private MPGClientEventLogic() {
    }

    public static void handleMainHandKey(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IMPGKey keyItem) || keyItem.usesStandardModeKey()) {
            invokeClient(stack, player);
        }
        ItemStack offhand = player.getOffhandItem();
        if (!(stack.getItem() instanceof IMPGOffhandKey)
                && offhand.getItem() instanceof IMPGOffhandKey) {
            invokeClient(offhand, player);
        }
    }

    public static void handlePaxelKey(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof IMPGKey keyItem && keyItem.usesDedicatedDoublingKey()) {
            keyItem.onDedicatedDoublingKeyOnClient(stack, player);
        }
    }

    public static void handleArmorKey(Player player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            invokeClient(player.getItemBySlot(slot), player);
        }
    }

    private static void invokeClient(ItemStack stack, Player player) {
        if (!stack.isEmpty() && stack.getItem() instanceof IMPGKey keyItem) {
            keyItem.onManaitaKeyPressOnClient(stack, player);
        }
    }

}
