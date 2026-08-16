package github.com.gengyoubo.MPG.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.MPGConfig;
import github.com.gengyoubo.MPG.block.MPBrewingStandBlock;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.MPG.core.MPGBlockEntityCore;

import javax.annotation.Nullable;
import java.util.Arrays;

public class MPBrewingStandBlockEntity extends BaseContainerBlockEntity implements WorldlyContainer {
    private static final int[] SLOTS_FOR_UP = new int[]{3};
    private static final int[] SLOTS_FOR_DOWN = new int[]{0, 1, 2, 3};
    private static final int[] SLOTS_FOR_SIDES = new int[]{0, 1, 2, 4};
    private NonNullList<ItemStack> items = NonNullList.withSize(5, ItemStack.EMPTY);
    int brewTime;
    private boolean[] lastPotionCount;
    private Item ingredient;
    int fuel;
    protected final ContainerData dataAccess = new ContainerData() {
        public int get(int p_59038_) {
            return switch (p_59038_) {
                case 0 -> MPBrewingStandBlockEntity.this.brewTime;
                case 1 -> MPBrewingStandBlockEntity.this.fuel;
                default -> 0;
            };
        }

        public void set(int p_59040_, int p_59041_) {
            switch (p_59040_) {
                case 0:
                    MPBrewingStandBlockEntity.this.brewTime = p_59041_;
                    break;
                case 1:
                    MPBrewingStandBlockEntity.this.fuel = p_59041_;
            }

        }

        public int getCount() {
            return 2;
        }
    };

    public MPBrewingStandBlockEntity(BlockPos p_155283_, BlockState p_155284_) {
        super(MPGBlockEntityCore.BREWING_BLOCK_ENTITY.get(), p_155283_, p_155284_);
    }

    protected @NotNull Component getDefaultName() {
        return Component.translatable("container.brewing_manaita");
    }

    public int getContainerSize() {
        return this.items.size();
    }

    @Override
    protected @NotNull NonNullList<ItemStack> getItems() {
        return this.items;
    }

    @Override
    protected void setItems(@NotNull NonNullList<ItemStack> items) {
        this.items = items;
    }

    public static void serverTick(Level p_155286_, BlockPos p_155287_, BlockState p_155288_, MPBrewingStandBlockEntity p_155289_) {
        ItemStack fuelStack = p_155289_.items.get(4);
        if (p_155289_.fuel <= 0 && fuelStack.is(Items.BLAZE_POWDER)) {
            p_155289_.fuel = 20;
            fuelStack.shrink(1);
            setChanged(p_155286_, p_155287_, p_155288_);
        }

        PotionBrewing potionBrewing = p_155286_.potionBrewing();
        boolean flag = isBrewable(potionBrewing, p_155289_.items);
        boolean brewing = p_155289_.brewTime > 0;
        ItemStack ingredientStack = p_155289_.items.get(3);
        if (brewing) {
            p_155289_.brewTime--;
            boolean finished = p_155289_.brewTime == 0;
            if (finished && flag) {
                doBrew(p_155286_, p_155287_, p_155289_.items);
            } else if (!flag || !ingredientStack.is(p_155289_.ingredient)) {
                p_155289_.brewTime = 0;
            }

            setChanged(p_155286_, p_155287_, p_155288_);
        } else if (flag && p_155289_.fuel > 0) {
            p_155289_.fuel--;
            p_155289_.brewTime = 400;
            p_155289_.ingredient = ingredientStack.getItem();
            setChanged(p_155286_, p_155287_, p_155288_);
        }

        boolean[] aboolean = p_155289_.getPotionBits();
        if (!Arrays.equals(aboolean, p_155289_.lastPotionCount)) {
            p_155289_.lastPotionCount = aboolean;
            BlockState blockstate = p_155288_;
            if (!(p_155288_.getBlock() instanceof MPBrewingStandBlock)) {
                return;
            }

            for (int i = 0; i < MPBrewingStandBlock.HAS_BOTTLE.length; ++i) {
                blockstate = blockstate.setValue(MPBrewingStandBlock.HAS_BOTTLE[i], aboolean[i]);
            }

            p_155286_.setBlock(p_155287_, blockstate, 2);
        }
    }

    private boolean[] getPotionBits() {
        boolean[] aboolean = new boolean[3];

        for(int i = 0; i < 3; ++i) {
            if (!this.items.get(i).isEmpty()) {
                aboolean[i] = true;
            }
        }

        return aboolean;
    }

    private static boolean isBrewable(PotionBrewing potionBrewing, NonNullList<ItemStack> p_155295_) {
        ItemStack itemstack = p_155295_.get(3);
        if (itemstack.isEmpty() || !potionBrewing.isIngredient(itemstack)) {
            return false;
        }
        for (int slot = 0; slot < 3; slot++) {
            ItemStack input = p_155295_.get(slot);
            if (!input.isEmpty() && potionBrewing.hasMix(input, itemstack)) {
                return true;
            }
        }
        return false;
    }

    private static void doBrew(Level p_155291_, BlockPos p_155292_, NonNullList<ItemStack> p_155293_) {
        if (net.minecraftforge.event.ForgeEventFactory.onPotionAttemptBrew(p_155293_)) return;
        ItemStack itemstack = p_155293_.get(3);
        PotionBrewing potionBrewing = p_155291_.potionBrewing();
        for (int i = 0; i < 3; i++) {
            ItemStack brewed = potionBrewing.mix(itemstack, p_155293_.get(i));
            if (!brewed.isEmpty()) {
                brewed.setCount(Math.max(1, brewed.getCount() * MPGConfig.brewing_doubling_value));
            }
            p_155293_.set(i, brewed);
        }
        MPGBrewingLogicHelper.finishBrew(p_155291_, p_155292_.getX(), p_155292_.getY(), p_155292_.getZ(), p_155293_, 3);
        p_155291_.levelEvent(1035, p_155292_, 0);
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag p_155297_, HolderLookup.@NotNull Provider provider) {
        super.loadAdditional(p_155297_, provider);
        this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
        ContainerHelper.loadAllItems(p_155297_, this.items, provider);
        this.brewTime = p_155297_.getShort("BrewTime");
        if (this.brewTime > 0) {
            this.ingredient = this.items.get(3).getItem();
        }
        this.fuel = p_155297_.getByte("Fuel");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag p_187484_, HolderLookup.@NotNull Provider provider) {
        super.saveAdditional(p_187484_, provider);
        p_187484_.putShort("BrewTime", (short) this.brewTime);
        ContainerHelper.saveAllItems(p_187484_, this.items, provider);
        p_187484_.putByte("Fuel", (byte) this.fuel);
    }

    public boolean canPlaceItem(int p_59017_, @NotNull ItemStack p_59018_) {
        PotionBrewing potionBrewing = this.level != null ? this.level.potionBrewing() : PotionBrewing.EMPTY;
        return MPGBrewingLogicHelper.canPlaceItem(potionBrewing, p_59017_, p_59018_, this.getItem(p_59017_));
    }

    public int @NotNull [] getSlotsForFace(@NotNull Direction p_59010_) {
        if (p_59010_ == Direction.UP) {
            return SLOTS_FOR_UP;
        } else {
            return p_59010_ == Direction.DOWN ? SLOTS_FOR_DOWN : SLOTS_FOR_SIDES;
        }
    }

    public boolean canPlaceItemThroughFace(int p_58996_, @NotNull ItemStack p_58997_, @Nullable Direction p_58998_) {
        return this.canPlaceItem(p_58996_, p_58997_);
    }

    public boolean canTakeItemThroughFace(int p_59020_, @NotNull ItemStack p_59021_, @NotNull Direction p_59022_) {
        return p_59020_ != 3 || p_59021_.is(Items.GLASS_BOTTLE);
    }

    protected @NotNull AbstractContainerMenu createMenu(int p_58990_, @NotNull Inventory p_58991_) {
        return new MPGBrewingStandMenu(p_58990_, p_58991_, this, this.dataAccess);
    }

}
