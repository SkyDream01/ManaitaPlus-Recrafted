package github.com.gengyoubo.MPG.item.ring;

import github.com.gengyoubo.MPG.item.portable.MPGBrewingPortable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class MPGBrewingRing extends MPGRingItem {
    public MPGBrewingRing() {
        super("item.ringBrewing.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.brewing_manaita",
                (containerId, inventory, player, heldStack, world) -> {
                    MPGBrewingPortable.ManaitaPlusBrewingStandBlockEntity blockEntity = new MPGBrewingPortable.ManaitaPlusBrewingStandBlockEntity(inventory.player, heldStack);
                    return blockEntity.createMenu(containerId, inventory, player);
                });
    }
}
