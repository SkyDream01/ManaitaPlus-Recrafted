package github.com.gengyoubo.MPG.entity;

import github.com.gengyoubo.common.client.renderer.MPGArrowRendererBase;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderManaitaArrow extends MPGArrowRendererBase<MPGEntityArrow> {
    public RenderManaitaArrow(EntityRendererProvider.Context context) {
        super(context);
    }
}
