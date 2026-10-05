package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import github.com.gengyoubo.MPG.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import java.util.Objects;

public class RenderCraftingManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPCraftingBlockEntity> {
    public RenderCraftingManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(() -> new ItemStack(MPGBlockCore.CraftingBlockItem.get()));
        Objects.requireNonNull(context);
    }
}
