package github.com.gengyoubo.item.portable;

import github.com.gengyoubo.common.block.entity.MPGPortableBrewingBlockEntityBase;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.menu.MPBrewingStandMenu;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPBrewingPortable extends MPGPortableItem {
    public MPBrewingPortable() {
        super("item.portableBrewing.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.brewing_manaita",
                (containerId, inventory, player, heldStack, world) ->
                        new MPBrewingStandBlockEntity(player, heldStack).createMenu(containerId, inventory));
    }

    public static class MPBrewingStandBlockEntity extends MPGPortableBrewingBlockEntityBase {
        public MPBrewingStandBlockEntity(Player player, ItemStack stack) {
            super(MPBlockEntityCore.BREWING_BLOCK_ENTITY.get(),
                    MPBlockCore.BrewingBlock.get().defaultBlockState(), player, stack);
        }

        @Override
        protected boolean isPotionInput(PotionBrewing potionBrewing, ItemStack stack) {
            return potionBrewing.isContainerIngredient(stack) || stack.is(Items.GLASS_BOTTLE);
        }

        @Override
        protected void finishBrew(Level level, double x, double y, double z, NonNullList<ItemStack> items) {
            items.get(3).shrink(1);
        }

        @Override
        public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
            return new MPBrewingStandMenu(containerId, inventory, this, dataAccess);
        }
    }
}
