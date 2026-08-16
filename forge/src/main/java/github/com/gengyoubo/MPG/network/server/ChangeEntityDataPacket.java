package github.com.gengyoubo.MPG.network.server;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.event.network.CustomPayloadEvent;
import github.com.gengyoubo.MPG.network.ClientPacketHandlers;
import github.com.gengyoubo.MPG.MPG;
import org.jetbrains.annotations.NotNull;

public record ChangeEntityDataPacket(int id, int flag) implements CustomPacketPayload {
    public static final Type<ChangeEntityDataPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "change_entity_data"));
    public static final StreamCodec<FriendlyByteBuf, ChangeEntityDataPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull ChangeEntityDataPacket decode(FriendlyByteBuf buffer) {
            return new ChangeEntityDataPacket(buffer.readInt(), buffer.readInt());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, ChangeEntityDataPacket payload) {
            buffer.writeInt(payload.id);
            buffer.writeInt(payload.flag);
        }
    };

    public static void handle(ChangeEntityDataPacket payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (!context.isClientSide()) {
                return;
            }
            ClientPacketHandlers.handleChangeEntityData(payload);
        });
        context.setPacketHandled(true);
    }

    @Override
    public @NotNull Type<ChangeEntityDataPacket> type() {
        return TYPE;
    }
}
