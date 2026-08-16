package github.com.gengyoubo.MPG.datagen;

import github.com.gengyoubo.MPG.MPG;
import github.com.gengyoubo.MPG.block.data.MPGBlockData;
import github.com.gengyoubo.MPG.core.MPGBlockCore;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ConfiguredModel;
import net.neoforged.neoforge.client.model.generators.MultiPartBlockStateBuilder;
import net.neoforged.neoforge.client.model.generators.VariantBlockStateBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class MPBlockStateProvider extends BlockStateProvider {
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

    public MPBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MPG.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        registerHookStates();
        registerTypedMultipartStates(MPGBlockCore.CraftingBlock.get(), "block/block_crafting_manaita", "item/crafting/crafting_manaita.");
        registerTypedMultipartStates(MPGBlockCore.FurnaceBlock.get(), "block/block_furnace_manaita", "item/furnace/furnace_manaita.");
        registerTypedMultipartStates(MPGBlockCore.BrewingBlock.get(), "block/block_brewing_manaita", "item/brewing/brewing_manaita.");
    }

    private void registerHookStates() {
        VariantBlockStateBuilder builder = getVariantBuilder(MPGBlockCore.HookBlock.get());

        for (int type = 0; type <= 8; type++) {
            String suffix = TYPE_MODEL_SUFFIXES[type];
            ResourceLocation model = modLoc("block/hook/fixed_hook_" + suffix);

            for (Direction facing : Direction.Plane.HORIZONTAL) {
                builder.partialState()
                        .with(MPGBlockData.TYPES, type)
                        .with(MPGBlockData.FACING, facing)
                        .addModels(new ConfiguredModel(models().getExistingFile(model), 0, yRotFromFacing(facing), false));
            }
        }
    }

    private void registerTypedMultipartStates(Block block, String baseModelPath, String typedModelPrefix) {
        MultiPartBlockStateBuilder builder = getMultipartBuilder(block);

        builder.part()
                .modelFile(models().getExistingFile(modLoc(baseModelPath)))
                .addModel()
                .condition(MPGBlockData.TYPES, 0)
                .end();

        for (int type = 1; type <= 8; type++) {
            builder.part()
                    .modelFile(models().getExistingFile(modLoc(typedModelPrefix + type)))
                    .addModel()
                    .condition(MPGBlockData.TYPES, type)
                    .end();
        }
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
