package github.com.gengyoubo.MPG.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.crafting.RecipeType;
import github.com.gengyoubo.MPG.core.MPGMenuCore;
import org.jetbrains.annotations.NotNull;

public class MPGFurnaceMenu extends AbstractFurnaceMenu {
    @SuppressWarnings("unused")
    public MPGFurnaceMenu(int p_39532_, Inventory p_39533_, FriendlyByteBuf extraData) {
        super(MPGMenuCore.FurnaceManaita.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39532_, p_39533_, new UnlimitedSimpleContainer(), new net.minecraft.world.inventory.SimpleContainerData(4));
        replaceResultSlot(p_39533_);
    }

    public MPGFurnaceMenu(int p_39535_, Inventory p_39536_, Container p_39537_, ContainerData p_39538_) {
        super(MPGMenuCore.FurnaceManaita.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39535_, p_39536_, p_39537_, p_39538_);
        replaceResultSlot(p_39536_);
    }

    private void replaceResultSlot(Inventory inventory) {
        Slot originalSlot = this.slots.get(2);
        UnlimitedFurnaceResultSlot replacement = new UnlimitedFurnaceResultSlot(
                inventory.player,
                originalSlot.container,
                originalSlot.x,
                originalSlot.y
        );
        replacement.index = originalSlot.index;
        this.slots.set(2, replacement);
    }

    private static class UnlimitedSimpleContainer extends SimpleContainer {
        private UnlimitedSimpleContainer() {
            super(3);
        }

        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }
    }

    private static class UnlimitedFurnaceResultSlot extends FurnaceResultSlot {
        private UnlimitedFurnaceResultSlot(Player player, Container container, int x, int y) {
            super(player, container, 2, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxStackSize(net.minecraft.world.item.@NotNull ItemStack stack) {
            return Integer.MAX_VALUE;
        }
    }

}
