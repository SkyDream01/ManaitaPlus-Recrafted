package github.com.gengyoubo.network.server;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.util.MPResource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MPChangeEntityDataPacket(int id, int flag) implements CustomPacketPayload {
    public static final Type<MPChangeEntityDataPacket> TYPE = new Type<>(MPResource.id(MPG.MODID, "change_entity_data"));
    public static final StreamCodec<FriendlyByteBuf, MPChangeEntityDataPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT,
            MPChangeEntityDataPacket::id,
            ByteBufCodecs.INT,
            MPChangeEntityDataPacket::flag,
            MPChangeEntityDataPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
