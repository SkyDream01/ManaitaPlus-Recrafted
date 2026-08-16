package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public record MPGKeyPressPayload(byte keyCode) implements CustomPacketPayload {
    public static final Type<MPGKeyPressPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(MPGCommon.MOD_ID, "key_press"));
    public static final StreamCodec<FriendlyByteBuf, MPGKeyPressPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull MPGKeyPressPayload decode(FriendlyByteBuf buffer) {
            return new MPGKeyPressPayload(buffer.readByte());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, MPGKeyPressPayload payload) {
            buffer.writeByte(payload.keyCode());
        }
    };

    @Override
    public @NotNull Type<MPGKeyPressPayload> type() {
        return TYPE;
    }
}
