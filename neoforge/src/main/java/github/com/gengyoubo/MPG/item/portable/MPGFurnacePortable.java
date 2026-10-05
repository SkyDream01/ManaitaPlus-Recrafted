package github.com.gengyoubo.MPG.item.portable;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import github.com.gengyoubo.common.block.entity.MPGPortableFurnaceBlockEntityBase;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGFurnacePortable extends MPGPortableItem {
    public MPGFurnacePortable(Item.Properties props) {
        super(props, "item.portableFurnace.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.furnace_manaita",
                (containerId, inventory, player, heldStack, world) ->
                        new ManaitaPlusFurnaceBlockEntity(player, heldStack).createMenu(containerId, inventory));
    }

    public static class ManaitaPlusFurnaceBlockEntity extends MPGPortableFurnaceBlockEntityBase {
        public ManaitaPlusFurnaceBlockEntity(Player player, ItemStack stack) {
            super(MPGBlockEntityCore.FURNACE_BLOCK_ENTITY.get(),
                    MPGBlockCore.FurnaceBlock.get().defaultBlockState(), player, stack);
        }

        @Override
        public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
            return new MPGFurnaceMenu(containerId, inventory, this, dataAccess);
        }
    }
}
