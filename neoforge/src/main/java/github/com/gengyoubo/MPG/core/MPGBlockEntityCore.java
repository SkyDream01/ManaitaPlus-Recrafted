package github.com.gengyoubo.MPG.core;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import github.com.gengyoubo.MPG.block.entity.MPBrewingStandBlockEntity;
import github.com.gengyoubo.MPG.block.entity.MPCraftingBlockEntity;
import github.com.gengyoubo.MPG.block.entity.MPFurnaceBlockEntity;

import java.util.Set;

import static github.com.gengyoubo.MPG.MPG.BLOCK_ENTITY_TYPES;

public class MPGBlockEntityCore {
    // BlockEntityType.Builder is gone in 26.3; the type takes its factory and valid blocks directly.
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MPFurnaceBlockEntity>> FURNACE_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("furnace_block_entity", () -> new BlockEntityType<>(
                    MPFurnaceBlockEntity::new, Set.of(MPGBlockCore.FurnaceBlock.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MPBrewingStandBlockEntity>> BREWING_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("brewing_block_entity", () -> new BlockEntityType<>(
                    MPBrewingStandBlockEntity::new, Set.of(MPGBlockCore.BrewingBlock.get())));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MPCraftingBlockEntity>> CRAFTING_BLOCK_ENTITY =
            BLOCK_ENTITY_TYPES.register("crafting_entity", () -> new BlockEntityType<>(
                    MPCraftingBlockEntity::new, Set.of(MPGBlockCore.CraftingBlock.get())));

    public static void init() {
    }

}

