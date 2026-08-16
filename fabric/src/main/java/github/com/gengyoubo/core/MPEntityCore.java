package github.com.gengyoubo.core;

import github.com.gengyoubo.entity.MPGEntityArrow;
import github.com.gengyoubo.entity.MPGLightningBolt;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

import static github.com.gengyoubo.MPG.ENTITY_TYPES;

public class MPEntityCore {
    public static final RegistryObject<EntityType<MPGLightningBolt>> ManaitaLightningBolt =
            register("manaita_lightning_bolt",
                    () -> EntityType.Builder.of(MPGLightningBolt::new, MobCategory.MISC)
                            .noSave()
                            .sized(0.0F, 0.0F)
                            .clientTrackingRange(16)
                            .updateInterval(Integer.MAX_VALUE)
                            .build("manaita_lightning_bolt"));
    public static final RegistryObject<EntityType<MPGEntityArrow>> ManaitaArrow =
            register("manaita_arrow",
                    () -> EntityType.Builder.of(MPGEntityArrow::new, MobCategory.MISC)
                            .noSave()
                            .sized(0.5F, 0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build("manaita_arrow"));

    public static void init() {
    }

    @SuppressWarnings("unchecked")
    private static <T extends Entity> RegistryObject<EntityType<T>> register(String name, Supplier<EntityType<T>> supplier) {
        return (RegistryObject<EntityType<T>>) (RegistryObject<?>) ENTITY_TYPES.register(name, (Supplier<EntityType<?>>) (Supplier<?>) supplier);
    }

}


