package github.com.gengyoubo.menu;

import github.com.gengyoubo.MPGConfig;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPMenuCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeBookMenu;
import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MPCraftingMenu extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;
    private boolean alwaysValid;

    @SuppressWarnings("unused")
    public MPCraftingMenu(int containerId, Inventory inventory, BlockPos blockPos) {
        this(containerId, inventory, ContainerLevelAccess.create(inventory.player.level(), blockPos));
    }

    public MPCraftingMenu(int containerId, Inventory inventory, Level level) {
        this(containerId, inventory, ContainerLevelAccess.create(level, BlockPos.ZERO));
        this.alwaysValid = true;
    }

    public MPCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        super(MPMenuCore.CraftingManaita.get(), containerId);
        this.access = access;
        this.player = inventory.player;
        this.addSlot(new ResultSlot(inventory.player, this.craftSlots, this.resultSlots, 0, 124, 35));

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 3; ++column) {
                this.addSlot(new Slot(this.craftSlots, column + row * 3, 30 + column * 18, 17 + row * 18));
            }
        }

        for (int inventoryRow = 0; inventoryRow < 3; ++inventoryRow) {
            for (int inventoryColumn = 0; inventoryColumn < 9; ++inventoryColumn) {
                this.addSlot(new Slot(inventory, inventoryColumn + inventoryRow * 9 + 9, 8 + inventoryColumn * 18, 84 + inventoryRow * 18));
            }
        }

        for (int hotbarSlot = 0; hotbarSlot < 9; ++hotbarSlot) {
            this.addSlot(new Slot(inventory, hotbarSlot, 8 + hotbarSlot * 18, 142));
        }
    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player, CraftingContainer craftSlots, ResultContainer resultSlots) {
        if (level.isClientSide || level.getServer() == null) {
            return;
        }

        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack result = ItemStack.EMPTY;
        CraftingInput input = CraftingInput.of(craftSlots.getWidth(), craftSlots.getHeight(), craftSlots.getItems());
        Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, level);
        if (optional.isPresent()) {
            RecipeHolder<CraftingRecipe> recipeHolder = optional.get();
            resultSlots.setRecipeUsed(recipeHolder);
            ItemStack assembled = recipeHolder.value().assemble(input, level.registryAccess());
            if (assembled.isItemEnabled(level.enabledFeatures())) {
                result = assembled;
                result.setCount(result.getCount() * MPGConfig.crafting_doubling_value);
            }
        }

        resultSlots.setItem(0, result);
        menu.setRemoteSlot(0, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(menu.containerId, menu.incrementStateId(), 0, result));
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        this.access.execute((level, pos) -> slotChangedCraftingGrid(this, level, this.player, this.craftSlots, this.resultSlots));
    }

    @Override
    public void fillCraftSlotsStackedContents(@NotNull StackedContents stackedContents) {
        this.craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        this.craftSlots.clearContent();
        this.resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipeHolder) {
        CraftingInput input = CraftingInput.of(this.craftSlots.getWidth(), this.craftSlots.getHeight(), this.craftSlots.getItems());
        return recipeHolder.value().matches(input, this.player.level());
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.access.execute((level, pos) -> this.clearContainer(player, this.craftSlots));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemStack = slotStack.copy();
            if (slotIndex == 0) {
                this.access.execute((level, pos) -> slotStack.getItem().onCraftedBy(slotStack, level, player));
                if (!this.moveItemStackTo(slotStack, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(slotStack, itemStack);
            } else if (slotIndex >= 10 && slotIndex < 46) {
                if (!this.moveItemStackTo(slotStack, 1, 10, false)) {
                    if (slotIndex < 37) {
                        if (!this.moveItemStackTo(slotStack, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(slotStack, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(slotStack, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, slotStack);
            if (slotIndex == 0) {
                player.drop(slotStack, false);
            }
        }

        return itemStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return this.alwaysValid || stillValid(this.access, player, MPBlockCore.CraftingBlock.get());
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return this.craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return this.craftSlots.getHeight();
    }

    @Override
    public int getSize() {
        return 10;
    }

    @Override
    public @NotNull RecipeBookType getRecipeBookType() {
        return RecipeBookType.CRAFTING;
    }

    @Override
    public boolean shouldMoveToInventory(int slotIndex) {
        return slotIndex != this.getResultSlotIndex();
    }
}
