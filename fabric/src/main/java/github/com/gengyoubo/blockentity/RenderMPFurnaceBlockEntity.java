package github.com.gengyoubo.blockentity;

import github.com.gengyoubo.block.entity.MPFurnaceBlockEntity;
import github.com.gengyoubo.core.MPBlockCore;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class RenderMPFurnaceBlockEntity extends AbstractRenderMPBlockEntity<MPFurnaceBlockEntity> {
    public RenderMPFurnaceBlockEntity(BlockEntityRendererProvider.Context context) {
        super(new ItemStack(MPBlockCore.FurnaceBlockItem.get()));
    }
}
