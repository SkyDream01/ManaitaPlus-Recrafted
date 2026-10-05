package github.com.gengyoubo.MPG.loottable;

import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
import github.com.gengyoubo.MPG.core.MPGItemCore;

import java.util.List;
import java.util.Set;

import static net.minecraft.world.level.storage.loot.BuiltInLootTables.END_CITY_TREASURE;

/** 26.3: loot tables are dynamic-registry data ({@code Registries.LOOT_TABLE}); this provider is a
 * {@link net.minecraft.core.registries.SingleRegistryBootstrap} like vanilla's, wired into datagen via
 * {@code RegistrySetBuilder.add(Registries.LOOT_TABLE, new MPGLootTable())}. */
public class MPGLootTable extends LootTableProvider {
    public MPGLootTable() {
        super(Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(ManaitaPlusLoot::new, LootContextParamSets.CHEST)
        ));
    }

    public static class ManaitaPlusLoot implements LootTableSubProvider {
        private final LootTableSubProvider.Context output;

        public ManaitaPlusLoot(LootTableSubProvider.Context output) {
            this.output = output;
        }

        @Override
        public void run() {
            this.output.accept(
                    END_CITY_TREASURE,
                    LootTable.lootTable()
                            .withPool(LootPool.lootPool()
                                    .setRolls(ContextIntProviders.exactly(1))
                                    .add(LootItem.lootTableItem(MPGItemCore.ManaitaBow.get())
                                            .setWeight(1)
                                            .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(1))))
                                    .when(LootItemRandomChanceCondition.randomChance(0.1F)))
            );
        }
    }
}
