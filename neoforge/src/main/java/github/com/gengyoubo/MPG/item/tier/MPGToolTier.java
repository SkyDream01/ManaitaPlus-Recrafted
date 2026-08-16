package github.com.gengyoubo.MPG.item.tier;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.MPG.core.MPGBlockCore;

public class MPGToolTier implements Tier {
    @Override
    public int getUses() {
        return -1;
    }

    @Override
    public float getSpeed() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public float getAttackDamageBonus() {
        return Float.POSITIVE_INFINITY;
    }

    @Override
    public int getEnchantmentValue() {
        return 0;
    }

    @Override
    public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }

    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.of(MPGBlockCore.CraftingBlockItem.get(), MPGBlockCore.FurnaceBlockItem.get(), MPGBlockCore.BrewingBlockItem.get());
    }

}
