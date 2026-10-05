package github.com.gengyoubo.MPG.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.block.MPBrewingStandBlock;

import static github.com.gengyoubo.MPG.core.MPGBlockCore.BrewingBlock;

public class MPBrewingBlockItem extends MPTypedBlockItem {
    public MPBrewingBlockItem(Item.Properties props) {
        super(BrewingBlock.get(), props.fireResistant(), "block.brewing.", MPBrewingStandBlock.class);
    }
}
