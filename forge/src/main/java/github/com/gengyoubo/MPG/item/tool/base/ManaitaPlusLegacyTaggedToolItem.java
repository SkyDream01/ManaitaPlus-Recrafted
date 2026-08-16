package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class ManaitaPlusLegacyTaggedToolItem extends ManaitaPlusLegacyToolBase {
    private final TagKey<Block> mineableTag;

    protected ManaitaPlusLegacyTaggedToolItem(TagKey<Block> mineableTag) {
        super(mineableTag);
        this.mineableTag = mineableTag;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        return state.is(mineableTag);
    }

    @Override
    public boolean accept(BlockState state) {
        return !state.is(mineableTag);
    }
}
