package github.com.gengyoubo.common.block;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** Creates the typed block and optional hook drops used by all MPG machine blocks. */
public final class MPGTypedBlockDrops {
    private MPGTypedBlockDrops() {
    }

    public static @NotNull List<ItemStack> create(BlockState state, Supplier<? extends Item> hookItem) {
        List<ItemStack> drops = new ArrayList<>(2);
        ItemStack blockStack = new ItemStack(state.getBlock());
        MPGItemStackData.putInt(blockStack, MPGNBTData.ItemType, state.getValue(MPGBlockData.TYPES));
        drops.add(blockStack);

        int hook = state.getValue(MPGBlockData.HOOK);
        if (hook != 8) {
            ItemStack hookStack = new ItemStack(hookItem.get());
            MPGItemStackData.putInt(hookStack, MPGNBTData.ItemType, hook);
            drops.add(hookStack);
        }
        return drops;
    }
}
