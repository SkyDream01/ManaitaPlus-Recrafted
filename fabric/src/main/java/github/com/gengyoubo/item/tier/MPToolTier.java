package github.com.gengyoubo.item.tier;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.core.MPBlockCore;

public class MPToolTier implements Tier {
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
    public @NotNull Ingredient getRepairIngredient() {
        return Ingredient.of(MPBlockCore.CraftingBlockItem.get(), MPBlockCore.FurnaceBlockItem.get(), MPBlockCore.BrewingBlockItem.get());
    }

    @Override
    public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
        return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
    }
}

