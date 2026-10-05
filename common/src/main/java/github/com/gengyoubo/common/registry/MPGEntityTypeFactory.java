package github.com.gengyoubo.common.registry;

import github.com.gengyoubo.common.MPGCommon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

/** Loader-neutral entity type definitions. */
public final class MPGEntityTypeFactory {
    private MPGEntityTypeFactory() {
    }

    public static ResourceKey<EntityType<?>> key(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Identifier.fromNamespaceAndPath(MPGCommon.MOD_ID, name));
    }

    public static <T extends Entity> EntityType<T> lightning(EntityType.EntityFactory<T> factory) {
        ResourceKey<EntityType<?>> key = key(MPGRegistryIds.LIGHTNING);
        return EntityType.Builder.of(factory, MobCategory.MISC)
                .noSave()
                .sized(0.0F, 0.0F)
                .clientTrackingRange(16)
                .updateInterval(Integer.MAX_VALUE)
                .build(key);
    }

    public static <T extends Entity> EntityType<T> arrow(EntityType.EntityFactory<T> factory) {
        ResourceKey<EntityType<?>> key = key(MPGRegistryIds.ARROW);
        return EntityType.Builder.of(factory, MobCategory.MISC)
                .noSave()
                .sized(0.5F, 0.5F)
                .clientTrackingRange(4)
                .updateInterval(20)
                .build(key);
    }
}
