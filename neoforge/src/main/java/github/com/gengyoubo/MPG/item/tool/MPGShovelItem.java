package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

public class MPGShovelItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGShovelItem(Item.Properties props) {
        super(props, BlockTags.MINEABLE_WITH_SHOVEL, MPGToolProfile.SHOVEL);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemInstance stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        // SHOVEL_DIG/SHOVEL_FLATTEN are gone in 26.3 (flattening is data-driven now); SHOVEL_DOUSE
        // survives and is still answered with yes, matching the old DEFAULT_SHOVEL_ACTIONS set.
        return toolAction == net.neoforged.neoforge.common.ItemAbilities.SHOVEL_DOUSE;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = ManaitaPlusLegacyToolActionHelper.applyInRange(context, range,
                (pos, state) -> ManaitaPlusLegacyToolActionHelper.applyShovelAction(context, pos, state));
        if (changed) {
            // InteractionResult.sidedSuccess is gone in 26.3.
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.SUCCESS_SERVER;
        }
        return InteractionResult.PASS;
    }
}
