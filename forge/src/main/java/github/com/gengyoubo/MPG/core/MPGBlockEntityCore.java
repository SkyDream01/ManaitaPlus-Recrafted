package github.com.gengyoubo.MPG.core;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.MPG.block.entity.MPFurnaceBlockEntity;

import static github.com.gengyoubo.MPG.MPG.BLOCK_ENTITY_TYPES;

public class MPGBlockEntityCore {
    public static final RegistryObject<BlockEntityType<MPFurnaceBlockEntity>> FURNACE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("furnace_block_entity", () -> buildType(BlockEntityType.Builder.of(MPFurnaceBlockEntity::new, MPGBlockCore.FurnaceBlock.get())));
    public static final RegistryObject<BlockEntityType<MPBrewingStandBlockEntity>> BREWING_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("brewing_block_entity", () -> buildType(BlockEntityType.Builder.of(MPBrewingStandBlockEntity::new, MPGBlockCore.BrewingBlock.get())));
    public static final RegistryObject<BlockEntityType<MPCraftingBlockEntity>> CRAFTING_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("crafting_entity", () -> buildType(BlockEntityType.Builder.of(MPCraftingBlockEntity::new, MPGBlockCore.CraftingBlock.get())));

    @SuppressWarnings("DataFlowIssue")
    private static <T extends net.minecraft.world.level.block.entity.BlockEntity> BlockEntityType<T> buildType(BlockEntityType.Builder<T> builder) {
        return builder.build(null);
    }

    public static void init() {
    }

}
