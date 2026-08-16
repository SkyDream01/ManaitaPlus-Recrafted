package github.com.gengyoubo.MPG.network.client;

import github.com.gengyoubo.MPG.baubles.common.lib.PlayerHandler;
import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import github.com.gengyoubo.MPG.MPG;
import org.jetbrains.annotations.NotNull;

public class KeyPressPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<KeyPressPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "key_press"));
    public static final StreamCodec<FriendlyByteBuf, KeyPressPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull KeyPressPacket decode(FriendlyByteBuf buffer) {
            return new KeyPressPacket(buffer.readByte());
        }

        @Override
        public void encode(FriendlyByteBuf buffer, KeyPressPacket payload) {
            buffer.writeByte(payload.keyCode);
        }
    };

    private final byte keyCode;

    public KeyPressPacket(byte key) {
        this.keyCode = key;
    }

    public static void handle(KeyPressPacket payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.isClientSide()) {
                return;
            }
            if (!(context.getSender() instanceof ServerPlayer sender)) {
                return;
            }
            switch (payload.keyCode) {
                case 0 -> {
                    ItemStack equippedRing = PlayerHandler.getEquippedRing(sender).orElse(ItemStack.EMPTY);
                    if (!equippedRing.isEmpty() && equippedRing.getItem() instanceof IMPGKey keyItem) {
                        keyItem.onManaitaKeyPress(equippedRing, sender);
                        return;
                    }
                    ItemStack mainHandItem = sender.getMainHandItem();
                    if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof IMPGKey keyItem) {
                        keyItem.onManaitaKeyPress(mainHandItem, sender);
                    }
                }
                case 1 -> {
                    for (ItemStack itemStack : sender.getInventory().armor) {
                        if (!itemStack.isEmpty() && itemStack.getItem() instanceof IMPGKey keyItem) {
                            keyItem.onManaitaKeyPress(itemStack);
                        }
                    }
                }
                default -> {
                }
            }
        });
        context.setPacketHandled(true);
    }

    @Override
    public CustomPacketPayload.@NotNull Type<KeyPressPacket> type() {
        return TYPE;
    }
}
