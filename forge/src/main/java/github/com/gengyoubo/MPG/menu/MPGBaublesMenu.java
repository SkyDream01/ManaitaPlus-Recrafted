package github.com.gengyoubo.MPG.menu;

import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import github.com.gengyoubo.MPG.baubles.common.container.InventoryBaubles;
import github.com.gengyoubo.MPG.core.MPGMenuCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ResultContainer;
import net.minecraft.world.inventory.ResultSlot;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Equipable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class MPGBaublesMenu extends AbstractContainerMenu {
    private static final int RESULT_SLOT = 0;
    private static final int CRAFT_SLOT_START = 1;
    private static final int CRAFT_SLOT_END = 5;
    private static final int ARMOR_SLOT_START = 5;
    private static final int ARMOR_SLOT_END = 9;
    private static final int BAUBLE_SLOT_START = 9;
    private static final int BAUBLE_SLOT_END = 13;
    private static final int INVENTORY_SLOT_START = 13;
    private static final int INVENTORY_SLOT_END = 40;
    private static final int HOTBAR_SLOT_START = 40;
    private static final int HOTBAR_SLOT_END = 49;
    private static final int[] ARMOR_SLOT_INDEXES = {39, 38, 37, 36};
    private static final EquipmentSlot[] ARMOR_SLOT_TYPES = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private final InventoryBaubles baublesInventory;
    private final TransientCraftingContainer craftSlots = new TransientCraftingContainer(this, 2, 2);
    private final ResultContainer resultSlots = new ResultContainer();
    private final Player player;

    @SuppressWarnings("unused")
    public MPGBaublesMenu(int containerId, Inventory playerInventory, FriendlyByteBuf ignored) {
        this(containerId, playerInventory, BaublesCapability.find(playerInventory.player).orElseGet(() -> new InventoryBaubles(playerInventory.player)));
    }

    public MPGBaublesMenu(int containerId, Inventory playerInventory, InventoryBaubles baublesInventory) {
        super(MPGMenuCore.Baubles.get(), containerId);
        this.player = playerInventory.player;
        this.baublesInventory = baublesInventory;
        this.baublesInventory.setOwner(playerInventory.player);

        this.addSlot(new ResultSlot(playerInventory.player, this.craftSlots, this.resultSlots, 0, 144, 36));

        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                this.addSlot(new Slot(this.craftSlots, column + row * 2, 106 + column * 18, 26 + row * 18));
            }
        }

        for (int slot = 0; slot < ARMOR_SLOT_INDEXES.length; slot++) {
            final EquipmentSlot equipmentSlot = ARMOR_SLOT_TYPES[slot];
            this.addSlot(new Slot(playerInventory, ARMOR_SLOT_INDEXES[slot], 8, 8 + slot * 18) {
                @Override
                public boolean mayPlace(@NotNull ItemStack stack) {
                    Equipable equipable = Equipable.get(stack);
                    return equipable != null && equipable.getEquipmentSlot() == equipmentSlot;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return super.mayPickup(player);
                }

                @Override
                public int getMaxStackSize() {
                    return 1;
                }
            });
        }

        this.addSlot(new SlotItemHandler(baublesInventory, InventoryBaubles.SLOT_AMULET, 80, 8));
        this.addSlot(new SlotItemHandler(baublesInventory, InventoryBaubles.SLOT_RING_PRIMARY, 80, 26));
        this.addSlot(new SlotItemHandler(baublesInventory, InventoryBaubles.SLOT_RING_SECONDARY, 80, 44));
        this.addSlot(new SlotItemHandler(baublesInventory, InventoryBaubles.SLOT_BELT, 80, 62));

        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }

        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public void slotsChanged(@NotNull Container container) {
        if (!this.player.level().isClientSide && this.player instanceof ServerPlayer serverPlayer) {
            ItemStack result = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = serverPlayer.server.getRecipeManager()
                    .getRecipeFor(RecipeType.CRAFTING, this.craftSlots.asCraftInput(), this.player.level());
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipeHolder = optional.get();
                this.resultSlots.setRecipeUsed(recipeHolder);
                ItemStack assembled = recipeHolder.value().assemble(this.craftSlots.asCraftInput(), this.player.level().registryAccess());
                if (assembled.isItemEnabled(this.player.level().enabledFeatures())) {
                    result = assembled;
                }
            }

            this.resultSlots.setItem(0, result);
            this.setRemoteSlot(RESULT_SLOT, result);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), RESULT_SLOT, result));
        }
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        this.clearContainer(player, this.craftSlots);
        this.resultSlots.clearContent();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return BaublesCapability.isEnabled();
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack copiedStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) {
            return copiedStack;
        }

        ItemStack source = slot.getItem();
        copiedStack = source.copy();
        if (index == RESULT_SLOT) {
            if (!this.moveItemStackTo(source, INVENTORY_SLOT_START, HOTBAR_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(source, copiedStack);
        } else if (index < INVENTORY_SLOT_START) {
            if (!this.moveItemStackTo(source, INVENTORY_SLOT_START, HOTBAR_SLOT_END, true)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (moveToArmor(source) || moveToBaubles(source)) {
                // moved into a specialized slot
            } else if (index < HOTBAR_SLOT_START) {
                if (!this.moveItemStackTo(source, HOTBAR_SLOT_START, HOTBAR_SLOT_END, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(source, INVENTORY_SLOT_START, HOTBAR_SLOT_START, false)) {
                return ItemStack.EMPTY;
            }
        }

        if (source.isEmpty()) {
            slot.setByPlayer(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (source.getCount() == copiedStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, source);
        if (index == RESULT_SLOT) {
            player.drop(source, false);
        }
        return copiedStack;
    }

    @Override
    public boolean canTakeItemForPickAll(@NotNull ItemStack stack, Slot slot) {
        return slot.container != this.resultSlots && super.canTakeItemForPickAll(stack, slot);
    }

    private boolean moveToArmor(ItemStack stack) {
        Equipable equipable = Equipable.get(stack);
        if (equipable == null) {
            return false;
        }
        EquipmentSlot equipmentSlot = equipable.getEquipmentSlot();
        for (int slot = ARMOR_SLOT_START; slot < ARMOR_SLOT_END; slot++) {
            int armorIndex = slot - ARMOR_SLOT_START;
            if (ARMOR_SLOT_TYPES[armorIndex] != equipmentSlot || this.slots.get(slot).hasItem()) {
                continue;
            }
            if (this.moveItemStackTo(stack, slot, slot + 1, false)) {
                return true;
            }
        }
        return false;
    }

    private boolean moveToBaubles(ItemStack stack) {
        for (int slot = 0; slot < this.baublesInventory.getSlots(); slot++) {
            if (!this.baublesInventory.isValidForSlot(slot, stack) || !this.baublesInventory.getStackInSlot(slot).isEmpty()) {
                continue;
            }
            int menuSlot = BAUBLE_SLOT_START + slot;
            if (this.moveItemStackTo(stack, menuSlot, menuSlot + 1, false)) {
                return true;
            }
        }
        return false;
    }
}
