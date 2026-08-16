package github.com.gengyoubo.common.item.data;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IMPGDestroy {
    boolean accept(BlockState state);

    int getRange(ItemStack itemStack);
}
