package github.com.gengyoubo.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.block.MPBrewingStandBlock;
import github.com.gengyoubo.block.MPFurnaceBlock;
import github.com.gengyoubo.block.MPCraftingBlock;
import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.block.item.MPHookBlockItem;

import static github.com.gengyoubo.MPG.BLOCKS;
import static github.com.gengyoubo.MPG.ITEMS;

public class MPBlockCore {
    public static final RegistryObject<Block> CraftingBlock = BLOCKS.register("block_crafting_manaita", MPCraftingBlock::new);
    public static final RegistryObject<Item> CraftingBlockItem = ITEMS.register("block_crafting_manaita", MPCraftingBlockItem::new);

    public static final RegistryObject<Block> FurnaceBlock = BLOCKS.register("block_furnace_manaita", MPFurnaceBlock::new);
    public static final RegistryObject<Item> FurnaceBlockItem = ITEMS.register("block_furnace_manaita", MPFurnaceBlockItem::new);

    public static final RegistryObject<Block> BrewingBlock = BLOCKS.register("block_brewing_manaita", MPBrewingStandBlock::new);
    public static final RegistryObject<Item> BrewingBlockItem = ITEMS.register("block_brewing_manaita", MPBrewingBlockItem::new);

    public static final RegistryObject<Block> HookBlock = BLOCKS.register("block_hook_manaita", MPHookBlock::new);
    public static final RegistryObject<Item> HookBlockItem = ITEMS.register("block_hook_manaita", MPHookBlockItem::new);

    public static void init() {
    }

}


