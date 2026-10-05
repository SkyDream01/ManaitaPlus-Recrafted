package github.com.gengyoubo.MPG.item.tier;

import github.com.gengyoubo.common.item.tier.MPGToolTierBase;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class MPGToolTier extends MPGToolTierBase {
    public MPGToolTier() {
        // The old repair Ingredient listed the three Manaita block items directly; ToolMaterial
        // repairs by tag instead, so the items moved into the manaita_tool_repairable item tag.
        super(() -> TagKey.create(Registries.ITEM,
                Identifier.fromNamespaceAndPath("manaita_plus_general", "manaita_tool_repairable")));
    }
}
