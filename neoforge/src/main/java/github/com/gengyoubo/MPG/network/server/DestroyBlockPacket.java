package github.com.gengyoubo.MPG.network.server;

import net.minecraft.core.BlockPos;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import github.com.gengyoubo.MPG.network.ClientPacketHandlers;
import github.com.gengyoubo.MPG.MPG;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public record DestroyBlockPacket(BlockPos blockPos, int range, Item item) implements CustomPacketPayload {
    public static final Type<DestroyBlockPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "destroy_block"));
    public static final StreamCodec<ByteBuf, DestroyBlockPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull DestroyBlockPacket decode(@NotNull ByteBuf buffer) {
            BlockPos blockPos = BlockPos.STREAM_CODEC.decode(buffer);
            int range = buffer.readInt();
            ResourceLocation itemId = ResourceLocation.STREAM_CODEC.decode(buffer);
            Item item = BuiltInRegistries.ITEM.get(itemId);
            return new DestroyBlockPacket(blockPos, range, item);
        }

        @Override
        public void encode(@NotNull ByteBuf buffer, DestroyBlockPacket payload) {
            BlockPos.STREAM_CODEC.encode(buffer, payload.blockPos);
            buffer.writeInt(payload.range);
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(payload.item);
            ResourceLocation.STREAM_CODEC.encode(buffer, itemId);
        }
    };

    public static void handle(DestroyBlockPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!context.flow().isClientbound()) {
                return;
            }
            ClientPacketHandlers.handleDestroyBlock(payload);
        });
    }

    @Override
    public @NotNull Type<DestroyBlockPacket> type() {
        return TYPE;
    }
}

