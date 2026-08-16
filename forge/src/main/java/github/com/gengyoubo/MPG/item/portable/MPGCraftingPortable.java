package github.com.gengyoubo.MPG.item.portable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;

public class MPGCraftingPortable extends MPGPortableItem {
    public MPGCraftingPortable() {
        super("item.portableCrafting.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.crafting_manaita",
                (containerId, inventory, player, heldStack, world) -> new MPGCraftingMenu(containerId, inventory, player.level()));
    }
}


