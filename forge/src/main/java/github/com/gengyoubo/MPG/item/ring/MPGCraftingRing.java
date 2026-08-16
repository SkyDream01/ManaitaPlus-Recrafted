package github.com.gengyoubo.MPG.item.ring;

import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MPGCraftingRing extends MPGRingItem {
    public MPGCraftingRing() {
        super("item.ringCrafting.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.crafting_manaita",
                (containerId, inventory, player, heldStack, world) -> new MPGCraftingMenu(containerId, inventory, player.level()));
    }
}
