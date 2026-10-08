package github.com.gengyoubo.MPG.event;

import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import java.util.Set;
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


    // GatherDataEvent is split into Server/Client variants in 26.3 (includeServer/includeClient
    // are gone). Loot tables are dynamic-registry data now, so MPGLootTable is a registry
    // bootstrap wired through RegistrySetBuilder like vanilla's LootTableProvider.
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Server event) {
        // The loot injects into the vanilla minecraft:end_city_treasure table, so the
        // "minecraft" namespace must be included in the output filter (it defaults to the
        // owning mod id and would silently drop the entry).
        event.createReloadableRegistryObjects(new RegistrySetBuilder()
                .add(Registries.LOOT_TABLE, new MPGLootTable()),
                Set.of("minecraft", "manaita_plus_recrafted"));
    }

    // ExistingFileHelper no longer exists in 26.3, so the model providers only take the PackOutput.
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent.Client event) {
        PackOutput packOutput = event.getGenerator().getPackOutput();
        event.addProvider(new MPBlockStateProvider(packOutput));
        event.addProvider(new MPItemModelProvider(packOutput));
    }

}

