package github.com.gengyoubo.MPG.network.client;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.lib.PlayerHandler;
import github.com.gengyoubo.MPG.menu.MPGBaublesMenu;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import org.jetbrains.annotations.NotNull;

public record OpenBaublesPacket() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<OpenBaublesPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(MPG.MODID, "open_baubles"));
    public static final StreamCodec<FriendlyByteBuf, OpenBaublesPacket> STREAM_CODEC = StreamCodec.unit(new OpenBaublesPacket());

    public static void handle(OpenBaublesPacket payload, CustomPayloadEvent.Context context) {
        context.enqueueWork(() -> {
            if (context.isClientSide() || !(context.getSender() instanceof ServerPlayer sender) || !BaublesCapability.isEnabled()) {
                return;
            }
            sender.openMenu(new net.minecraft.world.SimpleMenuProvider(
                    (containerId, inventory, player) -> new MPGBaublesMenu(containerId, inventory, PlayerHandler.getPlayerBaubles(player)),
                    Component.translatable("container.manaita_baubles")
            ));
        });
        context.setPacketHandled(true);
    }

    @Override
    public @NotNull Type<OpenBaublesPacket> type() {
        return TYPE;
    }
}
