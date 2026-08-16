package github.com.gengyoubo.MPG.block.item;

import github.com.gengyoubo.MPG.block.MPBrewingStandBlock;

import static github.com.gengyoubo.MPG.core.MPGBlockCore.BrewingBlock;

public class MPBrewingBlockItem extends MPTypedBlockItem {
    public MPBrewingBlockItem() {
        super(BrewingBlock.get(), new Properties().fireResistant(), "block.brewing.", MPBrewingStandBlock.class);
    }
}
