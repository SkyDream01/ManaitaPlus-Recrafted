package github.com.gengyoubo.MPG.item.tool.base;

import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.common.item.tool.MPGToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ManaitaPlusLegacyToolBase extends MPGToolItemBase {
    public ManaitaPlusLegacyToolBase(TagKey<Block> mineableTag) {
        super(new MPGToolTier(), mineableTag);
    }

    public ManaitaPlusLegacyToolBase(TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(new MPGToolTier(), mineableTag, profile);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
