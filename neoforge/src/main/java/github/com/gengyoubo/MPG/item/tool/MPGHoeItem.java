package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

public class MPGHoeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGHoeItem(Item.Properties props) {
        super(props, BlockTags.MINEABLE_WITH_HOE, MPGToolProfile.HOE);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = ManaitaPlusLegacyToolActionHelper.applyInRange(context, range, (pos, state) -> ManaitaPlusLegacyToolActionHelper.applyHoeTillAction(context, pos, state));
        // InteractionResult.sidedSuccess is gone in 26.3.
        return changed
                ? (context.getLevel().isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER)
                : InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemInstance stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        // DEFAULT_HOE_ACTIONS is gone in 26.3 (tilling is data-driven now) and no stock
        // ItemAbility is left for it to match, so every queried ability is rejected just like
        // the old set lookup did.
        return false;
    }
}
