package github.com.gengyoubo.MPG.network.server;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.network.CustomPayloadEvent;
import github.com.gengyoubo.MPG.network.ClientPacketHandlers;
import github.com.gengyoubo.MPG.MPG;
import org.jetbrains.annotations.NotNull;

public record DestroyBlockPacket(BlockPos blockPos, int range, Item item) implements CustomPacketPayload {
    public static final Type<DestroyBlockPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "destroy_block"));
    public static final StreamCodec<FriendlyByteBuf, DestroyBlockPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull DestroyBlockPacket decode(@NotNull FriendlyByteBuf buffer) {
            BlockPos blockPos = BlockPos.STREAM_CODEC.decode(buffer);
            int range = buffer.readInt();
            ResourceLocation itemId = ResourceLocation.STREAM_CODEC.decode(buffer);
            Item item = BuiltInRegistries.ITEM.get(itemId);
            return new DestroyBlockPacket(blockPos, range, item);
        }

        @Override
        public void encode(@NotNull FriendlyByteBuf buffer, DestroyBlockPacket payload) {
            BlockPos.STREAM_CODEC.encode(buffer, payload.blockPos);
            buffer.writeInt(payload.range);
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(payload.item);
            ResourceLocation.STREAM_CODEC.encode(buffer, itemId);
        }
    };

    public static void handle(DestroyBlockPacket payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (!context.isClientSide()) {
                return;
            }
            ClientPacketHandlers.handleDestroyBlock(payload);
        });
        context.setPacketHandled(true);
    }

    @Override
    public @NotNull Type<DestroyBlockPacket> type() {
        return TYPE;
    }
}
