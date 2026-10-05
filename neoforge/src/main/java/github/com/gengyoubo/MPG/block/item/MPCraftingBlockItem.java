package github.com.gengyoubo.MPG.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.block.MPCraftingBlock;
import github.com.gengyoubo.MPG.core.MPGBlockCore;

public class MPCraftingBlockItem extends MPTypedBlockItem {
    public MPCraftingBlockItem(Item.Properties props) {
        super(MPGBlockCore.CraftingBlock.get(), props.fireResistant(), "block.crafting.", MPCraftingBlock.class);
    }
}
