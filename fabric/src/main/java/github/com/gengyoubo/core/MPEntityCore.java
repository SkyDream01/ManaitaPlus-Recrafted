package github.com.gengyoubo.core;

import github.com.gengyoubo.entity.MPGEntityArrow;
import github.com.gengyoubo.entity.MPGLightningBolt;
import github.com.gengyoubo.common.registry.MPGEntityTypeFactory;
import github.com.gengyoubo.common.registry.MPGRegistryIds;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static github.com.gengyoubo.MPG.ENTITY_TYPES;

public class MPEntityCore {
    public static final RegistryObject<EntityType<MPGLightningBolt>> ManaitaLightningBolt =
            register(MPGRegistryIds.LIGHTNING, () -> MPGEntityTypeFactory.lightning(MPGLightningBolt::new));
    public static final RegistryObject<EntityType<MPGEntityArrow>> ManaitaArrow =
            register(MPGRegistryIds.ARROW, () -> MPGEntityTypeFactory.arrow(MPGEntityArrow::new));

    public static void init() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType<T>> supplier) {
        return (RegistryObject<EntityType<T>>) (RegistryObject<?>) ENTITY_TYPES.register(name, (Supplier<EntityType<?>>) (Supplier<?>) supplier);
    }

}

