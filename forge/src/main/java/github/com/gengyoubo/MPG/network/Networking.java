package github.com.gengyoubo.MPG.network;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.baubles.common.lib.PlayerHandler;
import github.com.gengyoubo.MPG.network.client.OpenBaublesPacket;
import github.com.gengyoubo.common.network.MPGKeyPressLogic;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class Networking {
    public static final int VERSION = 1;
    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "main"))
            .networkProtocolVersion(VERSION)
            .clientAcceptedVersions((status, version) -> true)
            .serverAcceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void registerMessage() {
        CHANNEL.messageBuilder(MPGKeyPressPayload.class, 0)
                .direction(PacketFlow.SERVERBOUND)
                .codec(MPGKeyPressPayload.STREAM_CODEC)
                .consumerMainThread(Networking::handleKeyPress)
                .add();
        CHANNEL.messageBuilder(OpenBaublesPacket.class, 1)
                .direction(PacketFlow.SERVERBOUND)
                .codec(OpenBaublesPacket.STREAM_CODEC)
                .consumerMainThread(OpenBaublesPacket::handle)
                .add();
        CHANNEL.messageBuilder(MPGDestroyBlockPayload.class, 2)
                .direction(PacketFlow.CLIENTBOUND)
                .codec(MPGDestroyBlockPayload.STREAM_CODEC)
                .consumerMainThread(Networking::handleDestroyBlock)
                .add();
        CHANNEL.messageBuilder(MPGChangeEntityDataPayload.class, 3)
                .direction(PacketFlow.CLIENTBOUND)
                .codec(MPGChangeEntityDataPayload.STREAM_CODEC)
                .consumerMainThread(Networking::handleChangeEntityData)
                .add();
    }

    private static void handleKeyPress(MPGKeyPressPayload payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.isClientSide() || !(context.getSender() instanceof ServerPlayer player)) {
                return;
            }
            if (payload.keyCode() == 0) {
                ItemStack ring = PlayerHandler.getEquippedRing(player).orElse(ItemStack.EMPTY);
                if (MPGKeyPressLogic.invoke(ring, player)) {
                    return;
                }
            }
            MPGKeyPressLogic.handle(player, payload.keyCode());
        });
        context.setPacketHandled(true);
    }

    private static void handleDestroyBlock(MPGDestroyBlockPayload payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.isClientSide()) {
                ClientPacketHandlers.handleDestroyBlock(payload);
            }
        });
        context.setPacketHandled(true);
    }

    private static void handleChangeEntityData(MPGChangeEntityDataPayload payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.isClientSide()) {
                ClientPacketHandlers.handleChangeEntityData(payload);
            }
        });
        context.setPacketHandled(true);
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
