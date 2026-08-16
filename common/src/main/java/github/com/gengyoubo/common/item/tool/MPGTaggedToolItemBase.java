package github.com.gengyoubo.common.item.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class MPGTaggedToolItemBase extends MPGToolItemBase {
    private final TagKey<Block> mineableTag;

    protected MPGTaggedToolItemBase(Tier tier, TagKey<Block> mineableTag) {
        super(tier, mineableTag);
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
