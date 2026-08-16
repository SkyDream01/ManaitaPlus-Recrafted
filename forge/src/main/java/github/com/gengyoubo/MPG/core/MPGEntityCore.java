package github.com.gengyoubo.MPG.core;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.MPG.entity.MPGEntityArrow;
import github.com.gengyoubo.MPG.entity.MPGLightningBolt;

import static github.com.gengyoubo.MPG.MPG.ENTITY_TYPES;

public class MPGEntityCore {
    public static final RegistryObject<EntityType<MPGLightningBolt>> ManaitaLightningBolt = ENTITY_TYPES.register("manaita_lightning_bolt",() -> EntityType.Builder.of(MPGLightningBolt::new, MobCategory.MISC).noSave().sized(0.0F, 0.0F).clientTrackingRange(16).updateInterval(Integer.MAX_VALUE).build("manaita_lightning_bolt"));
    public static final RegistryObject<EntityType<MPGEntityArrow>> ManaitaArrow = ENTITY_TYPES.register("manaita_arrow",() -> EntityType.Builder.of(MPGEntityArrow::new, MobCategory.MISC).noSave().sized(0.5F, 0.5F).clientTrackingRange(4).updateInterval(20).build("manaita_arrow"));

    public static void init() {
    }

}
