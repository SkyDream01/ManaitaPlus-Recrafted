package github.com.gengyoubo.common.registry;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Loader-neutral entity type definitions. */
public final class MPGEntityTypeFactory {
    private MPGEntityTypeFactory() {
    }

    public static <T extends Entity> EntityType<T> lightning(EntityType.EntityFactory<T> factory) {
        return EntityType.Builder.of(factory, MobCategory.MISC)
                .noSave()
                .sized(0.0F, 0.0F)
                .clientTrackingRange(16)
                .updateInterval(Integer.MAX_VALUE)
                .build(MPGRegistryIds.LIGHTNING);
    }

    public static <T extends Entity> EntityType<T> arrow(EntityType.EntityFactory<T> factory) {
        return EntityType.Builder.of(factory, MobCategory.MISC)
                .noSave()
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(MPGRegistryIds.ARROW);
    }
}
