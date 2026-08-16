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
import static github.com.gengyoubo.common.registry.MPGRegistryIds.*;

public class MPGBlockCore {
    public static final DeferredBlock<? extends Block> CraftingBlock = BLOCKS.register(CRAFTING_BLOCK, MPCraftingBlock::new);
    public static final DeferredItem<? extends Item> CraftingBlockItem = ITEMS.register(CRAFTING_BLOCK, MPCraftingBlockItem::new);

    public static final DeferredBlock<? extends Block> FurnaceBlock = BLOCKS.register(FURNACE_BLOCK, MPFurnaceBlock::new);
    public static final DeferredItem<? extends Item> FurnaceBlockItem = ITEMS.register(FURNACE_BLOCK, MPFurnaceBlockItem::new);

    public static final DeferredBlock<? extends Block> BrewingBlock = BLOCKS.register(BREWING_BLOCK, MPBrewingStandBlock::new);
    public static final DeferredItem<? extends Item> BrewingBlockItem = ITEMS.register(BREWING_BLOCK, MPBrewingBlockItem::new);

    public static final DeferredBlock<? extends Block> HookBlock = BLOCKS.register(HOOK_BLOCK, MPHookBlock::new);
    public static final DeferredItem<? extends Item> HookBlockItem = ITEMS.register(HOOK_BLOCK, MPHookBlockItem::new);

    public static void init() {
    }

}
