package github.com.gengyoubo.item.tool.base;

import github.com.gengyoubo.common.item.tool.MPGTaggedToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import github.com.gengyoubo.item.tier.MPToolTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public abstract class MPTaggedToolItem extends MPGTaggedToolItemBase {
    protected MPTaggedToolItem(TagKey<Block> mineableTag) {
        super(new MPToolTier(), mineableTag);
    }

    protected MPTaggedToolItem(TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(new MPToolTier(), mineableTag, profile);
    }
}
