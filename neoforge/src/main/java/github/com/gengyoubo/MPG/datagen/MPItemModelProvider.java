package github.com.gengyoubo.MPG.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.com.gengyoubo.MPG.MPG;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/** 26.3: item model predicates/overrides are gone. Item visuals are now picked by an item model
 * definition in {@code assets/<ns>/items/<item_id>.json} dispatching on the custom
 * {@code manaita_plus_recrafted:manaita_plus_recrafted_type} property (registered as a
 * {@code RangeSelectItemModelProperty} in RegisterEventHandler, returning the raw tier 0..8).
 * This provider keeps generating the plain {@code models/item/*.json} models and now emits the
 * {@code items/<id>.json} range dispatch definitions that replace the old
 * {@code override().predicate(...)} lists (same thresholds, 1..8). */
public class MPItemModelProvider implements DataProvider {
    private static final String[] TYPE_TEXTURE_SUFFIXES = {
            "wooden",
            "stone",
            "iron",
            "gold",
            "diamond",
            "emerald",
            "redstone",
            "netherite",
            "netherite"
    };

    private static final String[] TYPED_BLOCK_TEXTURE_SUFFIXES = {
            "wooden",
            "stone",
            "iron",
            "gold",
            "diamond",
            "emerald",
            "redstone",
            "netherite"
    };

    private final PackOutput.PathProvider models;
    private final PackOutput.PathProvider items;
    private final List<CompletableFuture<?>> saves = new ArrayList<>();
    private final Set<String> savedModels = new LinkedHashSet<>();

    public MPItemModelProvider(PackOutput output) {
        this.models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
        this.items = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "items");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        saves.clear();
        savedModels.clear();
        registerModels(cache);
        return CompletableFuture.allOf(saves.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "Item Models : " + MPG.MODID;
    }

    private void registerModels(CachedOutput cache) {
        registerHookItem(cache);
        registerTypedBlockItem(cache, "block_crafting_manaita", "block/crafting_manaita", "crafting/crafting_manaita.", "block/crafting/crafting_manaita_");
        registerTypedBlockItem(cache, "block_furnace_manaita", "block/furnace_manaita", "furnace/furnace_manaita.", "block/furnace/furnace_manaita_");
        registerTypedBlockItem(cache, "block_brewing_manaita", "block/brewing_manaita", "brewing/brewing_manaita.", "block/brewing/brewing_manaita_");
    }

    private void registerHookItem(CachedOutput cache) {
        withExistingParent(cache, "block_hook_manaita", "block/hook/fixed_hook_wooden");

        JsonArray entries = new JsonArray();
        for (int type = 1; type <= 8; type++) {
            String model = "block/hook/fixed_hook_" + TYPE_TEXTURE_SUFFIXES[type];
            withExistingParent(cache, model, model);
            entries.add(rangeEntry(type, modLoc("item/" + model)));
        }
        registerItemDefinition(cache, "block_hook_manaita", entries);
    }

    private void registerTypedBlockItem(CachedOutput cache, String rootName, String baseTexture, String modelPrefix, String texturePrefix) {
        withExistingParent(cache, rootName, baseTexture);

        JsonArray entries = new JsonArray();
        for (int type = 1; type <= 8; type++) {
            String model = modelPrefix + type;
            withExistingParent(cache, model, texturePrefix + TYPED_BLOCK_TEXTURE_SUFFIXES[type - 1]);
            entries.add(rangeEntry(type, modLoc("item/" + model)));
        }
        registerItemDefinition(cache, rootName, entries);
    }

    /** Writes {@code models/item/<name>.json}: an {@code item/generated} model using {@code <texture>} as layer0. */
    private void withExistingParent(CachedOutput cache, String name, String texture) {
        if (!savedModels.add(name)) {
            return;
        }
        JsonObject textures = new JsonObject();
        textures.addProperty("layer0", modLoc(texture));

        JsonObject json = new JsonObject();
        json.addProperty("parent", "item/generated");
        json.add("textures", textures);
        saves.add(DataProvider.saveStable(cache, json, models.json(Identifier.fromNamespaceAndPath(MPG.MODID, "item/" + name))));
    }

    /** Writes {@code items/<name>.json}: a {@code minecraft:range_dispatch} definition on the type property,
     * keeping the old override thresholds (the property reports the tier as 0..8). */
    private void registerItemDefinition(CachedOutput cache, String name, JsonArray entries) {
        JsonObject dispatch = new JsonObject();
        dispatch.addProperty("type", "minecraft:range_dispatch");
        dispatch.addProperty("property", modLoc("manaita_plus_recrafted_type"));
        dispatch.add("entries", entries);
        dispatch.add("fallback", modelRef(modLoc("item/" + name)));

        JsonObject json = new JsonObject();
        json.add("model", dispatch);
        saves.add(DataProvider.saveStable(cache, json, items.json(Identifier.fromNamespaceAndPath(MPG.MODID, name))));
    }

    private static JsonObject rangeEntry(int type, String model) {
        JsonObject entry = new JsonObject();
        entry.addProperty("threshold", type);
        entry.add("model", modelRef(model));
        return entry;
    }

    private static JsonObject modelRef(String model) {
        JsonObject ref = new JsonObject();
        ref.addProperty("type", "minecraft:model");
        ref.addProperty("model", model);
        return ref;
    }

    private static String modLoc(String path) {
        return Identifier.fromNamespaceAndPath(MPG.MODID, path).toString();
    }
}
