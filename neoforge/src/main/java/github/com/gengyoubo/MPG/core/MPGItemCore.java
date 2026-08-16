package github.com.gengyoubo.MPG.core;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import github.com.gengyoubo.MPG.item.*;
import github.com.gengyoubo.MPG.item.armor.MPGArmor;
import github.com.gengyoubo.MPG.item.portable.MPGBrewingPortable;
import github.com.gengyoubo.MPG.item.portable.MPGCraftingPortable;
import github.com.gengyoubo.MPG.item.portable.MPGFurnacePortable;
import github.com.gengyoubo.MPG.item.ring.MPGBrewingRing;
import github.com.gengyoubo.MPG.item.ring.MPGCraftingRing;
import github.com.gengyoubo.MPG.item.ring.MPGFurnaceRing;
import github.com.gengyoubo.MPG.item.tool.*;
import net.neoforged.fml.ModList;

import static github.com.gengyoubo.MPG.MPG.ITEMS;

public class MPGItemCore {
    private static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

    public static final DeferredItem<? extends Item> ManaitaSwordGod = ITEMS.register("manaita_sword_god", MPGGodSwordItem::new);
    public static final DeferredItem<? extends Item> ManaitaSword = ITEMS.register("manaita_sword", MPGSwordItem::new);
    public static final DeferredItem<? extends Item> ManaitaBow = ITEMS.register("manaita_bow", MPGBowItem::new);
    public static final DeferredItem<? extends Item> ManaitaShovel = ITEMS.register("manaita_shovel", MPGShovelItem::new);
    public static final DeferredItem<? extends Item> ManaitaPickaxe = ITEMS.register("manaita_pickaxe", MPGPickaxeItem::new);
    public static final DeferredItem<? extends Item> ManaitaAxe = ITEMS.register("manaita_axe", MPGAxeItem::new);
    public static final DeferredItem<? extends Item> ManaitaPaxel = ITEMS.register("manaita_paxel", MPGPaxelItem::new);
    public static final DeferredItem<? extends Item> ManaitaHoe = ITEMS.register("manaita_hoe", MPGHoeItem::new);
    public static final DeferredItem<? extends Item> ManaitaShears = ITEMS.register("manaita_shears", MPGShearsItem::new);
    public static final DeferredItem<? extends Item> ManaitaHook = ITEMS.register("manaita_hook", MPGHookItem::new);
    public static final DeferredItem<? extends Item> ManaitaHelmet = ITEMS.register("manaita_helmet", MPGArmor.Helmet::new);
    public static final DeferredItem<? extends Item> ManaitaChestplate = ITEMS.register("manaita_chestplate", MPGArmor.Chestplate::new);
    public static final DeferredItem<? extends Item> ManaitaLeggings = ITEMS.register("manaita_leggings", MPGArmor.Leggings::new);
    public static final DeferredItem<? extends Item> ManaitaBoots = ITEMS.register("manaita_boots", MPGArmor.Boots::new);
    public static final DeferredItem<? extends Item> ManaitaSource = ITEMS.register("manaita_source", MPGSourceItem::new);
    public static final DeferredItem<? extends Item> ManaitaCraftingPortable = ITEMS.register("manaita_crafting_portable", MPGCraftingPortable::new);
    public static final DeferredItem<? extends Item> ManaitaFurnacePortable = ITEMS.register("manaita_furnace_portable", MPGFurnacePortable::new);
    public static final DeferredItem<? extends Item> ManaitaBrewingPortable = ITEMS.register("manaita_brewing_portable", MPGBrewingPortable::new);
    public static final DeferredItem<? extends Item> ManaitaCraftingRing = CURIOS_LOADED ? ITEMS.register("manaita_crafting_ring", MPGCraftingRing::new) : null;
    public static final DeferredItem<? extends Item> ManaitaFurnaceRing = CURIOS_LOADED ? ITEMS.register("manaita_furnace_ring", MPGFurnaceRing::new) : null;
    public static final DeferredItem<? extends Item> ManaitaBrewingRing = CURIOS_LOADED ? ITEMS.register("manaita_brewing_ring", MPGBrewingRing::new) : null;

    public static boolean isCuriosLoaded() {
        return CURIOS_LOADED;
    }

    public static void init() {
    }

}


