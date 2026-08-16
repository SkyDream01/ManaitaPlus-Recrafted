package github.com.gengyoubo.MPG.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import github.com.gengyoubo.MPG.network.client.KeyPressPacket;
import github.com.gengyoubo.MPG.network.server.DestroyBlockPacket;
import github.com.gengyoubo.MPG.network.server.ChangeEntityDataPacket;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class Networking {
    public static final String VERSION = "1.0";

    public static void registerMessage(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(KeyPressPacket.TYPE, KeyPressPacket.STREAM_CODEC, KeyPressPacket::handle);
        registrar.playToClient(DestroyBlockPacket.TYPE, DestroyBlockPacket.STREAM_CODEC, DestroyBlockPacket::handle);
        registrar.playToClient(ChangeEntityDataPacket.TYPE, ChangeEntityDataPacket.STREAM_CODEC, ChangeEntityDataPacket::handle);
    }

    public static void sendToSameLevelPlayers(Level level, Object packet) {
        if (level instanceof ServerLevel serverLevel && packet instanceof net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
            PacketDistributor.sendToPlayersInDimension(serverLevel, payload);
        }
    }

// --娉ㄩ噴鎺夋鏌?START (2026/4/24 23:35):
//    public static void sendToNearByPlayers(Level level, Player center, Object packet, int range) {
//        if (level instanceof ServerLevel serverLevel) {
//            if (center == null) {
//                sendToSameLevelPlayers(level, packet);
//                return;
//            }
//            double finalRange = (double) range * (double) range;
//            for (ServerPlayer serverPlayer : serverLevel.players()) {
//                if (serverPlayer.distanceToSqr(center) <= finalRange) {
//                    Networking.INSTANCE.send(
//                            PacketDistributor.PLAYER.with(() -> serverPlayer),
//                            packet
//                    );
//                }
//            }
//        }
//    }
// --娉ㄩ噴鎺夋鏌?STOP (2026/4/24 23:35)

    @Deprecated
    public static void sendToNearByPlayers(Level level, Object packet, int range) {
        sendToSameLevelPlayers(level, packet);
    }

    public static void sendToServer(Object packet) {
        if (packet instanceof net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
            PacketDistributor.sendToServer(payload);
        }
    }

    public static void sendToTrackBySeen(Level level, Player player, Object packet) {
        if (level instanceof ServerLevel && packet instanceof net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
            PacketDistributor.sendToPlayersTrackingEntity(player, payload);
        }
    }
}

