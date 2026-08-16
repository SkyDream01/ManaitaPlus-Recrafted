package github.com.gengyoubo.MPG.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.sammy.malum.client.renderer.renderpass.ParallelWorldRenderer", remap = false)
public abstract class MalumParallelWorldRendererMixin {
    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/TextureTarget;<init>(IIZZ)V"),
            index = 0
    )
    private int mpg$clampInitialTargetWidth(int width) {
        return Math.max(width, 1);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/TextureTarget;<init>(IIZZ)V"),
            index = 1
    )
    private int mpg$clampInitialTargetHeight(int height) {
        return Math.max(height, 1);
    }

    @ModifyArg(
            method = "resize",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(IIZ)V"),
            index = 0
    )
    private int mpg$clampResizeTargetWidth(int width) {
        return Math.max(width, 1);
    }

    @ModifyArg(
            method = "resize",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(IIZ)V"),
            index = 1
    )
    private int mpg$clampResizeTargetHeight(int height) {
        return Math.max(height, 1);
    }
}
