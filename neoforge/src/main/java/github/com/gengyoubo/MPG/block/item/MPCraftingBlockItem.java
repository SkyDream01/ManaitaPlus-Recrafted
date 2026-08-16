package github.com.gengyoubo.MPG.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.block.MPCraftingBlock;
import github.com.gengyoubo.MPG.core.MPGBlockCore;

public class MPCraftingBlockItem extends MPTypedBlockItem {
    public MPCraftingBlockItem() {
        super(MPGBlockCore.CraftingBlock.get(), new Item.Properties().fireResistant(), "block.crafting.", MPCraftingBlock.class);
    }
}
