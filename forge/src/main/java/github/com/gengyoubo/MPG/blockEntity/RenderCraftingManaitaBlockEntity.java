package github.com.gengyoubo.MPG.blockEntity;

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import github.com.gengyoubo.MPG.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public class RenderCraftingManaitaBlockEntity extends AbstractRenderManaitaBlockEntity<MPCraftingBlockEntity> {
    public RenderCraftingManaitaBlockEntity(BlockEntityRendererProvider.Context context) {
        super(new ItemStack(MPGBlockCore.CraftingBlockItem.get()));
        Objects.requireNonNull(context);
    }
}

