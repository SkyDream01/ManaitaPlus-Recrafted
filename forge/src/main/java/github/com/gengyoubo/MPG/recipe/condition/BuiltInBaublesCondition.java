package github.com.gengyoubo.MPG.recipe.condition;

import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.baubles.common.capability.BaublesCapability;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record BuiltInBaublesCondition() implements ICondition {
    public static final BuiltInBaublesCondition INSTANCE = new BuiltInBaublesCondition();
    public static final MapCodec<BuiltInBaublesCondition> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public boolean test(@NotNull IContext context, DynamicOps<?> ops) {
        return BaublesCapability.isEnabled();
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec() {
        return CODEC;
    }
}
