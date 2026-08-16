package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGKeyBoardCore;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.common.event.MPGClientEventLogic;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import net.minecraft.client.Minecraft;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;

public class ClientEventHandler {
    private static final Minecraft MINECRAFT = Minecraft.getInstance();
    private static boolean registered;

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
        MPGClientEventLogic.tickDevWorldAutoLoad(FMLEnvironment.production, MPG.LOGGER);
    }
}
