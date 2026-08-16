package github.com.gengyoubo;

import com.mojang.blaze3d.platform.InputConstants;
import github.com.gengyoubo.core.MPKeyBoardCore;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.network.MPNetworking;
import github.com.gengyoubo.network.client.MPKeyPressPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.glfw.GLFW;

public final class MPGKeyBindings {
    private MPGKeyBindings() {
    }

    public static void init() {
        MPKeyBoardCore.MESSAGE_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.manaita",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                "key.categories.misc"
        ));
        MPKeyBoardCore.MESSAGE_ARMOR_KEY = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.manaita.armor",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_Y,
                "key.categories.misc"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(MPGKeyBindings::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        if (client.player == null) {
            return;
        }

        while (MPKeyBoardCore.MESSAGE_KEY.consumeClick()) {
            ItemStack mainHandItem = client.player.getMainHandItem();
            if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof IMPGKey keyItem) {
                keyItem.onManaitaKeyPressOnClient(mainHandItem, client.player);
            }
            sendKeyPacket((byte) 0);
        }

        while (MPKeyBoardCore.MESSAGE_ARMOR_KEY.consumeClick()) {
            for (ItemStack itemStack : client.player.getInventory().armor) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof IMPGKey keyItem) {
                    keyItem.onManaitaKeyPressOnClient(itemStack, client.player);
                }
            }
            sendKeyPacket((byte) 1);
        }
    }

    private static void sendKeyPacket(byte keyCode) {
        ClientPlayNetworking.send(new MPKeyPressPacket(keyCode));
    }
}
