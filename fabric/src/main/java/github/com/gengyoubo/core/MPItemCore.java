package github.com.gengyoubo.core;

import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.item.*;
import github.com.gengyoubo.item.armor.MPArmor;
import github.com.gengyoubo.item.portable.MPBrewingPortable;
import github.com.gengyoubo.item.portable.MPCraftingPortable;
import github.com.gengyoubo.item.portable.MPFurnacePortable;
import github.com.gengyoubo.item.tool.MPAxeItem;
import github.com.gengyoubo.item.tool.MPHoeItem;
import github.com.gengyoubo.item.tool.MPPaxelItem;
import github.com.gengyoubo.item.tool.MPPickaxeItem;
import github.com.gengyoubo.item.tool.MPShearsItem;
import github.com.gengyoubo.item.tool.MPShovelItem;

import static github.com.gengyoubo.MPG.ITEMS;

public class MPItemCore {
    public static final RegistryObject<Item> ManaitaSword = ITEMS.register("manaita_sword", MPSwordItem::new);
    public static final RegistryObject<Item> ManaitaSwordGod = ITEMS.register("manaita_sword_god", MPGodSwordItem::new);

    public static final RegistryObject<Item> ManaitaBow = ITEMS.register("manaita_bow", MPBowItem::new);

    public static final RegistryObject<Item> ManaitaAxe = ITEMS.register("manaita_axe", MPAxeItem::new);
    public static final RegistryObject<Item> ManaitaHoe = ITEMS.register("manaita_hoe", MPHoeItem::new);
    public static final RegistryObject<Item> ManaitaPaxel = ITEMS.register("manaita_paxel", MPPaxelItem::new);
    public static final RegistryObject<Item> ManaitaPickaxe = ITEMS.register("manaita_pickaxe", MPPickaxeItem::new);
    public static final RegistryObject<Item> ManaitaShears = ITEMS.register("manaita_shears", MPShearsItem::new);
    public static final RegistryObject<Item> ManaitaShovel = ITEMS.register("manaita_shovel", MPShovelItem::new);
    public static final RegistryObject<Item> ManaitaHelmet = ITEMS.register("manaita_helmet", MPArmor.Helmet::new);
    public static final RegistryObject<Item> ManaitaChestplate = ITEMS.register("manaita_chestplate", MPArmor.Chestplate::new);
    public static final RegistryObject<Item> ManaitaLeggings = ITEMS.register("manaita_leggings", MPArmor.Leggings::new);
    public static final RegistryObject<Item> ManaitaBoots = ITEMS.register("manaita_boots", MPArmor.Boots::new);

    public static final RegistryObject<Item> ManaitaSource = ITEMS.register("manaita_source", MPSourceItem::new);
    public static final RegistryObject<Item> ManaitaHook = ITEMS.register("manaita_hook", MPHookItem::new);

    public static final RegistryObject<Item> ManaitaCraftingPortable = ITEMS.register("manaita_crafting_portable", MPCraftingPortable::new);
    public static final RegistryObject<Item> ManaitaFurnacePortable = ITEMS.register("manaita_furnace_portable", MPFurnacePortable::new);
    public static final RegistryObject<Item> ManaitaBrewingPortable = ITEMS.register("manaita_brewing_portable", MPBrewingPortable::new);

    public static void init() {
    }

}

