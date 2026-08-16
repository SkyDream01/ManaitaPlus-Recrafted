package github.com.gengyoubo.MPG.item.ring;

import github.com.gengyoubo.MPG.item.portable.MPGFurnacePortable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MPGFurnaceRing extends MPGRingItem {
    public MPGFurnaceRing() {
        super("item.ringFurnace.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.furnace_manaita",
                (containerId, inventory, player, heldStack, world) -> {
                    MPGFurnacePortable.ManaitaPlusFurnaceBlockEntity block = new MPGFurnacePortable.ManaitaPlusFurnaceBlockEntity(player, heldStack);
                    return block.createMenu(containerId, inventory, player);
                });
    }
}
