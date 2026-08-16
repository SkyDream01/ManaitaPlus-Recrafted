package github.com.gengyoubo.MPG.mixin;

import mezz.jei.library.gui.widgets.AbstractScrollWidget;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = AbstractScrollWidget.class, remap = false)
public abstract class AbstractScrollWidgetMixin {
    @Shadow
    protected abstract int getHiddenAmount();

    @Shadow
    protected abstract void drawContents(GuiGraphics guiGraphics, double mouseX, double mouseY, float scrollOffsetY);

    @Inject(method = "drawWidget", at = @At("HEAD"), cancellable = true)
    private void mpg$hideScrollbarWhenNotScrollable(
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY,
            CallbackInfo ci
    ) {
        if (this.getHiddenAmount() <= 0) {
            this.drawContents(guiGraphics, mouseX, mouseY, 0.0F);
            ci.cancel();
        }
    }
}
