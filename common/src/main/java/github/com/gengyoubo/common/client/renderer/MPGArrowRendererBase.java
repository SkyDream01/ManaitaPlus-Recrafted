package github.com.gengyoubo.common.client.renderer;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import org.jetbrains.annotations.NotNull;

/** Shared renderer for the custom arrow entity. */
public class MPGArrowRendererBase<T extends AbstractArrow> extends ArrowRenderer<T, ArrowRenderState> {
    private static final Identifier ARROW_TEXTURE =
            Identifier.withDefaultNamespace("textures/entity/projectiles/arrow.png");

    public MPGArrowRendererBase(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public @NotNull Identifier getTextureLocation(@NotNull ArrowRenderState state) {
        return ARROW_TEXTURE;
    }

    @Override
    public ArrowRenderState createRenderState() {
        return new ArrowRenderState();
    }
}
