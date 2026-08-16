package github.com.gengyoubo.block.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.block.MPFurnaceBlock;

import static github.com.gengyoubo.core.MPBlockCore.FurnaceBlock;

public class MPFurnaceBlockItem extends MPTypedBlockItem {
    public MPFurnaceBlockItem() {
        super(FurnaceBlock.get(), new Item.Properties().fireResistant(), "block.furnace.", MPFurnaceBlock.class);
    }
}


