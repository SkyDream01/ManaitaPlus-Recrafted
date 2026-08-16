package github.com.gengyoubo.common.block.entity;

import github.com.gengyoubo.common.util.MPGItemStackData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

/** Shared item-backed furnace inventory used by the portable furnace and furnace ring. */
public abstract class MPGPortableFurnaceBlockEntityBase extends MPGFurnaceBlockEntityBase {
    private final Player owner;
    private final ItemStack backingStack;

    protected MPGPortableFurnaceBlockEntityBase(BlockEntityType<?> type, BlockState state,
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
        super.setItem(slot, stack);
        if (!getItem(0).isEmpty()) {
            processAll(owner.level());
        }
        saveToBackingStack();
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
