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

public class MPShovelItem extends MPTaggedToolItem {
    public MPShovelItem() {
        super(BlockTags.MINEABLE_WITH_SHOVEL);
    }
    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (MPToolActionHelper.applyShovelAction(context, blockPos, blockState)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}

