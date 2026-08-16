package github.com.gengyoubo.MPG.item.tier;

import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.common.item.tier.MPGToolTierBase;
import net.minecraft.world.item.crafting.Ingredient;

public class MPGToolTier extends MPGToolTierBase {
    public MPGToolTier() {
        super(() -> Ingredient.of(
                MPGBlockCore.CraftingBlockItem.get(),
                MPGBlockCore.FurnaceBlockItem.get(),
                MPGBlockCore.BrewingBlockItem.get()));
    }
}
