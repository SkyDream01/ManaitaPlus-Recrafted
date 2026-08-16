package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import github.com.gengyoubo.MPG.block.entity.MPFurnaceBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderFurnaceManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPFurnaceBlockEntity> {
    public RenderFurnaceManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(new ItemStack(MPGBlockCore.FurnaceBlockItem.get()));
        Objects.requireNonNull(context);
    }
}

