package github.com.gengyoubo.item.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.item.tool.base.MPTaggedToolItem;
import github.com.gengyoubo.item.tool.base.MPToolActionHelper;

public class MPHoeItem extends MPTaggedToolItem {
    public MPHoeItem() {
        super(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = MPToolActionHelper.applyInRange(context, range, (pos, state) -> MPToolActionHelper.applyHoeTillAction(context, pos, state));
        return changed ? InteractionResult.sidedSuccess(context.getLevel().isClientSide) : InteractionResult.PASS;
    }
}

