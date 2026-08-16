package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyTaggedToolItem;
import org.jetbrains.annotations.NotNull;

public class MPGPickaxeItem extends ManaitaPlusLegacyTaggedToolItem {
    public MPGPickaxeItem() {
        super(BlockTags.MINEABLE_WITH_PICKAXE);
    }

    @Override
    public boolean canPerformAction(@NotNull ItemStack stack, net.neoforged.neoforge.common.@NotNull ItemAbility toolAction) {
        return net.neoforged.neoforge.common.ItemAbilities.DEFAULT_PICKAXE_ACTIONS.contains(toolAction);
    }

}

