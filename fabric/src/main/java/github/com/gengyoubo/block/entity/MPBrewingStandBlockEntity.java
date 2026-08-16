package github.com.gengyoubo.block.entity;

import github.com.gengyoubo.common.block.entity.MPGBrewingStandBlockEntityBase;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.menu.MPBrewingStandMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class MPBrewingStandBlockEntity extends MPGBrewingStandBlockEntityBase
        implements ExtendedScreenHandlerFactory<BlockPos> {
    public MPBrewingStandBlockEntity(BlockPos pos, BlockState state) {
        super(MPBlockEntityCore.BREWING_BLOCK_ENTITY.get(), pos, state);
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
    protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
        return new MPBrewingStandMenu(containerId, inventory, this, dataAccess);
    }

    @Override
    public BlockPos getScreenOpeningData(ServerPlayer player) {
        return worldPosition;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, MPBrewingStandBlockEntity entity) {
        MPGBrewingStandBlockEntityBase.serverTick(level, pos, state, entity);
    }
}
