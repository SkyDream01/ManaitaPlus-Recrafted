package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;

public class MPGShovelItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGShovelItem() {
        super(BlockTags.MINEABLE_WITH_SHOVEL);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_SHOVEL_ACTIONS.contains(toolAction);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (ManaitaPlusLegacyToolActionHelper.applyShovelAction(context, blockPos, blockState)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return InteractionResult.PASS;
    }
}

