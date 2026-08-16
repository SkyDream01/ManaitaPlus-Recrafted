package github.com.gengyoubo.blockentity;

import github.com.gengyoubo.common.client.renderer.MPGBlockEntityRendererBase;
import github.com.gengyoubo.core.MPBlockCore;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class AbstractRenderMPBlockEntity<T extends BlockEntity> extends MPGBlockEntityRendererBase<T> {
    protected AbstractRenderMPBlockEntity(ItemStack displayStack) {
        super(displayStack, MPBlockCore.HookBlock.get().defaultBlockState());
    }
}
