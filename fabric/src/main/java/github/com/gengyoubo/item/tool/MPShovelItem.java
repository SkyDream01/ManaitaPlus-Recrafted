package github.com.gengyoubo.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.item.tool.base.MPTaggedToolItem;
import github.com.gengyoubo.item.tool.base.MPToolActionHelper;
import github.com.gengyoubo.common.item.tool.MPGToolProfile;

public class MPShovelItem extends MPTaggedToolItem {
    public MPShovelItem() {
        super(BlockTags.MINEABLE_WITH_SHOVEL, MPGToolProfile.SHOVEL);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        int range = getRange(context.getItemInHand()) >> 1;
        boolean changed = MPToolActionHelper.applyInRange(context, range,
                (pos, state) -> MPToolActionHelper.applyShovelAction(context, pos, state));
        if (changed) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}
