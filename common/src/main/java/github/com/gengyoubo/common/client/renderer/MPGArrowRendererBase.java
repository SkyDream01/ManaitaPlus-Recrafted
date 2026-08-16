package github.com.gengyoubo.common.client.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.AbstractArrow;
import org.jetbrains.annotations.NotNull;

/** Shared renderer for the custom arrow entity. */
public class MPGArrowRendererBase<T extends AbstractArrow> extends ArrowRenderer<T> {
    private static final ResourceLocation ARROW_TEXTURE =
            ResourceLocation.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public MPGArrowRendererBase(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull T arrow) {
        return ARROW_TEXTURE;
    }
}
