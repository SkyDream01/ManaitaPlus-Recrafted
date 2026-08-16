package github.com.gengyoubo.MPG.core;

import com.mojang.serialization.MapCodec;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.recipe.condition.BuiltInBaublesCondition;
import github.com.gengyoubo.MPG.recipe.condition.EasyModeCondition;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class MPGConditionCore {
    public static final DeferredRegister<MapCodec<? extends ICondition>> CONDITION_CODECS =
            DeferredRegister.create(ForgeRegistries.Keys.CONDITION_SERIALIZERS, MPG.MODID);

    public static final RegistryObject<MapCodec<? extends ICondition>> EASY_MODE =
            CONDITION_CODECS.register("easy_mode", () -> EasyModeCondition.CODEC);
    public static final RegistryObject<MapCodec<? extends ICondition>> BUILT_IN_BAUBLES =
            CONDITION_CODECS.register("built_in_baubles", () -> BuiltInBaublesCondition.CODEC);

    private MPGConditionCore() {
    }

    public static void init() {

    }
}
