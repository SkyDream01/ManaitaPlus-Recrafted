package github.com.gengyoubo.block.item;

import github.com.gengyoubo.block.MPHookBlock;
import github.com.gengyoubo.common.block.item.MPGTypedBlockItemBase;
import net.minecraft.world.level.block.Block;

public abstract class MPTypedBlockItem extends MPGTypedBlockItemBase {
    protected MPTypedBlockItem(Block block, Properties properties, String translationPrefix,
                               Class<? extends Block> typedBlockClass) {
        super(block, properties, translationPrefix, typedBlockClass, MPHookBlock.class);
    }
}
