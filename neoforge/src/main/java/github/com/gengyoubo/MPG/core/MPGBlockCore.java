package github.com.gengyoubo.MPG.core;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
    public static final DeferredBlock<? extends Block> CraftingBlock = BLOCKS.registerBlock(CRAFTING_BLOCK, MPCraftingBlock::new, BlockBehaviour.Properties::noOcclusion);
    public static final DeferredItem<? extends Item> CraftingBlockItem = ITEMS.registerItem(CRAFTING_BLOCK, MPCraftingBlockItem::new);

    public static final DeferredBlock<? extends Block> FurnaceBlock = BLOCKS.registerBlock(FURNACE_BLOCK, MPFurnaceBlock::new, BlockBehaviour.Properties::noOcclusion);
    public static final DeferredItem<? extends Item> FurnaceBlockItem = ITEMS.registerItem(FURNACE_BLOCK, MPFurnaceBlockItem::new);

    public static final DeferredBlock<? extends Block> BrewingBlock = BLOCKS.registerBlock(BREWING_BLOCK, MPBrewingStandBlock::new, BlockBehaviour.Properties::noOcclusion);
    public static final DeferredItem<? extends Item> BrewingBlockItem = ITEMS.registerItem(BREWING_BLOCK, MPBrewingBlockItem::new);

    public static final DeferredBlock<? extends Block> HookBlock = BLOCKS.registerBlock(HOOK_BLOCK, MPHookBlock::new, BlockBehaviour.Properties::noOcclusion);
    public static final DeferredItem<? extends Item> HookBlockItem = ITEMS.registerItem(HOOK_BLOCK, MPHookBlockItem::new);

    public static void init() {
    }

}
