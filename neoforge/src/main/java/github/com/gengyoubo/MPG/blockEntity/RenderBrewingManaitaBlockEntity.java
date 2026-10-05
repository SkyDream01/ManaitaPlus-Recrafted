package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import java.util.Objects;

public class RenderBrewingManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPBrewingStandBlockEntity> {
    public RenderBrewingManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(() -> new ItemStack(MPGBlockCore.BrewingBlockItem.get()));
        Objects.requireNonNull(context);
    }
}
