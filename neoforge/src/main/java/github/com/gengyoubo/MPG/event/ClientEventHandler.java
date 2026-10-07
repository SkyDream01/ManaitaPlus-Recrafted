package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGKeyBoardCore;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.event.MPGClientEventLogic;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEventHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static boolean registered;
    private static boolean legacyBindingsChecked;

    public static void register() {
        if (!registered) {
            NeoForge.EVENT_BUS.register(new ClientEventHandler());
            registered = true;
            MPG.LOGGER.info("Registered client runtime event handlers");
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.Key event) {
        if (MINECRAFT.player == null) {
            return;
        }
        while (MPGKeyBoardCore.MESSAGE_KEY != null && MPGKeyBoardCore.MESSAGE_KEY.consumeClick()) {
            MPGClientEventLogic.handleMainHandKey(MINECRAFT.player);
            Networking.sendToServer(new MPGKeyPressPayload((byte) 0));
        }
        while (MPGKeyBoardCore.MESSAGE_ARMOR_KEY != null && MPGKeyBoardCore.MESSAGE_ARMOR_KEY.consumeClick()) {
            MPGClientEventLogic.handleArmorKey(MINECRAFT.player);
            Networking.sendToServer(new MPGKeyPressPayload((byte) 1));
        }
        while (MPGKeyBoardCore.PAXEL_KEY != null && MPGKeyBoardCore.PAXEL_KEY.consumeClick()) {
            MPGClientEventLogic.handlePaxelKey(MINECRAFT.player);
            Networking.sendToServer(new MPGKeyPressPayload((byte) 2));
        }
    }

    @SubscribeEvent
    public void onClientTick(ClientTickEvent.Post event) {
        migrateLegacyBindings();
        // FMLEnvironment.production is now the isProduction() accessor in 26.3.
        MPGClientEventLogic.tickDevWorldAutoLoad(FMLEnvironment.isProduction(), MPG.LOGGER);
    }

    private static void migrateLegacyBindings() {
        if (legacyBindingsChecked || MPGKeyBoardCore.MESSAGE_KEY == null
                || MPGKeyBoardCore.MESSAGE_ARMOR_KEY == null || MPGKeyBoardCore.PAXEL_KEY == null) {
            return;
        }
        legacyBindingsChecked = true;

        InputConstants.Type keyboard = InputConstants.Type.KEYBOARD;
        // Only migrate the complete set of incorrect defaults from the first 26.3 port.
        // A single matching key may be an intentional user binding.
        if (!MPGKeyBoardCore.MESSAGE_KEY.getKey().equals(keyboard.getOrCreate(88))
                || !MPGKeyBoardCore.MESSAGE_ARMOR_KEY.getKey().equals(keyboard.getOrCreate(86))
                || !MPGKeyBoardCore.PAXEL_KEY.getKey().equals(keyboard.getOrCreate(67))) {
            return;
        }

        MPGKeyBoardCore.MESSAGE_KEY.setKey(keyboard.getOrCreate(InputConstants.KEY_X));
        MPGKeyBoardCore.MESSAGE_ARMOR_KEY.setKey(keyboard.getOrCreate(InputConstants.KEY_V));
        MPGKeyBoardCore.PAXEL_KEY.setKey(keyboard.getOrCreate(InputConstants.KEY_C));
        KeyMapping.resetMapping();
        MINECRAFT.options.save();
        MPG.LOGGER.info("Migrated legacy Manaita key bindings to X, V, and C");
    }
}
