package github.com.gengyoubo.MPG.menu;

import github.com.gengyoubo.MPG.core.MPGBlockCore;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.core.MPGMenuCore;

import java.util.Optional;

public class MPGCraftingMenu extends AbstractContainerMenu {
    private final CraftingContainer craftSlots = new TransientCraftingContainer(this, 3, 3);
    private final ResultContainer resultSlots = new ResultContainer();
    private final ContainerLevelAccess access;
    private final Player player;
    private boolean stillValid = false;

    @SuppressWarnings("unused")
    public MPGCraftingMenu(int p_39353_, Inventory p_39354_, FriendlyByteBuf extraData) {
        this(p_39353_, p_39354_, ContainerLevelAccess.NULL);
    }

    public MPGCraftingMenu(int p_39353_, Inventory p_39354_, Level level) {
        this(p_39353_, p_39354_, ContainerLevelAccess.create(level, BlockPos.ZERO));
        stillValid = true;
    }

    public MPGCraftingMenu(int p_39356_, Inventory p_39357_, ContainerLevelAccess p_39358_) {
        super(MPGMenuCore.CraftingManaita.get(), p_39356_);
        this.access = p_39358_;
        this.player = p_39357_.player;
        this.addSlot(new ResultSlot(p_39357_.player, this.craftSlots, this.resultSlots, 0, 124, 35));

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftSlots, j + i * 3, 30 + j * 18, 17 + i * 18));
            }
        }

        for(int k = 0; k < 3; ++k) {
            for(int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new Slot(p_39357_, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for(int l = 0; l < 9; ++l) {
            this.addSlot(new Slot(p_39357_, l, 8 + l * 18, 142));
        }

    }

    protected static void slotChangedCraftingGrid(AbstractContainerMenu p_150547_, Level p_150548_, Player p_150549_, CraftingContainer p_150550_, ResultContainer p_150551_) {
        if (!p_150548_.isClientSide) {
            if (p_150548_.getServer() == null) {
                return;
            }
            ServerPlayer serverplayer = (ServerPlayer) p_150549_;
            ItemStack itemstack = ItemStack.EMPTY;
            Optional<RecipeHolder<CraftingRecipe>> optional = p_150548_.getServer().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, p_150550_.asCraftInput(), p_150548_);
            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> craftingrecipe = optional.get();
                p_150551_.setRecipeUsed(craftingrecipe);
                ItemStack itemstack1 = craftingrecipe.value().assemble(p_150550_.asCraftInput(), p_150548_.registryAccess());
                if (itemstack1.isItemEnabled(p_150548_.enabledFeatures())) {
                    itemstack = itemstack1;
                    itemstack.setCount(itemstack.getCount() * MPGConfig.crafting_doubling_value);
                }
            }

            p_150551_.setItem(0, itemstack);
            p_150547_.setRemoteSlot(0, itemstack);
            serverplayer.connection.send(new ClientboundContainerSetSlotPacket(p_150547_.containerId, p_150547_.incrementStateId(), 0, itemstack));
        }
    }

    public void slotsChanged(@NotNull Container p_39366_) {
        this.access.execute((p_39386_, p_39387_) -> slotChangedCraftingGrid(this, p_39386_, this.player, this.craftSlots, this.resultSlots));
    }

    public void removed(@NotNull Player p_39389_) {
        super.removed(p_39389_);
        this.access.execute((p_39371_, p_39372_) -> this.clearContainer(p_39389_, this.craftSlots));
    }

    public @NotNull ItemStack quickMoveStack(@NotNull Player p_39391_, int p_39392_) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(p_39392_);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (p_39392_ == 0) {
                this.access.execute((p_39378_, p_39379_) -> itemstack1.getItem().onCraftedBy(itemstack1, p_39378_, p_39391_));
                if (!this.moveItemStackTo(itemstack1, 10, 46, true)) {
                    return ItemStack.EMPTY;
                }

                slot.onQuickCraft(itemstack1, itemstack);
            } else if (p_39392_ >= 10 && p_39392_ < 46) {
                if (!this.moveItemStackTo(itemstack1, 1, 10, false)) {
                    if (p_39392_ < 37) {
                        if (!this.moveItemStackTo(itemstack1, 37, 46, false)) {
                            return ItemStack.EMPTY;
                        }
                    } else if (!this.moveItemStackTo(itemstack1, 10, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            } else if (!this.moveItemStackTo(itemstack1, 10, 46, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(p_39391_, itemstack1);
            if (p_39392_ == 0) {
                p_39391_.drop(itemstack1, false);
            }
        }

        return itemstack;
    }

    public boolean stillValid(@NotNull Player p_39368_) {
        if (stillValid) return true;
        return stillValid(this.access, p_39368_, MPGBlockCore.CraftingBlock.get());
    }


    public boolean canTakeItemForPickAll(@NotNull ItemStack p_39381_, Slot p_39382_) {
        return p_39382_.container != this.resultSlots && super.canTakeItemForPickAll(p_39381_, p_39382_);
    }
}
