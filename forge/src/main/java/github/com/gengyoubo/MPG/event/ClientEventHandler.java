package github.com.gengyoubo.MPG.event;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorageSource;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import github.com.gengyoubo.MPG.core.MPGKeyBoardCore;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.MPG.network.Networking;
import github.com.gengyoubo.MPG.network.client.KeyPressPacket;
import github.com.gengyoubo.MPG.network.client.OpenBaublesPacket;

import java.util.List;

public class ClientEventHandler {
    private static final Minecraft MC = Minecraft.getInstance();
    private static boolean registered = false;
    private static boolean autoLoadRequested = false;
    private static boolean autoLoadCheckLogged = false;

    public static void register() {
        if (registered) {
            return;
        }
        MinecraftForge.EVENT_BUS.register(new ClientEventHandler());
        registered = true;
        MPG.LOGGER.info("Registered client runtime event handlers");
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onKeyInput(InputEvent.Key event) {
        if (MC.player == null) return;
        while (MPGKeyBoardCore.MESSAGE_KEY != null && MPGKeyBoardCore.MESSAGE_KEY.consumeClick()) {
            ItemStack mainHandItem = MC.player.getMainHandItem();
            if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof IMPGKey keyItem) {
                keyItem.onManaitaKeyPressOnClient(mainHandItem, MC.player);
            }
            Networking.sendToServer(new KeyPressPacket((byte) 0));
        }
        while (MPGKeyBoardCore.MESSAGE_ARMOR_KEY != null && MPGKeyBoardCore.MESSAGE_ARMOR_KEY.consumeClick()) {
            for (ItemStack itemStack : MC.player.getInventory().armor) {
                if (!itemStack.isEmpty() && itemStack.getItem() instanceof IMPGKey keyItem) {
                    keyItem.onManaitaKeyPressOnClient(itemStack, MC.player);
                }
            }
            Networking.sendToServer(new KeyPressPacket((byte) 1));
        }
        while (BaublesCapability.isEnabled()
                && MPGKeyBoardCore.BAUBLES_KEY != null
                && MPGKeyBoardCore.BAUBLES_KEY.consumeClick()) {
            Networking.sendToServer(new OpenBaublesPacket());
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onClientTick(TickEvent.ClientTickEvent.Post event) {
        if (FMLEnvironment.production) {
            return;
        }
        if (MC.level != null || !(MC.screen instanceof TitleScreen)) {
            return;
        }
        if (!autoLoadCheckLogged) {
            autoLoadCheckLogged = true;
            MPG.LOGGER.info("Detected title screen in dev environment, checking for first-world auto-load");
        }
        if (autoLoadRequested) {
            return;
        }

        autoLoadRequested = true;
        tryAutoLoadFirstWorld();
    }

    private static void tryAutoLoadFirstWorld() {
        LevelStorageSource levelSource = MC.getLevelSource();
        LevelStorageSource.LevelCandidates candidates;
        try {
            candidates = levelSource.findLevelCandidates();
        } catch (LevelStorageException exception) {
            MPG.LOGGER.warn("Failed to enumerate local worlds for dev auto-load", exception);
            return;
        }

        if (candidates.isEmpty()) {
            MPG.LOGGER.info("No local worlds found, skipping dev auto-load");
            return;
        }

        levelSource.loadLevelSummaries(candidates).thenAccept((summaries) -> MC.execute(() -> loadFirstWorld(summaries)));
    }

    private static void loadFirstWorld(List<LevelSummary> summaries) {
        if (MC.level != null || !(MC.screen instanceof TitleScreen) || summaries.isEmpty()) {
            return;
        }

        LevelSummary firstWorld = summaries.get(0);
        MPG.LOGGER.info("Dev auto-loading first world: {}", firstWorld.getLevelId());
        MPG.LOGGER.info("Skipping dev auto-load on 1.21.1 until WorldOpenFlows migration is finished");
    }
}
