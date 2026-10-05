package github.com.gengyoubo.common.item.tool;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public abstract class MPGTaggedToolItemBase extends MPGToolItemBase {
    private final TagKey<Block> mineableTag;

    protected MPGTaggedToolItemBase(Item.Properties props, ToolMaterial material, TagKey<Block> mineableTag) {
        super(props, material, mineableTag);
        this.mineableTag = mineableTag;
    }

    protected MPGTaggedToolItemBase(Item.Properties props, ToolMaterial material, TagKey<Block> mineableTag, MPGToolProfile profile) {
        super(props, material, mineableTag, profile);
        this.mineableTag = mineableTag;
    }

    @Override
    public boolean isCorrectToolForDrops(@NotNull ItemStack stack, BlockState state) {
        return canHarvest(stack) && state.is(mineableTag);
    }

    @Override
    public boolean accept(BlockState state) {
        return !state.is(mineableTag);
    }
}
