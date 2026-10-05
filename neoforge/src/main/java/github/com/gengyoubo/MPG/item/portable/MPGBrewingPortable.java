package github.com.gengyoubo.MPG.item.portable;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.common.block.entity.MPGPortableBrewingBlockEntityBase;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGBrewingPortable extends MPGPortableItem {
    public MPGBrewingPortable(Item.Properties props) {
        super(props, "item.portableBrewing.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.brewing_manaita",
                (containerId, inventory, player, heldStack, world) ->
                        new ManaitaPlusBrewingStandBlockEntity(player, heldStack).createMenu(containerId, inventory));
    }

    public static class ManaitaPlusBrewingStandBlockEntity extends MPGPortableBrewingBlockEntityBase {
        public ManaitaPlusBrewingStandBlockEntity(Player player, ItemStack stack) {
            super(MPGBlockEntityCore.BREWING_BLOCK_ENTITY.get(),
                    MPGBlockCore.BrewingBlock.get().defaultBlockState(), player, stack);
        }

        @Override
        protected boolean beforeBrew(NonNullList<ItemStack> items) {
            return net.neoforged.neoforge.event.EventHooks.onPotionAttemptBrew(items);
        }

        @Override
        protected void finishBrew(Level level, double x, double y, double z, NonNullList<ItemStack> items) {
            net.neoforged.neoforge.event.EventHooks.onPotionBrewed(items);
            consumeIngredient(level, x, y, z, items);
        }

        @Override
        protected boolean isPotionInput(RecipeAccess recipeAccess, ItemStack stack) {
            // PotionBrewing is gone in 26.3; the recipe property sets describe the potion inputs.
            return recipeAccess.propertySet(RecipePropertySet.BREWING_INPUTS).test(stack) || stack.is(Items.GLASS_BOTTLE);
        }

        @Override
        public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
            return new MPGBrewingStandMenu(containerId, inventory, this, dataAccess);
        }

        private static void consumeIngredient(Level level, double x, double y, double z,
                                              NonNullList<ItemStack> items) {
            ItemStack ingredient = items.get(3);
            // ItemStack#hasCraftingRemainingItem/getCraftingRemainingItem are gone in 26.3;
            // the remainder now lives on the item as an ItemStackTemplate.
            ItemStackTemplate craftingRemainder = ingredient.getItem().getCraftingRemainder();
            if (craftingRemainder != null) {
                ItemStack remainder = craftingRemainder.create();
                ingredient.shrink(1);
                if (ingredient.isEmpty()) {
                    ingredient = remainder;
                } else {
                    Containers.dropItemStack(level, x, y, z, remainder);
                }
            } else {
                ingredient.shrink(1);
            }
            items.set(3, ingredient);
        }
    }
}
