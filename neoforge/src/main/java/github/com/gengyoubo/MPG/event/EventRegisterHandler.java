package github.com.gengyoubo.MPG.event;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.core.MPGAttributeCore;
import github.com.gengyoubo.MPG.datagen.MPBlockStateProvider;
import github.com.gengyoubo.MPG.datagen.MPItemModelProvider;
import github.com.gengyoubo.MPG.loottable.MPGLootTable;

@EventBusSubscriber(modid = MPG.MODID)
public class EventRegisterHandler {
    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.getTypes().forEach(entityType -> event.add(entityType, MPGAttributeCore.Type));
    }


    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();

        gen.addProvider(event.includeServer(), new MPGLootTable(gen.getPackOutput(), event.getLookupProvider()));

        PackOutput packOutput = gen.getPackOutput();
        gen.addProvider(event.includeClient(), new MPBlockStateProvider(packOutput, event.getExistingFileHelper()));
        gen.addProvider(event.includeClient(), new MPItemModelProvider(packOutput, event.getExistingFileHelper()));
    }

}

