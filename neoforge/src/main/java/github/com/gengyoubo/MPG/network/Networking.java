package github.com.gengyoubo.MPG.network;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import github.com.gengyoubo.common.network.MPGKeyPressLogic;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGBucketCornerPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import github.com.gengyoubo.MPG.item.MPGBucketItem;

public class Networking {
    public static final String VERSION = "1.0";

    public static void registerMessage(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToServer(MPGKeyPressPayload.TYPE, MPGKeyPressPayload.STREAM_CODEC, Networking::handleKeyPress);
        registrar.playToServer(MPGBucketCornerPayload.TYPE, MPGBucketCornerPayload.STREAM_CODEC, Networking::handleBucketCorner);
        registrar.playToClient(MPGDestroyBlockPayload.TYPE, MPGDestroyBlockPayload.STREAM_CODEC, Networking::handleDestroyBlock);
        registrar.playToClient(MPGChangeEntityDataPayload.TYPE, MPGChangeEntityDataPayload.STREAM_CODEC, Networking::handleChangeEntityData);
    }

    private static void handleKeyPress(MPGKeyPressPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.flow().isClientbound() && context.player() instanceof ServerPlayer player) {
                MPGKeyPressLogic.handle(player, payload.keyCode());
            }
        });
    }

    private static void handleBucketCorner(MPGBucketCornerPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.flow().isClientbound() && context.player() instanceof ServerPlayer player) {
                MPGBucketItem.selectFirstCorner(player, payload.pos());
            }
        });
    }

    private static void handleDestroyBlock(MPGDestroyBlockPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                ClientPacketHandlers.handleDestroyBlock(payload);
            }
        });
    }

    private static void handleChangeEntityData(MPGChangeEntityDataPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                ClientPacketHandlers.handleChangeEntityData(payload);
            }
        });
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
            // PacketDistributor#sendToServer moved to the client-only ClientPacketDistributor in 26.3;
            // this method is only ever called from client code.
            net.neoforged.neoforge.client.network.ClientPacketDistributor.sendToServer(payload);
        }
    }

    public static void sendToTrackBySeen(Level level, Player player, Object packet) {
        if (level instanceof ServerLevel && packet instanceof net.minecraft.network.protocol.common.custom.CustomPacketPayload payload) {
            PacketDistributor.sendToPlayersTrackingEntity(player, payload);
        }
    }
}
