package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderBrewingManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPBrewingStandBlockEntity> {
    public RenderBrewingManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(new ItemStack(MPGBlockCore.BrewingBlockItem.get()));
        Objects.requireNonNull(context);
    }
}

