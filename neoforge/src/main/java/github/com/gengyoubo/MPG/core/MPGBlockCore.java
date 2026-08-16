package github.com.gengyoubo.MPG.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import github.com.gengyoubo.MPG.block.MPBrewingStandBlock;
import github.com.gengyoubo.MPG.block.MPFurnaceBlock;
import github.com.gengyoubo.MPG.block.MPCraftingBlock;
import github.com.gengyoubo.MPG.block.MPHookBlock;
import github.com.gengyoubo.MPG.block.item.MPBrewingBlockItem;
import github.com.gengyoubo.MPG.block.item.MPFurnaceBlockItem;
import github.com.gengyoubo.MPG.block.item.MPCraftingBlockItem;
import github.com.gengyoubo.MPG.block.item.MPHookBlockItem;

import static github.com.gengyoubo.MPG.MPG.BLOCKS;
import static github.com.gengyoubo.MPG.MPG.ITEMS;

public class MPGBlockCore {
    public static final DeferredBlock<? extends Block> CraftingBlock = BLOCKS.register("block_crafting_manaita", MPCraftingBlock::new);
    public static final DeferredItem<? extends Item> CraftingBlockItem = ITEMS.register("block_crafting_manaita", MPCraftingBlockItem::new);

    public static final DeferredBlock<? extends Block> FurnaceBlock = BLOCKS.register("block_furnace_manaita", MPFurnaceBlock::new);
    public static final DeferredItem<? extends Item> FurnaceBlockItem = ITEMS.register("block_furnace_manaita", MPFurnaceBlockItem::new);

    public static final DeferredBlock<? extends Block> BrewingBlock = BLOCKS.register("block_brewing_manaita", MPBrewingStandBlock::new);
    public static final DeferredItem<? extends Item> BrewingBlockItem = ITEMS.register("block_brewing_manaita", MPBrewingBlockItem::new);

    public static final DeferredBlock<? extends Block> HookBlock = BLOCKS.register("block_hook_manaita", MPHookBlock::new);
    public static final DeferredItem<? extends Item> HookBlockItem = ITEMS.register("block_hook_manaita", MPHookBlockItem::new);

    public static void init() {
    }

}

