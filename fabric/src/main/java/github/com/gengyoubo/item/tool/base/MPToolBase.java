package github.com.gengyoubo.item.tool.base;

import github.com.gengyoubo.common.item.tool.MPGToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import github.com.gengyoubo.item.tier.MPToolTier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class MPToolBase extends MPGToolItemBase {
    public MPToolBase(TagKey<Block> mineableTag) {
        super(new MPToolTier(), mineableTag);
    }

    public MPToolBase(TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(new MPToolTier(), mineableTag, profile);
    }
}
