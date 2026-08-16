package github.com.gengyoubo.menu;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.item.ItemStack;

final class UnlimitedFurnaceResultSlot extends FurnaceResultSlot {
    UnlimitedFurnaceResultSlot(Player player, Container container, int slot, int x, int y) {
        super(player, container, slot, x, y);
    }

    @Override
    public int getMaxStackSize() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return Integer.MAX_VALUE;
    }
}
