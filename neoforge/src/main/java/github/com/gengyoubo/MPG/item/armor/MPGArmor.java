package github.com.gengyoubo.MPG.item.armor;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.world.item.equipment.ArmorType;

public class MPGArmor extends MPGArmorItemBase {
    // ArmorItem.Type is deleted in 26.3; ArmorType (net.minecraft.world.item.equipment) takes its place.
    protected MPGArmor(Item.Properties props, ArmorType type) {
        super(props, MANAITA_ARMOR_MATERIAL, type);
    }

    public static class Helmet extends MPGArmorItemBase.Helmet {
        public Helmet(Item.Properties props) {
            super(props, MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_recrafted:textures/models/armor/manaita_armor_layer_1.png";
        }

    }

    public static class Chestplate extends MPGArmorItemBase.Chestplate {
        public Chestplate(Item.Properties props) {
            super(props, MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_recrafted:textures/models/armor/manaita_armor_layer_1.png";
        }
    }

    public static class Leggings extends MPGArmorItemBase.Leggings {
        public Leggings(Item.Properties props) {
            super(props, MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_recrafted:textures/models/armor/manaita_armor_layer_2.png";
        }

    }

    public static class Boots extends MPGArmorItemBase.Boots {
        public Boots(Item.Properties props) {
            super(props, MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_recrafted:textures/models/armor/manaita_armor_layer_2.png";
        }

        @Override
        protected boolean messageUsesOverlay() {
            return false;
        }
    }
}
