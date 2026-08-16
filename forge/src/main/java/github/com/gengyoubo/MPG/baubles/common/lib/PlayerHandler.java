package github.com.gengyoubo.MPG.baubles.common.lib;

import github.com.gengyoubo.MPG.baubles.api.BaubleType;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.container.InventoryBaubles;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public final class PlayerHandler {
    private PlayerHandler() {
    }

    public static InventoryBaubles getPlayerBaubles(Player player) {
        return BaublesCapability.get(player);
    }

    public static boolean equipItem(Player player, ItemStack stack) {
        return BaublesCapability.isEnabled() && getPlayerBaubles(player).equip(stack, player);
    }

    public static Optional<ItemStack> getEquippedRing(Player player) {
        return BaublesCapability.findPrimaryRing(player);
    }

    public static Optional<ItemStack> findFirstMatching(Player player, BaubleType type, Predicate<ItemStack> predicate) {
        return BaublesCapability.find(player).flatMap(inventory -> inventory.findFirstMatching(type, predicate));
    }
}
