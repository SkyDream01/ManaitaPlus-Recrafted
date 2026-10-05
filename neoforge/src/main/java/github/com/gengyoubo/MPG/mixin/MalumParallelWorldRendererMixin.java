package github.com.gengyoubo.MPG.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "com.sammy.malum.client.renderer.renderpass.ParallelWorldRenderer", remap = false)
public abstract class MalumParallelWorldRendererMixin {
    // 26.3: TextureTarget's ctor is (String label, int width, int height, GpuFormat color,
    // GpuFormat depth), so the clamped width/height args are at index 1 and 2.
    private static final String TEXTURE_TARGET_CTOR =
            "Lcom/mojang/blaze3d/pipeline/TextureTarget;<init>(Ljava/lang/String;IILcom/mojang/renderpearl/api/GpuFormat;Lcom/mojang/renderpearl/api/GpuFormat;)V";

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = TEXTURE_TARGET_CTOR),
            index = 1
    )
    private int mpg$clampInitialTargetWidth(int width) {
        return Math.max(width, 1);
    }

    @ModifyArg(
            method = "<init>",
            at = @At(value = "INVOKE", target = TEXTURE_TARGET_CTOR),
            index = 2
    )
    private int mpg$clampInitialTargetHeight(int height) {
        return Math.max(height, 1);
    }

    // 26.3: RenderTarget#resize lost its boolean flag, now (int width, int height).
    @ModifyArg(
            method = "resize",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(II)V"),
            index = 0
    )
    private int mpg$clampResizeTargetWidth(int width) {
        return Math.max(width, 1);
    }

    @ModifyArg(
            method = "resize",
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/pipeline/RenderTarget;resize(II)V"),
            index = 1
    )
    private int mpg$clampResizeTargetHeight(int height) {
        return Math.max(height, 1);
    }
}
