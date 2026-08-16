package github.com.gengyoubo.network;

import github.com.gengyoubo.common.network.MPGKeyPressLogic;
import github.com.gengyoubo.common.network.payload.MPGChangeEntityDataPayload;
import github.com.gengyoubo.common.network.payload.MPGDestroyBlockPayload;
import github.com.gengyoubo.common.network.payload.MPGKeyPressPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public final class MPNetworking {
    public static final CustomPayloadId<MPGKeyPressPayload> KEY_PRESS =
            new CustomPayloadId<>(MPGKeyPressPayload.TYPE, MPGKeyPressPayload.STREAM_CODEC);
    public static final CustomPayloadId<MPGDestroyBlockPayload> DESTROY_BLOCK =
            new CustomPayloadId<>(MPGDestroyBlockPayload.TYPE, MPGDestroyBlockPayload.STREAM_CODEC);
    public static final CustomPayloadId<MPGChangeEntityDataPayload> CHANGE_ENTITY_DATA =
            new CustomPayloadId<>(MPGChangeEntityDataPayload.TYPE, MPGChangeEntityDataPayload.STREAM_CODEC);
    private static boolean payloadsRegistered;

    private MPNetworking() {
    }

    public static void initCommon() {
        if (payloadsRegistered) {
            return;
        }

        PayloadTypeRegistry.playC2S().register(KEY_PRESS.type(), KEY_PRESS.codec());
        PayloadTypeRegistry.playS2C().register(DESTROY_BLOCK.type(), DESTROY_BLOCK.codec());
        PayloadTypeRegistry.playS2C().register(CHANGE_ENTITY_DATA.type(), CHANGE_ENTITY_DATA.codec());
        payloadsRegistered = true;
    }

    public static void initServer() {
        initCommon();
        ServerPlayNetworking.registerGlobalReceiver(KEY_PRESS.type(), (payload, context) -> {
            MPGKeyPressLogic.handle(context.player(), payload.keyCode());
        });
    }

    public static void sendToSameLevelPlayers(Level level, MPGDestroyBlockPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                ServerPlayNetworking.send(serverPlayer, packet);
            }
        }
    }

    public static void sendToSameLevelPlayers(Level level, MPGChangeEntityDataPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                ServerPlayNetworking.send(serverPlayer, packet);
            }
        }
    }

    @Deprecated
    public static void sendToNearByPlayers(Level level, MPGDestroyBlockPayload packet, int range) {
        sendToSameLevelPlayers(level, packet);
    }

    public static void sendToTrackBySeen(Level level, Player player, MPGDestroyBlockPayload packet) {
        if (level instanceof ServerLevel serverLevel) {
            for (ServerPlayer serverPlayer : serverLevel.players()) {
                if (serverPlayer == player || serverPlayer.distanceToSqr(player) <= 128.0D * 128.0D) {
                    ServerPlayNetworking.send(serverPlayer, packet);
                }
            }
        }
    }

    public record CustomPayloadId<T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload>(
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
            net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, T> codec
    ) {
    }
}
