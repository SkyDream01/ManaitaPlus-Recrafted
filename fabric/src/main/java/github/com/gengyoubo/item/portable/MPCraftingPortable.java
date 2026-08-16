package github.com.gengyoubo.item.portable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import github.com.gengyoubo.menu.MPCraftingMenu;

public class MPCraftingPortable extends MPGPortableItem {
    public MPCraftingPortable() {
        super("item.portableCrafting.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.crafting_manaita",
                (containerId, inventory, player, heldStack, world) -> new MPCraftingMenu(containerId, inventory, player.level()));
    }
}




