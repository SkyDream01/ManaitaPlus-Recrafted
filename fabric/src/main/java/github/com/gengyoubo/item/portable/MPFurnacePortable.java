package github.com.gengyoubo.item.portable;

import github.com.gengyoubo.common.block.entity.MPGPortableFurnaceBlockEntityBase;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.menu.MPFurnaceMenu;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPFurnacePortable extends MPGPortableItem {
    public MPFurnacePortable() {
        super("item.portableFurnace.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.furnace_manaita",
                (containerId, inventory, player, heldStack, world) ->
                        new MPFurnaceBlockEntity(player, heldStack).createMenu(containerId, inventory));
    }

    public static class MPFurnaceBlockEntity extends MPGPortableFurnaceBlockEntityBase {
        public MPFurnaceBlockEntity(Player player, ItemStack stack) {
            super(MPBlockEntityCore.FURNACE_BLOCK_ENTITY.get(),
                    MPBlockCore.FurnaceBlock.get().defaultBlockState(), player, stack);
        }

        @Override
        public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
            return new MPFurnaceMenu(containerId, inventory, this, dataAccess);
        }
    }
}
