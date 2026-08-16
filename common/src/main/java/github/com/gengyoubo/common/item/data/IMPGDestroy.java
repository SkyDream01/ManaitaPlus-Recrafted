package github.com.gengyoubo.common.item.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IMPGDestroy {
    boolean accept(BlockState state);

    int getRange(ItemStack itemStack);

    default int getDepth(ItemStack itemStack) {
        return 1;
    }

    default boolean canHarvest(ItemStack itemStack) {
        return true;
    }

    default boolean canDigUnderPlayer(ItemStack itemStack) {
        return true;
    }

    default boolean requiresShiftForDoubling() {
        return true;
    }
}
