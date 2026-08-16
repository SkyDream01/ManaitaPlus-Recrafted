package github.com.gengyoubo.MPG.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.block.MPFurnaceBlock;

import static github.com.gengyoubo.MPG.core.MPGBlockCore.FurnaceBlock;

public class MPFurnaceBlockItem extends MPTypedBlockItem {
    public MPFurnaceBlockItem() {
        super(FurnaceBlock.get(), new Item.Properties().fireResistant(), "block.furnace.", MPFurnaceBlock.class);
    }
}
