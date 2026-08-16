package github.com.gengyoubo.item.tier;

import github.com.gengyoubo.common.item.tier.MPGToolTierBase;
import github.com.gengyoubo.core.MPBlockCore;
import net.minecraft.world.item.crafting.Ingredient;

public class MPToolTier extends MPGToolTierBase {
    public MPToolTier() {
        super(() -> Ingredient.of(
                MPBlockCore.CraftingBlockItem.get(),
                MPBlockCore.FurnaceBlockItem.get(),
                MPBlockCore.BrewingBlockItem.get()));
    }
}
