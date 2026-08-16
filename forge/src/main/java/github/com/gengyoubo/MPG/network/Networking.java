package github.com.gengyoubo.MPG.network;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.network.client.KeyPressPacket;
import github.com.gengyoubo.MPG.network.client.OpenBaublesPacket;
import github.com.gengyoubo.MPG.network.server.ChangeEntityDataPacket;
import github.com.gengyoubo.MPG.network.server.DestroyBlockPacket;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class Networking {
    public static final int VERSION = 1;
    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "main"))
            .networkProtocolVersion(VERSION)
            .clientAcceptedVersions((status, version) -> true)
            .serverAcceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void registerMessage() {
        CHANNEL.messageBuilder(KeyPressPacket.class, 0)
                .direction(PacketFlow.SERVERBOUND)
                .codec(KeyPressPacket.STREAM_CODEC)
                .consumerMainThread(KeyPressPacket::handle)
                .add();
        CHANNEL.messageBuilder(OpenBaublesPacket.class, 1)
                .direction(PacketFlow.SERVERBOUND)
                .codec(OpenBaublesPacket.STREAM_CODEC)
                .consumerMainThread(OpenBaublesPacket::handle)
                .add();
        CHANNEL.messageBuilder(DestroyBlockPacket.class, 2)
                .direction(PacketFlow.CLIENTBOUND)
                .codec(DestroyBlockPacket.STREAM_CODEC)
                .consumerMainThread(DestroyBlockPacket::handle)
                .add();
        CHANNEL.messageBuilder(ChangeEntityDataPacket.class, 3)
                .direction(PacketFlow.CLIENTBOUND)
                .codec(ChangeEntityDataPacket.STREAM_CODEC)
                .consumerMainThread(ChangeEntityDataPacket::handle)
                .add();
    }

    public static void sendToSameLevelPlayers(Level level, Object packet) {
        if (level instanceof ServerLevel serverLevel && packet instanceof CustomPacketPayload payload) {
            CHANNEL.send(payload, PacketDistributor.DIMENSION.with(serverLevel.dimension()));
        }
    }

// --濞夈劑鍣撮幒澶嬵梾閺?START (2026/4/24 23:35):
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
// --濞夈劑鍣撮幒澶嬵梾閺?STOP (2026/4/24 23:35)

    @Deprecated
    public static void sendToNearByPlayers(Level level, Object packet, int range) {
        sendToSameLevelPlayers(level, packet);
    }

    public static void sendToServer(Object packet) {
        if (packet instanceof CustomPacketPayload payload) {
            CHANNEL.send(payload, PacketDistributor.SERVER.noArg());
        }
    }

    public static void sendToTrackBySeen(Level level, Player player, Object packet) {
        if (level instanceof ServerLevel && packet instanceof CustomPacketPayload payload) {
            CHANNEL.send(payload, PacketDistributor.TRACKING_ENTITY.with(player));
        }
    }
}
