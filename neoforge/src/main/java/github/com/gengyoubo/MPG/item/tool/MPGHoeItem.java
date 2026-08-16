package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;

public class MPGHoeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGHoeItem() {
        super(BlockTags.MINEABLE_WITH_HOE);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = ManaitaPlusLegacyToolActionHelper.applyInRange(context, range, (pos, state) -> ManaitaPlusLegacyToolActionHelper.applyHoeTillAction(context, pos, state));
        return changed ? InteractionResult.sidedSuccess(context.getLevel().isClientSide) : InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_HOE_ACTIONS.contains(toolAction);
    }
}

