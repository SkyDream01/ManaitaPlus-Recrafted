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

    public static final DeferredItem<? extends Item> ManaitaSwordGod = ITEMS.register(GOD_SWORD, MPGGodSwordItem::new);
    public static final DeferredItem<? extends Item> ManaitaSword = ITEMS.register(SWORD, MPGSwordItem::new);
    public static final DeferredItem<? extends Item> ManaitaBow = ITEMS.register(BOW, MPGBowItem::new);
    public static final DeferredItem<? extends Item> ManaitaShovel = ITEMS.register(SHOVEL, MPGShovelItem::new);
    public static final DeferredItem<? extends Item> ManaitaPickaxe = ITEMS.register(PICKAXE, MPGPickaxeItem::new);
    public static final DeferredItem<? extends Item> ManaitaAxe = ITEMS.register(AXE, MPGAxeItem::new);
    public static final DeferredItem<? extends Item> ManaitaPaxel = ITEMS.register(PAXEL, MPGPaxelItem::new);
    public static final DeferredItem<? extends Item> ManaitaHoe = ITEMS.register(HOE, MPGHoeItem::new);
    public static final DeferredItem<? extends Item> ManaitaShears = ITEMS.register(SHEARS, MPGShearsItem::new);
    public static final DeferredItem<? extends Item> ManaitaHook = ITEMS.register(HOOK, MPGHookItem::new);
    public static final DeferredItem<? extends Item> ManaitaHelmet = ITEMS.register(HELMET, MPGArmor.Helmet::new);
    public static final DeferredItem<? extends Item> ManaitaChestplate = ITEMS.register(CHESTPLATE, MPGArmor.Chestplate::new);
    public static final DeferredItem<? extends Item> ManaitaLeggings = ITEMS.register(LEGGINGS, MPGArmor.Leggings::new);
    public static final DeferredItem<? extends Item> ManaitaBoots = ITEMS.register(BOOTS, MPGArmor.Boots::new);
    public static final DeferredItem<? extends Item> ManaitaSource = ITEMS.register(SOURCE, MPGSourceItem::new);
    public static final DeferredItem<? extends Item> ManaitaCraftingPortable = ITEMS.register(CRAFTING_PORTABLE, MPGCraftingPortable::new);
    public static final DeferredItem<? extends Item> ManaitaFurnacePortable = ITEMS.register(FURNACE_PORTABLE, MPGFurnacePortable::new);
    public static final DeferredItem<? extends Item> ManaitaBrewingPortable = ITEMS.register(BREWING_PORTABLE, MPGBrewingPortable::new);
    public static final DeferredItem<? extends Item> ManaitaCraftingRing = CURIOS_LOADED ? ITEMS.register(CRAFTING_RING, MPGCraftingRing::new) : null;
    public static final DeferredItem<? extends Item> ManaitaFurnaceRing = CURIOS_LOADED ? ITEMS.register(FURNACE_RING, MPGFurnaceRing::new) : null;
    public static final DeferredItem<? extends Item> ManaitaBrewingRing = CURIOS_LOADED ? ITEMS.register(BREWING_RING, MPGBrewingRing::new) : null;

    public static boolean isCuriosLoaded() {
        return CURIOS_LOADED;
    }

    public static void init() {
    }
}
