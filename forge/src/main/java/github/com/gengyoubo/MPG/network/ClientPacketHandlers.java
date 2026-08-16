package github.com.gengyoubo.MPG.network;

import github.com.gengyoubo.common.network.MPGClientPayloadHandler;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;

public final class ClientPacketHandlers {
    private ClientPacketHandlers() {}

    public static void handleDestroyBlock(MPGDestroyBlockPayload packet) {
        MPGClientPayloadHandler.handleDestroyBlock(packet);
    }

    public static void handleChangeEntityData(MPGChangeEntityDataPayload packet) {
        MPGClientPayloadHandler.handleChangeEntityData(packet);
    }
}
