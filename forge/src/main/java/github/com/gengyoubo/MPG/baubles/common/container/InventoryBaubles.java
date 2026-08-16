package github.com.gengyoubo.MPG.baubles.common.container;

import net.minecraft.core.HolderLookup;
import github.com.gengyoubo.MPG.baubles.api.BaubleType;
import github.com.gengyoubo.MPG.baubles.api.IBauble;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class InventoryBaubles extends ItemStackHandler {
    public static final int SLOT_AMULET = 0;
    public static final int SLOT_RING_PRIMARY = 1;
    public static final int SLOT_RING_SECONDARY = 2;
    public static final int SLOT_BELT = 3;
    private static final BaubleType[] SLOT_TYPES = {
            BaubleType.AMULET,
            BaubleType.RING,
            BaubleType.RING,
            BaubleType.BELT
    };

    private Player owner;

    public InventoryBaubles(Player owner) {
        super(SLOT_TYPES.length);
        this.owner = owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }

    public boolean isValidForSlot(int slot, ItemStack stack) {
        if (!(stack.getItem() instanceof IBauble bauble)) {
            return false;
        }
        if (slot < 0 || slot >= SLOT_TYPES.length) {
            return false;
        }
        return bauble.getBaubleType(stack) == SLOT_TYPES[slot] && owner != null && bauble.canEquip(stack, owner);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return isValidForSlot(slot, stack);
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!isValidForSlot(slot, stack)) {
            return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Override
    public void setStackInSlot(int slot, @NotNull ItemStack stack) {
        ItemStack previous = getStackInSlot(slot).copy();
        super.setStackInSlot(slot, stack);
        if (owner == null) {
            return;
        }
        if (!previous.isEmpty() && previous.getItem() instanceof IBauble previousBauble) {
            previousBauble.onUnequipped(previous, owner);
        }
        if (!stack.isEmpty() && stack.getItem() instanceof IBauble bauble) {
            bauble.onEquipped(stack, owner);
        }
    }

    public boolean equip(ItemStack sourceStack, Player player) {
        Objects.requireNonNull(player, "player");
        if (!(sourceStack.getItem() instanceof IBauble bauble)) {
            return false;
        }

        for (int slot = 0; slot < SLOT_TYPES.length; slot++) {
            if (SLOT_TYPES[slot] != bauble.getBaubleType(sourceStack) || !getStackInSlot(slot).isEmpty()) {
                continue;
            }
            if (!bauble.canEquip(sourceStack, player)) {
                continue;
            }

            ItemStack equipped = sourceStack.copyWithCount(1);
            setStackInSlot(slot, equipped);
            sourceStack.shrink(1);
            return true;
        }
        return false;
    }

    public Optional<ItemStack> findFirstEquipped(BaubleType type) {
        for (int slot = 0; slot < SLOT_TYPES.length; slot++) {
            if (SLOT_TYPES[slot] != type) {
                continue;
            }
            ItemStack stack = getStackInSlot(slot);
            if (!stack.isEmpty()) {
                return Optional.of(stack);
            }
        }
        return Optional.empty();
    }

    public Optional<ItemStack> findFirstMatching(BaubleType type, Predicate<ItemStack> predicate) {
        for (int slot = 0; slot < SLOT_TYPES.length; slot++) {
            if (SLOT_TYPES[slot] != type) {
                continue;
            }
            ItemStack stack = getStackInSlot(slot);
            if (!stack.isEmpty() && predicate.test(stack)) {
                return Optional.of(stack);
            }
        }
        return Optional.empty();
    }

    public void tick(Player player) {
        setOwner(player);
        for (int slot = 0; slot < getSlots(); slot++) {
            ItemStack stack = getStackInSlot(slot);
            if (!stack.isEmpty() && stack.getItem() instanceof IBauble bauble) {
                bauble.onWornTick(stack, player);
            }
        }
    }

    public void copyFrom(InventoryBaubles other) {
        HolderLookup.Provider provider = Objects.requireNonNull(owner, "owner").registryAccess();
        deserializeNBT(provider, other.serializeNBT(provider));
    }
}
