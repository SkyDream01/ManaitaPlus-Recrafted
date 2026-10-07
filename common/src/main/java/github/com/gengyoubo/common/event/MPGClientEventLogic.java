package github.com.gengyoubo.common.event;

import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.item.data.IMPGOffhandKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.LevelStorageException;
import net.minecraft.world.level.storage.LevelSummary;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.slf4j.Logger;

import java.util.List;

/** Loader-neutral client event actions. */
public final class MPGClientEventLogic {
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static boolean autoLoadRequested;
    private static boolean autoLoadCheckLogged;

    private MPGClientEventLogic() {
    }

    public static void handleMainHandKey(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof IMPGKey keyItem) || keyItem.usesStandardModeKey()) {
            invokeClient(stack, player);
        }
        ItemStack offhand = player.getOffhandItem();
        if (!(stack.getItem() instanceof IMPGOffhandKey)
                && offhand.getItem() instanceof IMPGOffhandKey) {
            invokeClient(offhand, player);
        }
    }

    public static void handlePaxelKey(Player player) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof IMPGKey keyItem && keyItem.usesDedicatedDoublingKey()) {
            keyItem.onDedicatedDoublingKeyOnClient(stack, player);
        }
    }

    public static void handleArmorKey(Player player) {
        for (EquipmentSlot slot : ARMOR_SLOTS) {
            invokeClient(player.getItemBySlot(slot), player);
        }
    }

    private static void invokeClient(ItemStack stack, Player player) {
        if (!stack.isEmpty() && stack.getItem() instanceof IMPGKey keyItem) {
            keyItem.onManaitaKeyPressOnClient(stack, player);
        }
    }

    public static void tickDevWorldAutoLoad(boolean production, Logger logger) {
        if (production) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level != null || !(minecraft.gui.screen() instanceof TitleScreen)) {
            return;
        }
        if (!autoLoadCheckLogged) {
            autoLoadCheckLogged = true;
            logger.info("Detected title screen in dev environment, checking for first-world auto-load");
        }
        if (autoLoadRequested) {
            return;
        }
        autoLoadRequested = true;
        tryAutoLoadFirstWorld(minecraft, logger);
    }

    private static void tryAutoLoadFirstWorld(Minecraft minecraft, Logger logger) {
        LevelStorageSource levelSource = minecraft.getLevelSource();
        LevelStorageSource.LevelCandidates candidates;
        try {
            candidates = levelSource.findLevelCandidates();
        } catch (LevelStorageException exception) {
            logger.warn("Failed to enumerate local worlds for dev auto-load", exception);
            return;
        }

        if (candidates.isEmpty()) {
            logger.info("No local worlds found, skipping dev auto-load");
            return;
        }
        levelSource.loadLevelSummaries(candidates)
                .thenAccept(summaries -> minecraft.execute(() -> logFirstWorld(minecraft, summaries, logger)));
    }

    private static void logFirstWorld(Minecraft minecraft, List<LevelSummary> summaries, Logger logger) {
        if (minecraft.level != null || !(minecraft.gui.screen() instanceof TitleScreen) || summaries.isEmpty()) {
            return;
        }
        logger.info("Dev auto-loading first world: {}", summaries.getFirst().getLevelId());
        logger.info("Skipping dev auto-load on 1.21.1 until WorldOpenFlows migration is finished");
    }
}
