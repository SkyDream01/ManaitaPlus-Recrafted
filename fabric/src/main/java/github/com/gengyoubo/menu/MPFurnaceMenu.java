package github.com.gengyoubo.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.crafting.RecipeType;
import github.com.gengyoubo.core.MPMenuCore;

public class MPFurnaceMenu extends AbstractFurnaceMenu {
    @SuppressWarnings("unused")
    public MPFurnaceMenu(int p_39532_, Inventory p_39533_, BlockPos blockPos) {
        super(MPMenuCore.FurnaceManaita.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39532_, p_39533_, new UnlimitedSimpleContainer(), new SimpleContainerData(4));
        replaceResultSlot(p_39533_);
    }

    public MPFurnaceMenu(int p_39535_, Inventory p_39536_, Container p_39537_, ContainerData p_39538_) {
        super(MPMenuCore.FurnaceManaita.get(), RecipeType.SMELTING, RecipeBookType.FURNACE, p_39535_, p_39536_, p_39537_, p_39538_);
        replaceResultSlot(p_39536_);
    }

    private void replaceResultSlot(Inventory inventory) {
        net.minecraft.world.inventory.FurnaceResultSlot originalSlot = (net.minecraft.world.inventory.FurnaceResultSlot) this.slots.get(2);
        UnlimitedFurnaceResultSlot replacement = new UnlimitedFurnaceResultSlot(inventory.player, originalSlot.container, 2, originalSlot.x, originalSlot.y);
        replacement.index = originalSlot.index;
        this.slots.set(2, replacement);
    }

    private static class UnlimitedSimpleContainer extends SimpleContainer {
        public UnlimitedSimpleContainer() {
            super(3);
        }

        @Override
        public int getMaxStackSize() {
            return Integer.MAX_VALUE;
        }
    }

}


