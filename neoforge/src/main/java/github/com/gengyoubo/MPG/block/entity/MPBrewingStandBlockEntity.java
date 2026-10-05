package github.com.gengyoubo.MPG.block.entity;

import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.common.block.entity.MPGBrewingStandBlockEntityBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Containers;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeAccess;
import net.minecraft.world.item.crafting.RecipePropertySet;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MPBrewingStandBlockEntity extends MPGBrewingStandBlockEntityBase {
    public MPBrewingStandBlockEntity(BlockPos pos, BlockState state) {
        super(MPGBlockEntityCore.BREWING_BLOCK_ENTITY.get(), pos, state);
    }

    @Override
    protected boolean beforeBrew(NonNullList<ItemStack> items) {
        return net.neoforged.neoforge.event.EventHooks.onPotionAttemptBrew(items);
    }

    @Override
    protected void finishBrew(Level level, double x, double y, double z, NonNullList<ItemStack> items) {
        net.neoforged.neoforge.event.EventHooks.onPotionBrewed(items);
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

    @Override
    protected boolean isPotionInput(RecipeAccess recipeAccess, ItemStack stack) {
        // PotionBrewing is gone in 26.3; the recipe property sets describe the potion inputs.
        return recipeAccess.propertySet(RecipePropertySet.BREWING_INPUTS).test(stack) || stack.is(Items.GLASS_BOTTLE);
    }

    @Override
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new MPGBrewingStandMenu(containerId, inventory, this, dataAccess);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MPBrewingStandBlockEntity entity) {
        MPGBrewingStandBlockEntityBase.serverTick(level, pos, state, entity);
    }
}
