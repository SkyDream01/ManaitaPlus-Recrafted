package github.com.gengyoubo.MPG.core;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.recipe.condition.EasyModeCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

public final class MPGConditionCore {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(NeoForgeRegistries.Keys.CONDITION_CODECS, MPG.MODID);

    public static final DeferredHolder<MapCodec<? extends ICondition>, MapCodec<? extends ICondition>> EASY_MODE =
            CONDITION_CODECS.register("easy_mode", () -> EasyModeCondition.CODEC);

    private MPGConditionCore() {
    }

    public static void init() {

    }
}
