package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.common.item.tool.MPGToolItemBase;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ManaitaPlusLegacyToolBase extends MPGToolItemBase {
    public ManaitaPlusLegacyToolBase(Item.Properties props, TagKey<Block> mineableTag) {
        // MPGToolTier now wraps a ToolMaterial instead of being one.
        super(props, new MPGToolTier().material(), mineableTag);
    }

    public ManaitaPlusLegacyToolBase(Item.Properties props, TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(props, new MPGToolTier().material(), mineableTag, profile);
    }

    @Override
    protected boolean messageUsesOverlay() {
        return false;
    }
}
