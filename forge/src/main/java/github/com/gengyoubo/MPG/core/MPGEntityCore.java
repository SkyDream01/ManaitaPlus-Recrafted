package github.com.gengyoubo.MPG.core;

import net.minecraft.world.entity.EntityType;
import github.com.gengyoubo.common.registry.MPGEntityTypeFactory;
import github.com.gengyoubo.common.registry.MPGRegistryIds;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.MPG.entity.MPGEntityArrow;
import github.com.gengyoubo.MPG.entity.MPGLightningBolt;

import static github.com.gengyoubo.MPG.MPG.ENTITY_TYPES;

public class MPGEntityCore {
    public static final RegistryObject<EntityType<MPGLightningBolt>> ManaitaLightningBolt =
            ENTITY_TYPES.register(MPGRegistryIds.LIGHTNING, () -> MPGEntityTypeFactory.lightning(MPGLightningBolt::new));
    public static final RegistryObject<EntityType<MPGEntityArrow>> ManaitaArrow =
            ENTITY_TYPES.register(MPGRegistryIds.ARROW, () -> MPGEntityTypeFactory.arrow(MPGEntityArrow::new));

    public static void init() {
    }

}
