package github.com.gengyoubo.common.menu;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/** Shared brewing menu layout, transfer rules and data access. */
public abstract class MPGBrewingStandMenuBase extends AbstractContainerMenu {
    private final Container brewingStand;
    private final ContainerData brewingStandData;
    private final Slot ingredientSlot;
    private final Predicate<ItemStack> potionInput;

    protected MPGBrewingStandMenuBase(MenuType<?> menuType, int containerId, Inventory inventory,
                                      Container brewingStand, ContainerData data,
                                      Predicate<ItemStack> ingredientInput,
                                      Predicate<ItemStack> potionInput,
                                      BiConsumer<Player, ItemStack> brewedPotionHook) {
        super(menuType, containerId);
        checkContainerSize(brewingStand, 5);
        checkContainerDataCount(data, 2);
        this.brewingStand = brewingStand;
        this.brewingStandData = data;
        this.potionInput = potionInput;

        addSlot(new PotionSlot(brewingStand, 0, 56, 51, potionInput, brewedPotionHook));
        addSlot(new PotionSlot(brewingStand, 1, 79, 58, potionInput, brewedPotionHook));
        addSlot(new PotionSlot(brewingStand, 2, 102, 51, potionInput, brewedPotionHook));
        ingredientSlot = addSlot(new IngredientsSlot(brewingStand, 3, 79, 17, ingredientInput));
        addSlot(new FuelSlot(brewingStand, 4, 17, 17));
        addDataSlots(data);

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, 142));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return brewingStand.stillValid(player);
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
        if ((slotIndex < 0 || slotIndex > 2) && slotIndex != 3 && slotIndex != 4) {
            if (FuelSlot.mayPlaceItem(moving)) {
                if (moveItemStackTo(moving, 4, 5, false)
                        || ingredientSlot.mayPlace(moving) && !moveItemStackTo(moving, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (ingredientSlot.mayPlace(moving)) {
                if (!moveItemStackTo(moving, 3, 4, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (potionInput.test(moving)) {
                if (!moveItemStackTo(moving, 0, 3, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 5 && slotIndex < 32) {
                if (!moveItemStackTo(moving, 32, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex >= 32 && slotIndex < 41) {
                if (!moveItemStackTo(moving, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(moving, 5, 41, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            if (!moveItemStackTo(moving, 5, 41, true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(moving, original);
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
        return original;
    }

    public int getFuel() {
        return brewingStandData.get(1);
    }

    public int getBrewingTicks() {
        return brewingStandData.get(0);
    }

    private static final class FuelSlot extends Slot {
        private FuelSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return mayPlaceItem(stack);
        }

        private static boolean mayPlaceItem(ItemStack stack) {
            return stack.is(Items.BLAZE_POWDER);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    private static final class IngredientsSlot extends Slot {
        private final Predicate<ItemStack> ingredientInput;

        private IngredientsSlot(Container container, int slot, int x, int y,
                                Predicate<ItemStack> ingredientInput) {
            super(container, slot, x, y);
            this.ingredientInput = ingredientInput;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return ingredientInput.test(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }
    }

    private static final class PotionSlot extends Slot {
        private final Predicate<ItemStack> potionInput;
        private final BiConsumer<Player, ItemStack> brewedPotionHook;

        private PotionSlot(Container container, int slot, int x, int y,
                           Predicate<ItemStack> potionInput,
                           BiConsumer<Player, ItemStack> brewedPotionHook) {
            super(container, slot, x, y);
            this.potionInput = potionInput;
            this.brewedPotionHook = brewedPotionHook;
        }

        @Override
        public boolean mayPlace(@NotNull ItemStack stack) {
            return potionInput.test(stack);
        }

        @Override
        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return 64;
        }

        @Override
        public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
            Optional<Holder<Potion>> potion = stack
                    .getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY)
                    .potion();
            if (potion.isPresent() && player instanceof ServerPlayer serverPlayer) {
                brewedPotionHook.accept(player, stack);
                CriteriaTriggers.BREWED_POTION.trigger(serverPlayer, potion.get());
            }
            super.onTake(player, stack);
        }
    }
}
