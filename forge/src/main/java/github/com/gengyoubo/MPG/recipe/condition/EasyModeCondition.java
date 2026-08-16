package github.com.gengyoubo.MPG.recipe.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import github.com.gengyoubo.MPG.MPGConfig;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record EasyModeCondition(boolean value) implements ICondition {
    public static final MapCodec<EasyModeCondition> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(Codec.BOOL.optionalFieldOf("value", true).forGetter(EasyModeCondition::value))
                    .apply(instance, EasyModeCondition::new)
    );

    @Override
    public boolean test(@NotNull IContext context, DynamicOps<?> ops) {
        return MPGConfig.easy_mode_value == value;
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
