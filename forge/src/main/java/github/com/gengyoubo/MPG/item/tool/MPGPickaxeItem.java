package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import org.jetbrains.annotations.NotNull;

public class MPGPickaxeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGPickaxeItem() {
        super(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, @NotNull ToolAction toolAction) {
        return ToolActions.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }

}
