package github.com.gengyoubo.network.server;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.util.MPResource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;

public record MPDestroyBlockPacket(BlockPos blockPos, int range, Item item) implements CustomPacketPayload {
    public static final Type<MPDestroyBlockPacket> TYPE = new Type<>(MPResource.id(MPG.MODID, "destroy_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, MPDestroyBlockPacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            MPDestroyBlockPacket::blockPos,
            ByteBufCodecs.INT,
            MPDestroyBlockPacket::range,
            ByteBufCodecs.registry(net.minecraft.core.registries.Registries.ITEM),
            MPDestroyBlockPacket::item,
            MPDestroyBlockPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
