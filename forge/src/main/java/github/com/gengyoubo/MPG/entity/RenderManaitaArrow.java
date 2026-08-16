package github.com.gengyoubo.MPG.entity;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderManaitaArrow extends ArrowRenderer<MPGEntityArrow> {
    public static final ResourceLocation NORMAL_ARROW_LOCATION =
            ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");
    public RenderManaitaArrow(EntityRendererProvider.Context p_174422_) {
        super(p_174422_);
    }

    public @NotNull ResourceLocation getTextureLocation(@NotNull MPGEntityArrow p_116140_) {
        return NORMAL_ARROW_LOCATION;
    }
}
