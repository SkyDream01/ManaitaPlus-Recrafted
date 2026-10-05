package github.com.gengyoubo.common.item.tier;

import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.block.Block;

import java.util.function.Supplier;

/** Supplies the Manaita {@link ToolMaterial} replacing the deleted Tier interface. */
public class MPGToolTierBase {
    private final ToolMaterial material;

    public MPGToolTierBase(Supplier<TagKey<Item>> repairItems) {
        // The old Tier values map onto the record unchanged: uses = -1 keeps the items
        // from being damageable (the item bases add UNBREAKABLE for that), and the
        // infinite speed/damage bonuses keep the Manaita tools instant and lethal.
        // Tier#getRepairIngredient() (Ingredient) is now the record's repair TagKey<Item>,
        // so loaders pass a repair tag instead of an Ingredient.
        // enchantmentValue: the old Tier#getEnchantmentValue() was 0 (no enchanting
        // bonus). Enchantable now rejects non-positive values, so 1 is used instead; the
        // ENCHANTABLE component still exists, so isEnchantable() stays true as before.
        this.material = new ToolMaterial(BlockTags.INCORRECT_FOR_NETHERITE_TOOL, -1,
                Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, 1, repairItems.get());
    }

    public ToolMaterial material() {
        return this.material;
    }
}
