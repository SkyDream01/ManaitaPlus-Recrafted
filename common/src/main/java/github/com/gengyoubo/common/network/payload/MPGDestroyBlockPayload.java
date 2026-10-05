package github.com.gengyoubo.common.network.payload;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.NotNull;

public record MPGDestroyBlockPayload(BlockPos blockPos, int range, Item item) implements CustomPacketPayload {
    public static final Type<MPGDestroyBlockPayload> TYPE = new Type<>(
            Identifier.fromNamespaceAndPath(MPGCommon.MOD_ID, "destroy_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MPGDestroyBlockPayload> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull MPGDestroyBlockPayload decode(RegistryFriendlyByteBuf buffer) {
            BlockPos position = BlockPos.STREAM_CODEC.decode(buffer);
            int range = buffer.readInt();
            Item item = BuiltInRegistries.ITEM.getValue(Identifier.STREAM_CODEC.decode(buffer));
            return new MPGDestroyBlockPayload(position, range, item);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, MPGDestroyBlockPayload payload) {
            BlockPos.STREAM_CODEC.encode(buffer, payload.blockPos());
            buffer.writeInt(payload.range());
            Identifier.STREAM_CODEC.encode(buffer, BuiltInRegistries.ITEM.getKey(payload.item()));
        }
    };

    @Override
    public @NotNull Type<MPGDestroyBlockPayload> type() {
        return TYPE;
    }
}
