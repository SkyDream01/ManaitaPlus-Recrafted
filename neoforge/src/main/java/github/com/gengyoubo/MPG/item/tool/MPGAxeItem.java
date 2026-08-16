package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;

public class MPGAxeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGAxeItem() {
        super(BlockTags.MINEABLE_WITH_AXE);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = ManaitaPlusLegacyToolActionHelper.applyInRange(context, range, (pos, state) -> ManaitaPlusLegacyToolActionHelper.applyAxeActions(context, pos, state));
        return changed ? InteractionResult.sidedSuccess(context.getLevel().isClientSide) : InteractionResult.PASS;
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_AXE_ACTIONS.contains(toolAction);
    }
}

