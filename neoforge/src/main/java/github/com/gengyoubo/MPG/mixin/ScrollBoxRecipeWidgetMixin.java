package github.com.gengyoubo.MPG.mixin;

import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.widgets.IScrollBoxWidget;
import mezz.jei.common.gui.elements.DrawableWrappedText;
import mezz.jei.library.gui.widgets.AbstractScrollWidget;
import mezz.jei.library.gui.widgets.ScrollBoxRecipeWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.FormattedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(value = ScrollBoxRecipeWidget.class, remap = false)
public abstract class ScrollBoxRecipeWidgetMixin {
    @Shadow
    private IDrawable contents;

    @Shadow
    public abstract int getContentAreaHeight();

    @Inject(method = "getContentAreaWidth", at = @At("HEAD"), cancellable = true)
    private void mpg$useFullWidthByDefault(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(this.mpg$getWideContentWidth());
    }

    @Inject(method = "setContents(Ljava/util/List;)Lmezz/jei/api/gui/widgets/IScrollBoxWidget;", at = @At("HEAD"), cancellable = true)
    private void mpg$wrapTextWithScrollbarSpace(List<FormattedText> text, CallbackInfoReturnable<IScrollBoxWidget> cir) {
        IDrawable wideContents = new DrawableWrappedText(text, this.mpg$getWideContentWidth());
        if (wideContents.getHeight() > this.getContentAreaHeight()) {
            this.contents = new DrawableWrappedText(text, this.mpg$getNarrowContentWidth());
        } else {
            this.contents = wideContents;
        }
        cir.setReturnValue((IScrollBoxWidget) this);
    }

    @Inject(method = "drawContents", at = @At("HEAD"), cancellable = true)
    private void mpg$drawFullWidthWhenScrollbarIsHidden(
            GuiGraphics guiGraphics,
            double mouseX,
            double mouseY,
            float scrollOffsetY,
            CallbackInfo ci
    ) {
        if (this.contents.getHeight() <= this.getContentAreaHeight()) {
            this.contents.draw(guiGraphics);
            ci.cancel();
        }
    }

    @Unique
    private int mpg$getWideContentWidth() {
        return Math.max(1, ((AbstractScrollWidgetAccessor) this).mpg$getArea().width() + 8);
    }

    @Unique
    private int mpg$getNarrowContentWidth() {
        return Math.max(1, this.mpg$getWideContentWidth() - AbstractScrollWidget.getScrollBoxScrollbarExtraWidth());
    }
}
