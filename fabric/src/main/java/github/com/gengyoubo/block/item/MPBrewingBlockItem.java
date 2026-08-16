package github.com.gengyoubo.block.item;

import github.com.gengyoubo.block.MPBrewingStandBlock;

import static github.com.gengyoubo.core.MPBlockCore.BrewingBlock;

public class MPBrewingBlockItem extends MPTypedBlockItem {
    public MPBrewingBlockItem() {
        super(BrewingBlock.get(), new Properties().fireResistant(), "block.brewing.", MPBrewingStandBlock.class);
    }
}


