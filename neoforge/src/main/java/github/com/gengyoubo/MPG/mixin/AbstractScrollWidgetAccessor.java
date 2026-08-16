package github.com.gengyoubo.MPG.mixin;

import mezz.jei.common.util.ImmutableRect2i;
import mezz.jei.library.gui.widgets.AbstractScrollWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = AbstractScrollWidget.class, remap = false)
public interface AbstractScrollWidgetAccessor {
    @Accessor("area")
    ImmutableRect2i mpg$getArea();
}
