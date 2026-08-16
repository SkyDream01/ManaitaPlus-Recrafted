package github.com.gengyoubo.MPG.item.tool.base;

import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.common.item.tool.MPGTaggedToolItemBase;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public abstract class ManaitaPlusLegacyTaggedToolItem extends MPGTaggedToolItemBase {
    protected ManaitaPlusLegacyTaggedToolItem(TagKey<Block> mineableTag) {
        super(new MPGToolTier(), mineableTag);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
