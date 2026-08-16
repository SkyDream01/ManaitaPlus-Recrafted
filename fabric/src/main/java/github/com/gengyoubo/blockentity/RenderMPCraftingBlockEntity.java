package github.com.gengyoubo.blockentity;

import github.com.gengyoubo.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.core.MPBlockCore;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;

public class RenderMPCraftingBlockEntity extends AbstractRenderMPBlockEntity<MPCraftingBlockEntity> {
    public RenderMPCraftingBlockEntity(BlockEntityRendererProvider.Context context) {
        super(new ItemStack(MPBlockCore.CraftingBlockItem.get()));
    }
}
