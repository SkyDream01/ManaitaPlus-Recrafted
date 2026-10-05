package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record MPGChangeEntityDataPayload(int entityId, int flags) implements CustomPacketPayload {
    public static final Type<MPGChangeEntityDataPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(MPGCommon.MOD_ID, "change_entity_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MPGChangeEntityDataPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull MPGChangeEntityDataPayload decode(RegistryFriendlyByteBuf buffer) {
            return new MPGChangeEntityDataPayload(buffer.readInt(), buffer.readInt());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, MPGChangeEntityDataPayload payload) {
            buffer.writeInt(payload.entityId());
            buffer.writeInt(payload.flags());
        }
    };

    @Override
    public @NotNull Type<MPGChangeEntityDataPayload> type() {
        return TYPE;
    }
}
