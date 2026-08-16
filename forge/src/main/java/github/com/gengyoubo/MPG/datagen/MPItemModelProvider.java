package github.com.gengyoubo.MPG.datagen;

import github.com.gengyoubo.MPG.MPG;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class MPItemModelProvider extends ItemModelProvider {
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

    public MPItemModelProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, MPG.MODID, exFileHelper);
    }

    @Override
    protected void registerModels() {
        registerHookItem();
        registerTypedBlockItem("block_crafting_manaita", "block/crafting_manaita", "crafting/crafting_manaita.", "block/crafting/crafting_manaita_");
        registerTypedBlockItem("block_furnace_manaita", "block/furnace_manaita", "furnace/furnace_manaita.", "block/furnace/furnace_manaita_");
        registerTypedBlockItem("block_brewing_manaita", "block/brewing_manaita", "brewing/brewing_manaita.", "block/brewing/brewing_manaita_");
    }

    private void registerHookItem() {
        ItemModelBuilder builder = withExistingParent("block_hook_manaita", "item/generated")
                .texture("layer0", modLoc("block/hook/fixed_hook_wooden"));

        for (int type = 1; type <= 8; type++) {
            builder.override()
                    .predicate(modLoc("manaita_plus_general_type"), type)
                    .model(withExistingParent("block/hook/fixed_hook_" + TYPE_TEXTURE_SUFFIXES[type], "item/generated")
                            .texture("layer0", modLoc("block/hook/fixed_hook_" + TYPE_TEXTURE_SUFFIXES[type])));
        }
    }

    private void registerTypedBlockItem(String rootName, String baseTexture, String modelPrefix, String texturePrefix) {
        ItemModelBuilder builder = withExistingParent(rootName, "item/generated")
                .texture("layer0", modLoc(baseTexture));

        for (int type = 1; type <= 8; type++) {
            withExistingParent(modelPrefix + type, "item/generated")
                    .texture("layer0", modLoc(texturePrefix + TYPED_BLOCK_TEXTURE_SUFFIXES[type - 1]));
        }

        for (int type = 1; type <= 8; type++) {
            builder.override()
                    .predicate(modLoc("manaita_plus_general_type"), type)
                    .model(getBuilder(modelPrefix + type));
        }
    }
}
