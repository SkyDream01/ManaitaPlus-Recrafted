package github.com.gengyoubo.blockentity;

import github.com.gengyoubo.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.core.MPBlockCore;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class RenderMPBrewingBlockEntity extends AbstractRenderMPBlockEntity<MPBrewingStandBlockEntity> {
    public RenderMPBrewingBlockEntity(BlockEntityRendererProvider.Context context) {
        super(new ItemStack(MPBlockCore.BrewingBlockItem.get()));
    }
}
