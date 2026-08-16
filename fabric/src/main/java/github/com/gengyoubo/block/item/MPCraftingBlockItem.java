package github.com.gengyoubo.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.block.MPCraftingBlock;
import github.com.gengyoubo.core.MPBlockCore;

public class MPCraftingBlockItem extends MPTypedBlockItem {
    public MPCraftingBlockItem() {
        super(MPBlockCore.CraftingBlock.get(), new Item.Properties().fireResistant(), "block.crafting.", MPCraftingBlock.class);
    }
}


