package github.com.gengyoubo.resource;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG;
import github.com.gengyoubo.MPGConfig;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditionType;
import net.minecraft.core.HolderLookup;

public final class EasyModeResourceCondition implements ResourceCondition {
    public static final MapCodec<EasyModeResourceCondition> CODEC = MapCodec.unit(EasyModeResourceCondition::new);
    public static final ResourceConditionType<EasyModeResourceCondition> TYPE =
            ResourceConditionType.create(github.com.gengyoubo.util.MPResource.id(MPG.MODID, "easy_mode"), CODEC);

    @Override
    public ResourceConditionType<?> getType() {
        return TYPE;
    }

    @Override
    public boolean test(HolderLookup.Provider registryLookup) {
        return MPGConfig.easy_mode_value;
    }
}
