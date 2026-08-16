package github.com.gengyoubo.network.client;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.util.MPResource;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public record MPKeyPressPacket(byte keyCode) implements CustomPacketPayload {
    public static final Type<MPKeyPressPacket> TYPE = new Type<>(MPResource.id(MPG.MODID, "key_press"));
    public static final StreamCodec<FriendlyByteBuf, MPKeyPressPacket> STREAM_CODEC =
            CustomPacketPayload.codec(MPKeyPressPacket::write, MPKeyPressPacket::new);

    private MPKeyPressPacket(FriendlyByteBuf buffer) {
        this(buffer.readByte());
    }

    private void write(FriendlyByteBuf buf) {
        buf.writeByte(keyCode);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void handle(ServerPlayer sender) {
        switch (keyCode) {
            case 0 -> {
                ItemStack mainHandItem = sender.getMainHandItem();
                if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof IMPGKey keyItem) {
                    keyItem.onManaitaKeyPress(mainHandItem, sender);
                }
            }
            case 1 -> {
                for (ItemStack itemStack : sender.getInventory().armor) {
                    if (!itemStack.isEmpty() && itemStack.getItem() instanceof IMPGKey keyItem) {
                        keyItem.onManaitaKeyPress(itemStack, sender);
                    }
                }
            }
            default -> {
            }
        }
    }
}
