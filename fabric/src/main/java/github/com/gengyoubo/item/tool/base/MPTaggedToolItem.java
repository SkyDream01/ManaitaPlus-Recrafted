package github.com.gengyoubo.item.tool.base;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class MPTaggedToolItem extends MPToolBase {
    private final TagKey<Block> mineableTag;

    protected MPTaggedToolItem(TagKey<Block> mineableTag) {
        super(mineableTag);
        this.mineableTag = mineableTag;
    }

    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        return state.is(mineableTag);
    }

    public boolean accept(BlockState state) {
        return !state.is(mineableTag);
    }
}

