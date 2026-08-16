package github.com.gengyoubo.MPG.menu;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.core.MPGMenuCore;

import java.util.Optional;

public class MPGBrewingStandMenu extends AbstractContainerMenu {
    private final Container brewingStand;
    private final ContainerData brewingStandData;
    private final Slot ingredientSlot;

    @SuppressWarnings("unused")
    public MPGBrewingStandMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public MPGBrewingStandMenu(int p_39093_, Inventory p_39094_, Container p_39095_, ContainerData p_39096_) {
        super(MPGMenuCore.BrewingStandManaita.get(), p_39093_);
        checkContainerSize(p_39095_, 5);
        checkContainerDataCount(p_39096_, 2);
        this.brewingStand = p_39095_;
        this.brewingStandData = p_39096_;
        PotionBrewing potionBrewing = p_39094_.player.level().potionBrewing();
        this.addSlot(new PotionSlot(potionBrewing, p_39095_, 0, 56, 51));
        this.addSlot(new PotionSlot(potionBrewing, p_39095_, 1, 79, 58));
        this.addSlot(new PotionSlot(potionBrewing, p_39095_, 2, 102, 51));
        this.ingredientSlot = this.addSlot(new IngredientsSlot(potionBrewing, p_39095_, 3, 79, 17));
        this.addSlot(new FuelSlot(p_39095_, 4, 17, 17));
        this.addDataSlots(p_39096_);

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(p_39094_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(p_39094_, k, 8 + k * 18, 142));
        }
    }

    public boolean stillValid(@NotNull Player p_39098_) {
        return this.brewingStand.stillValid(p_39098_);
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player p_39100_, int p_39101_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_39101_);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if ((p_39101_ < 0 || p_39101_ > 2) && p_39101_ != 3 && p_39101_ != 4) {
                if (FuelSlot.mayPlaceItem(itemstack)) {
                    if (this.moveItemStackTo(itemstack1, 4, 5, false) || this.ingredientSlot.mayPlace(itemstack1) && !this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (this.ingredientSlot.mayPlace(itemstack1)) {
                    if (!this.moveItemStackTo(itemstack1, 3, 4, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (PotionSlot.mayPlaceItem(p_39100_.level().potionBrewing(), itemstack)) {
                    if (!this.moveItemStackTo(itemstack1, 0, 3, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_39101_ >= 5 && p_39101_ < 32) {
                    if (!this.moveItemStackTo(itemstack1, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (p_39101_ >= 32 && p_39101_ < 41) {
                    if (!this.moveItemStackTo(itemstack1, 5, 32, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!this.moveItemStackTo(itemstack1, 5, 41, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(itemstack1, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_39100_, itemstack1);
        }

        return itemstack;
    }

    public int getFuel() {
        return this.brewingStandData.get(1);
    }

    public int getBrewingTicks() {
        return this.brewingStandData.get(0);
    }

    static class FuelSlot extends Slot {
        public FuelSlot(Container p_39105_, int p_39106_, int p_39107_, int p_39108_) {
            super(p_39105_, p_39106_, p_39107_, p_39108_);
        }

        public boolean mayPlace(@NotNull ItemStack p_39111_) {
            return mayPlaceItem(p_39111_);
        }

        public static boolean mayPlaceItem(ItemStack p_39113_) {
            return p_39113_.is(Items.BLAZE_POWDER);
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class IngredientsSlot extends Slot {
        private final PotionBrewing potionBrewing;

        public IngredientsSlot(PotionBrewing potionBrewing, Container p_39115_, int p_39116_, int p_39117_, int p_39118_) {
            super(p_39115_, p_39116_, p_39117_, p_39118_);
            this.potionBrewing = potionBrewing;
        }

        public boolean mayPlace(@NotNull ItemStack p_39121_) {
            return this.potionBrewing.isIngredient(p_39121_);
        }

        public int getMaxStackSize() {
            return 64;
        }
    }

    static class PotionSlot extends Slot {
        private final PotionBrewing potionBrewing;

        public PotionSlot(PotionBrewing potionBrewing, Container p_39123_, int p_39124_, int p_39125_, int p_39126_) {
            super(p_39123_, p_39124_, p_39125_, p_39126_);
            this.potionBrewing = potionBrewing;
        }

        public boolean mayPlace(@NotNull ItemStack p_39132_) {
            return mayPlaceItem(this.potionBrewing, p_39132_);
        }

        public int getMaxStackSize() {
            return 64;
        }

        @Override
        public int getMaxStackSize(@NotNull ItemStack stack) {
            return 64;
        }

        public void onTake(@NotNull Player p_150499_, @NotNull ItemStack p_150500_) {
            Optional<Holder<Potion>> potion = p_150500_.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY).potion();
            if (potion.isPresent() && p_150499_ instanceof ServerPlayer serverPlayer) {
                net.neoforged.neoforge.event.EventHooks.onPlayerBrewedPotion(p_150499_, p_150500_);
                CriteriaTriggers.BREWED_POTION.trigger(serverPlayer, potion.get());
            }

            super.onTake(p_150499_, p_150500_);
        }

        public static boolean mayPlaceItem(PotionBrewing potionBrewing, ItemStack p_39134_) {
            return potionBrewing.isInput(p_39134_) || p_39134_.is(Items.GLASS_BOTTLE);
        }
    }
}

