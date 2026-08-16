package github.com.gengyoubo.MPG.baubles.api;

import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.container.InventoryBaubles;
import net.minecraft.world.entity.player.Player;

public final class BaublesApi {
    private BaublesApi() {
    }

    public static boolean isBaublesAvailable() {
        return BaublesCapability.isEnabled();
    }

    public static InventoryBaubles getBaubles(Player player) {
        return BaublesCapability.get(player);
    }
}
