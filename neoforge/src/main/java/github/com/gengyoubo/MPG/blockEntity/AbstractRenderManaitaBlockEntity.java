package github.com.gengyoubo.MPG.blockEntity;

import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.common.client.renderer.MPGBlockEntityRendererBase;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.Supplier;

public abstract class AbstractRenderManaitaBlockEntity<T extends BlockEntity>
        extends MPGBlockEntityRendererBase<T> {
    protected AbstractRenderManaitaBlockEntity(Supplier<ItemStack> displayStackFactory) {
        super(displayStackFactory, MPGBlockCore.HookBlock.get().defaultBlockState());
    }
}
