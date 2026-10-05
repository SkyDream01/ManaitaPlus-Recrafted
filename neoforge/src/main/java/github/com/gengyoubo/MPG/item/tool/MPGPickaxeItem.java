package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemInstance;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;
import org.jetbrains.annotations.NotNull;

public class MPGPickaxeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGPickaxeItem(Item.Properties props) {
        super(props, BlockTags.MINEABLE_WITH_PICKAXE, MPGToolProfile.PICKAXE);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemInstance stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        // DEFAULT_PICKAXE_ACTIONS is gone in 26.3 and no stock ItemAbility is left for it to
        // match, so every queried ability is rejected just like the old set lookup did.
        return false;
    }

}
