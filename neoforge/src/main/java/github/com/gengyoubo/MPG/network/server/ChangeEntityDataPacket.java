package github.com.gengyoubo.MPG.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import github.com.gengyoubo.MPG.network.ClientPacketHandlers;
import github.com.gengyoubo.MPG.MPG;
import org.jetbrains.annotations.NotNull;

public record ChangeEntityDataPacket(int id, int flag) implements CustomPacketPayload {
    public static final Type<ChangeEntityDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "change_entity_data"));
    public static final StreamCodec<ByteBuf, ChangeEntityDataPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ChangeEntityDataPacket decode(ByteBuf buffer) {
            return new ChangeEntityDataPacket(buffer.readInt(), buffer.readInt());
        }

        @Override
        public void encode(ByteBuf buffer, ChangeEntityDataPacket payload) {
            buffer.writeInt(payload.id);
            buffer.writeInt(payload.flag);
        }
    };

    public static void handle(ChangeEntityDataPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.flow().isClientbound()) {
                return;
            }
            ClientPacketHandlers.handleChangeEntityData(payload);
        });
    }

    @Override
    public @NotNull Type<ChangeEntityDataPacket> type() {
        return TYPE;
    }
}

