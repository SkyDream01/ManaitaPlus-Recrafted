package github.com.gengyoubo.MPG.network.client;

import github.com.gengyoubo.common.item.data.IMPGKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import github.com.gengyoubo.MPG.MPG;
import org.jetbrains.annotations.NotNull;

public class KeyPressPacket implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<KeyPressPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "key_press"));
    public static final StreamCodec<RegistryFriendlyByteBuf, KeyPressPacket> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull KeyPressPacket decode(RegistryFriendlyByteBuf buffer) {
            return new KeyPressPacket(buffer.readByte());
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buffer, KeyPressPacket payload) {
            buffer.writeByte(payload.keyCode);
        }
    };

    private final byte keyCode;

    public KeyPressPacket(byte key) {
        this.keyCode = key;
    }

    public static void handle(KeyPressPacket payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.flow().isClientbound()) {
                return;
            }
            if (!(context.player() instanceof ServerPlayer sender)) {
                return;
            }
            switch (payload.keyCode) {
                case 0 -> {
                    ItemStack mainHandItem = sender.getMainHandItem();
                    if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof IMPGKey keyItem) {
                        keyItem.onManaitaKeyPress(mainHandItem);
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
    }

    @Override
    public CustomPacketPayload.@NotNull Type<KeyPressPacket> type() {
        return TYPE;
    }
}
