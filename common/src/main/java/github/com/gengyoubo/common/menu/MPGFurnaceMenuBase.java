package github.com.gengyoubo.common.menu;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.FurnaceResultSlot;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

public class MPGFurnaceMenuBase extends AbstractFurnaceMenu {
    protected MPGFurnaceMenuBase(MenuType<?> menuType, int containerId, Inventory inventory) {
        this(menuType, containerId, inventory, new UnlimitedSimpleContainer(), new SimpleContainerData(4));
    }

    protected MPGFurnaceMenuBase(MenuType<?> menuType, int containerId, Inventory inventory,
                                 Container container, ContainerData data) {
        super(menuType, RecipeType.SMELTING, RecipeBookType.FURNACE,
                containerId, inventory, container, data);
        replaceResultSlot(inventory);
    }

    private void replaceResultSlot(Inventory inventory) {
        Slot originalSlot = slots.get(2);
        UnlimitedFurnaceResultSlot replacement = new UnlimitedFurnaceResultSlot(
                inventory.player, originalSlot.container, originalSlot.x, originalSlot.y);
        replacement.index = originalSlot.index;
        slots.set(2, replacement);
    }

    private static final class UnlimitedSimpleContainer extends SimpleContainer {
        private UnlimitedSimpleContainer() {
            super(3);
        }

        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }
    }

    private static final class UnlimitedFurnaceResultSlot extends FurnaceResultSlot {
        private UnlimitedFurnaceResultSlot(Player player, Container container, int x, int y) {
            super(player, container, 2, x, y);
        }

        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return Integer.MAX_VALUE;
        }
    }
}
