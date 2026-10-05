package github.com.gengyoubo.MPG.datagen;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.block.data.MPGBlockData;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/** Writes blockstates and the empty models used by entity-rendered mounted blocks. */
public class MPBlockStateProvider implements DataProvider {
    private static final String[] TYPE_MODEL_SUFFIXES = {
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

    private final PackOutput.PathProvider blockStates;
    private final PackOutput.PathProvider models;
    private final List<CompletableFuture<?>> saves = new ArrayList<>();

    public MPBlockStateProvider(PackOutput output) {
        this.blockStates = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "blockstates");
        this.models = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models");
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        saves.clear();
        registerStatesAndModels(cache);
        return CompletableFuture.allOf(saves.toArray(new CompletableFuture[0]));
    }

    @Override
    public String getName() {
        return "Block States : " + MPG.MODID;
    }

    private void registerStatesAndModels(CachedOutput cache) {
        registerHookStates(cache);
        registerEntityRenderedBlock(cache, MPGBlockCore.CraftingBlock.get(), "crafting_manaita");
        registerEntityRenderedBlock(cache, MPGBlockCore.FurnaceBlock.get(), "furnace_manaita");
        registerEntityRenderedBlock(cache, MPGBlockCore.BrewingBlock.get(), "brewing_manaita");
    }

    private void registerHookStates(CachedOutput cache) {
        JsonObject variants = new JsonObject();

        for (int type = 0; type <= 8; type++) {
            String suffix = TYPE_MODEL_SUFFIXES[type];
            String model = modLoc("block/hook/fixed_hook_" + suffix);

            for (Direction facing : Direction.Plane.HORIZONTAL) {
                JsonObject variant = new JsonObject();
                variant.addProperty("model", model);
                int yRot = yRotFromFacing(facing);
                if (yRot != 0) {
                    variant.addProperty("y", yRot);
                }
                variants.add(MPGBlockData.FACING.getName() + "=" + facing.getName() + "," + MPGBlockData.TYPES.getName() + "=" + type, variant);
            }
        }

        JsonObject json = new JsonObject();
        json.add("variants", variants);
        save(cache, MPGBlockCore.HookBlock.get(), json);
    }

    private void registerEntityRenderedBlock(CachedOutput cache, Block block, String textureName) {
        // These blocks use RenderShape.INVISIBLE; their BER selects the tiered item and hook.
        // A multipart containing item/generated models nests ModelBaker.compute calls and
        // deadlocks the parallel model cache in 26.3. All states can share empty geometry.
        String modelPath = "block/empty_" + textureName;
        JsonObject textures = new JsonObject();
        textures.addProperty("particle", modLoc("block/" + textureName));
        JsonObject model = new JsonObject();
        model.add("textures", textures);
        model.add("elements", new JsonArray());
        saves.add(DataProvider.saveStable(cache, model,
                models.json(Identifier.fromNamespaceAndPath(MPG.MODID, modelPath))));

        JsonObject variant = new JsonObject();
        variant.addProperty("model", modLoc(modelPath));
        JsonObject variants = new JsonObject();
        variants.add("", variant);
        JsonObject blockState = new JsonObject();
        blockState.add("variants", variants);
        save(cache, block, blockState);
    }

    private static String modLoc(String path) {
        return Identifier.fromNamespaceAndPath(MPG.MODID, path).toString();
    }

    private void save(CachedOutput cache, Block block, JsonObject json) {
        Identifier id = BuiltInRegistries.BLOCK.getKey(block);
        saves.add(DataProvider.saveStable(cache, json, blockStates.json(id)));
    }

    private static int yRotFromFacing(Direction facing) {
        return switch (facing) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}
