package github.com.gengyoubo.common.menu;

import github.com.gengyoubo.common.config.MPGConfigValues;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.MenuType;
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
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/** Shared 3x3 crafting menu and doubled-result behavior. */
public abstract class MPGCraftingMenuBase extends RecipeBookMenu<CraftingInput, CraftingRecipe> {
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;
    private final Block craftingBlock;
    private final boolean alwaysValid;

    protected MPGCraftingMenuBase(MenuType<?> menuType, int containerId, Inventory inventory,
                                  ContainerLevelAccess access, Block craftingBlock, boolean alwaysValid) {
        super(menuType, containerId);
        this.access = access;
        this.player = inventory.player;
        this.craftingBlock = craftingBlock;
        this.alwaysValid = alwaysValid;

        addSlot(new ResultSlot(inventory.player, craftSlots, resultSlots, 0, 124, 35));
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 3; column++) {
                addSlot(new Slot(craftSlots, column + row * 3, 30 + column * 18, 17 + row * 18));
            }
        }
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    private static void slotChangedCraftingGrid(AbstractContainerMenu menu, Level level, Player player,
                                                CraftingContainer craftSlots, ResultContainer resultSlots) {
        if (level.isClientSide || level.getServer() == null) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer) player;
        ItemStack result = ItemStack.EMPTY;
        CraftingInput input = CraftingInput.of(craftSlots.getWidth(), craftSlots.getHeight(), craftSlots.getItems());
        Optional<RecipeHolder<CraftingRecipe>> recipe = level.getServer().getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, input, level);
        if (recipe.isPresent()) {
            RecipeHolder<CraftingRecipe> holder = recipe.get();
            resultSlots.setRecipeUsed(holder);
            ItemStack assembled = holder.value().assemble(input, level.registryAccess());
            if (assembled.isItemEnabled(level.enabledFeatures())) {
                long multiplied = (long) assembled.getCount() * MPGConfigValues.crafting_doubling_value;
                assembled.setCount((int) Math.min(Integer.MAX_VALUE, multiplied));
                result = assembled;
            }
        }
        resultSlots.setItem(0, result);
        menu.setRemoteSlot(0, result);
        serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(
                menu.containerId, menu.incrementStateId(), 0, result));
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        access.execute((level, pos) -> slotChangedCraftingGrid(this, level, player, craftSlots, resultSlots));
    }

    @Override
    public void fillCraftSlotsStackedContents(@NotNull StackedContents stackedContents) {
        craftSlots.fillStackedContents(stackedContents);
    }

    @Override
    public void clearCraftingContent() {
        craftSlots.clearContent();
        resultSlots.clearContent();
    }

    @Override
    public boolean recipeMatches(RecipeHolder<CraftingRecipe> recipe) {
        CraftingInput input = CraftingInput.of(craftSlots.getWidth(), craftSlots.getHeight(), craftSlots.getItems());
        return recipe.value().matches(input, player.level());
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        access.execute((level, pos) -> clearContainer(player, craftSlots));
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack original = ItemStack.EMPTY;
        Slot slot = slots.get(slotIndex);
        if (!slot.hasItem()) {
            return original;
        }
        ItemStack moving = slot.getItem();
        original = moving.copy();
        if (slotIndex == 0) {
            access.execute((level, pos) -> moving.getItem().onCraftedBy(moving, level, player));
            if (!moveItemStackTo(moving, 10, 46, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(moving, original);
        } else if (slotIndex >= 10 && slotIndex < 46) {
            if (!moveItemStackTo(moving, 1, 10, false)) {
                if (slotIndex < 37) {
                    if (!moveItemStackTo(moving, 37, 46, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(moving, 10, 37, false)) {
                    return ItemStack.EMPTY;
                }
            }
        } else if (!moveItemStackTo(moving, 10, 46, false)) {
            return ItemStack.EMPTY;
        }

        if (moving.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (moving.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, moving);
        if (slotIndex == 0) {
            player.drop(moving, false);
        }
        return original;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return alwaysValid || stillValid(access, player, craftingBlock);
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.container != resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    @Override
    public int getResultSlotIndex() {
        return 0;
    }

    @Override
    public int getGridWidth() {
        return craftSlots.getWidth();
    }

    @Override
    public int getGridHeight() {
        return craftSlots.getHeight();
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
        return slotIndex != getResultSlotIndex();
    }
}
