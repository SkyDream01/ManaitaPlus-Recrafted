package github.com.gengyoubo.common.block.entity;

import github.com.gengyoubo.common.util.MPGItemStackData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Shared item-backed brewing inventory used by the portable brewing stand. */
public abstract class MPGPortableBrewingBlockEntityBase extends MPGBrewingStandBlockEntityBase {
    private final Player owner;
    private final ItemStack backingStack;

    protected MPGPortableBrewingBlockEntityBase(BlockEntityType<?> type, BlockState state,
                                                Player owner, ItemStack backingStack) {
        super(type, owner.blockPosition(), state);
        this.owner = owner;
        this.backingStack = backingStack;
        CompoundTag tag = MPGItemStackData.getTag(backingStack);
        if (tag != null) {
            loadAdditional(tag, owner.registryAccess());
        }
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (slot >= 0 && slot < items.size()) {
            items.set(slot, stack);
            if (fuel <= 0 && items.get(4).is(Items.BLAZE_POWDER)) {
                fuel = 20;
                items.get(4).shrink(1);
            }
            PotionBrewing potionBrewing = owner.level().potionBrewing();
            if (isBrewable(potionBrewing, items) && fuel > 0) {
                fuel--;
                brewTime = BREW_TIME;
                ingredient = items.get(3).getItem();
                performBrew(owner.level(), owner.getX(), owner.getY(), owner.getZ());
                brewTime = 0;
            }
        }
        saveToBackingStack();
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        super.clearContent();
        saveToBackingStack();
    }

    private void saveToBackingStack() {
        CompoundTag tag = MPGItemStackData.getOrCreateTag(backingStack);
        saveAdditional(tag, owner.registryAccess());
        MPGItemStackData.setTag(backingStack, tag);
    }
}
