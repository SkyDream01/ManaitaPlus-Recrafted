package github.com.gengyoubo.item.armor;

import github.com.gengyoubo.common.item.armor.MPGArmorItemBase;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;

public class MPArmor extends MPGArmorItemBase {
    private static final Holder<ArmorMaterial> MATERIAL = ArmorMaterials.NETHERITE;

    protected MPArmor(Type type) {
        super(MATERIAL, type);
    }

    public static class Helmet extends MPGArmorItemBase.Helmet {
        public Helmet() {
            super(MATERIAL);
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_1.png";
        }
    }

    public static class Chestplate extends MPGArmorItemBase.Chestplate {
        public Chestplate() {
            super(MATERIAL);
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_1.png";
        }
    }

    public static class Leggings extends MPGArmorItemBase.Leggings {
        public Leggings() {
            super(MATERIAL);
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_2.png";
        }
    }

    public static class Boots extends MPGArmorItemBase.Boots {
        public Boots() {
            super(MATERIAL);
        }

        public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
            return "manaita_plus_general:textures/models/armor/manaita_armor_layer_2.png";
        }
    }
}
