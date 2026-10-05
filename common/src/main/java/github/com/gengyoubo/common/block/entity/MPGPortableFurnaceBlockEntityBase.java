package github.com.gengyoubo.common.block.entity;

import github.com.gengyoubo.common.util.MPGItemStackData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
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
            loadAdditional(TagValueInput.create(ProblemReporter.DISCARDING, owner.registryAccess(), tag));
        }
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack);
        if (owner.level() instanceof ServerLevel serverLevel && !getItem(0).isEmpty()) {
            processAll(serverLevel);
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
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, owner.registryAccess());
        saveAdditional(output);
        tag.merge(output.buildResult());
        MPGItemStackData.setTag(backingStack, tag);
    }
}
