package github.com.gengyoubo.item.portable;

import net.minecraft.core.NonNullList;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.block.entity.MPGBrewingLogicHelper;
import github.com.gengyoubo.core.MPBlockEntityCore;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.menu.MPBrewingStandMenu;

import java.util.Arrays;

public class MPBrewingPortable extends MPGPortableItem {
    public MPBrewingPortable() {
        super("item.portableBrewing.");
    }

    @Override
    protected void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level) {
        openPortableScreen(serverPlayer, itemInHand, level, "container.brewing_manaita",
                (containerId, inventory, player, heldStack, world) -> {
                    MPBrewingStandBlockEntity blockEntity = new MPBrewingStandBlockEntity(inventory.player, heldStack);
                    return blockEntity.createMenu(containerId, inventory, player);
                });
    }

    public static class MPBrewingStandBlockEntity extends BaseContainerBlockEntity {
        private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
        private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
        private boolean[] lastPotionCount;
        private final Player player;
        private final ItemStack stack;

        protected final ContainerData dataAccess = new ContainerData() {
            public int get(int index) {
                return 0;
            }

            public void set(int index, int value) {
            }

            public int getCount() {
                return 2;
            }
        };

        public MPBrewingStandBlockEntity(Player player, ItemStack stack) {
            super(MPBlockEntityCore.BREWING_BLOCK_ENTITY.get(), player.blockPosition(), MPBlockCore.BrewingBlock.get().defaultBlockState());
            this.player = player;
            this.stack = stack;
            loadAdditional(github.com.gengyoubo.common.util.MPGItemStackData.getOrCreateTag(stack), player.registryAccess());
        }

        protected @NotNull net.minecraft.network.chat.Component getDefaultName() {
            return net.minecraft.network.chat.Component.translatable("container.brewing_manaita");
        }

        public int getContainerSize() {
            return this.items.size();
        }

        public boolean isEmpty() {
            for (ItemStack itemstack : this.items) {
                if (!itemstack.isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        private boolean[] getPotionBits() {
            boolean[] bits = new boolean[3];
            for (int i = 0; i < 3; ++i) {
                if (!this.items.get(i).isEmpty()) {
                    bits[i] = true;
                }
            }
            return bits;
        }

        private boolean isBrewable(NonNullList<ItemStack> items) {
            return MPGBrewingLogicHelper.isBrewable(this.player.level(), items, 3);
        }

        private void doBrew(Level level, NonNullList<ItemStack> items) {
            MPGBrewingLogicHelper.finishBrew(level, player.getX(), player.getY(), player.getZ(), items, 3);
        }

        @Override
        protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            super.loadAdditional(tag, provider);
            this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
            ContainerHelper.loadAllItems(tag, this.items, provider);
        }

        @Override
        protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider provider) {
            super.saveAdditional(tag, provider);
            ContainerHelper.saveAllItems(tag, this.items, provider);
        }

        public @NotNull ItemStack getItem(int index) {
            return index >= 0 && index < this.items.size() ? this.items.get(index) : ItemStack.EMPTY;
        }

        public @NotNull ItemStack removeItem(int index, int count) {
            return ContainerHelper.removeItem(this.items, index, count);
        }

        public @NotNull ItemStack removeItemNoUpdate(int index) {
            return ContainerHelper.takeItem(this.items, index);
        }

        public void setItem(int index, @NotNull ItemStack stack) {
            if (index >= 0 && index < this.items.size()) {
                this.items.set(index, stack);
                if (isBrewable(this.items)) {
                    doBrew(player.level(), this.items);
                }

                boolean[] bits = this.getPotionBits();
                if (!Arrays.equals(bits, this.lastPotionCount)) {
                    this.lastPotionCount = bits;
                }
            }
            saveAdditional(github.com.gengyoubo.common.util.MPGItemStackData.getOrCreateTag(this.stack), this.player.registryAccess());
        }

        public boolean stillValid(@NotNull Player player) {
            return true;
        }

        public boolean canPlaceItem(int index, @NotNull ItemStack stack) {
            return MPGBrewingLogicHelper.canPlaceItem(index, stack, this.getItem(index));
        }

        public void clearContent() {
            this.items.clear();
        }

        @Override
        protected @NotNull NonNullList<ItemStack> getItems() {
            return this.items;
        }

        @Override
        protected void setItems(@NotNull NonNullList<ItemStack> items) {
            this.items = items;
        }

        protected @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory) {
            return new MPBrewingStandMenu(containerId, inventory, this, this.dataAccess);
        }
    }
}

