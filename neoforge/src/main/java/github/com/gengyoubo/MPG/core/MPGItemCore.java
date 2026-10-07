package github.com.gengyoubo.MPG.core;

import github.com.gengyoubo.MPG.item.*;
import github.com.gengyoubo.MPG.item.armor.MPGArmor;
import github.com.gengyoubo.MPG.item.portable.*;
import github.com.gengyoubo.MPG.item.ring.*;
import github.com.gengyoubo.MPG.item.tool.*;
import net.minecraft.world.item.Item;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.registries.DeferredItem;

import static github.com.gengyoubo.MPG.MPG.ITEMS;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.*;

public class MPGItemCore {
    private static final boolean CURIOS_LOADED = ModList.get().isLoaded("curios");

    public static final DeferredItem<? extends Item> ManaitaSwordGod = ITEMS.registerItem(GOD_SWORD, MPGGodSwordItem::new);
    public static final DeferredItem<? extends Item> ManaitaSword = ITEMS.registerItem(SWORD, MPGSwordItem::new);
    public static final DeferredItem<? extends Item> ManaitaBow = ITEMS.registerItem(BOW, MPGBowItem::new);
    public static final DeferredItem<? extends Item> ManaitaShield = ITEMS.registerItem(SHIELD, MPGShieldItem::new);
    public static final DeferredItem<? extends Item> ManaitaShovel = ITEMS.registerItem(SHOVEL, MPGShovelItem::new);
    public static final DeferredItem<? extends Item> ManaitaPickaxe = ITEMS.registerItem(PICKAXE, MPGPickaxeItem::new);
    public static final DeferredItem<? extends Item> ManaitaAxe = ITEMS.registerItem(AXE, MPGAxeItem::new);
    public static final DeferredItem<? extends Item> ManaitaPaxel = ITEMS.registerItem(PAXEL, MPGPaxelItem::new);
    public static final DeferredItem<? extends Item> ManaitaHoe = ITEMS.registerItem(HOE, MPGHoeItem::new);
    public static final DeferredItem<? extends Item> ManaitaShears = ITEMS.registerItem(SHEARS, MPGShearsItem::new);
    public static final DeferredItem<? extends Item> ManaitaHook = ITEMS.registerItem(HOOK, MPGHookItem::new);
    public static final DeferredItem<? extends Item> ManaitaHelmet = ITEMS.registerItem(HELMET, MPGArmor.Helmet::new);
    public static final DeferredItem<? extends Item> ManaitaChestplate = ITEMS.registerItem(CHESTPLATE, MPGArmor.Chestplate::new);
    public static final DeferredItem<? extends Item> ManaitaLeggings = ITEMS.registerItem(LEGGINGS, MPGArmor.Leggings::new);
    public static final DeferredItem<? extends Item> ManaitaBoots = ITEMS.registerItem(BOOTS, MPGArmor.Boots::new);
    public static final DeferredItem<? extends Item> ManaitaSource = ITEMS.registerItem(SOURCE, MPGSourceItem::new);
    public static final DeferredItem<? extends Item> ManaitaCraftingPortable = ITEMS.registerItem(CRAFTING_PORTABLE, MPGCraftingPortable::new);
    public static final DeferredItem<? extends Item> ManaitaFurnacePortable = ITEMS.registerItem(FURNACE_PORTABLE, MPGFurnacePortable::new);
    public static final DeferredItem<? extends Item> ManaitaBrewingPortable = ITEMS.registerItem(BREWING_PORTABLE, MPGBrewingPortable::new);
    public static final DeferredItem<? extends Item> ManaitaCraftingRing = CURIOS_LOADED ? ITEMS.registerItem(CRAFTING_RING, MPGCraftingRing::new) : null;
    public static final DeferredItem<? extends Item> ManaitaFurnaceRing = CURIOS_LOADED ? ITEMS.registerItem(FURNACE_RING, MPGFurnaceRing::new) : null;
    public static final DeferredItem<? extends Item> ManaitaBrewingRing = CURIOS_LOADED ? ITEMS.registerItem(BREWING_RING, MPGBrewingRing::new) : null;

    public static boolean isCuriosLoaded() {
        return CURIOS_LOADED;
    }

    public static void init() {
    }
}
