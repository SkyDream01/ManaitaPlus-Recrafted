package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record MPGKeyPressPayload(byte keyCode) implements CustomPacketPayload {
    public static final Type<MPGKeyPressPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(MPGCommon.MOD_ID, "key_press"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MPGKeyPressPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull MPGKeyPressPayload decode(RegistryFriendlyByteBuf buffer) {
            return new MPGKeyPressPayload(buffer.readByte());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, MPGKeyPressPayload payload) {
            buffer.writeByte(payload.keyCode());
        }
    };

    @Override
    public @NotNull Type<MPGKeyPressPayload> type() {
        return TYPE;
    }
}
