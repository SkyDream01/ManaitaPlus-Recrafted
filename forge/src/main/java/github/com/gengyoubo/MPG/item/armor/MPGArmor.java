package github.com.gengyoubo.MPG.item.armor;

import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;

public class MPGArmor extends MPGArmorItemBase {
    protected MPGArmor(Type type) {
        super(MANAITA_ARMOR_MATERIAL, type);
    }

    public static class Helmet extends MPGArmorItemBase.Helmet {
        public Helmet() {
            super(MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_1.png";
        }

    }

    public static class Chestplate extends MPGArmorItemBase.Chestplate {
        public Chestplate() {
            super(MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_1.png";
        }
    }

    public static class Leggings extends MPGArmorItemBase.Leggings {
        public Leggings() {
            super(MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_2.png";
        }

    }

    public static class Boots extends MPGArmorItemBase.Boots {
        public Boots() {
            super(MANAITA_ARMOR_MATERIAL);
        }

        public String getArmorTexture() {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_2.png";
        }

        @Override
        protected boolean messageUsesOverlay() {
            return false;
        }
    }
}
