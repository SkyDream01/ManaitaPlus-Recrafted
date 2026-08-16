package github.com.gengyoubo.MPG.core;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.MPG.menu.MPGBaublesMenu;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import static github.com.gengyoubo.MPG.MPG.MENU_TYPES;

public class MPGMenuCore {
    public static final RegistryObject<MenuType<MPGCraftingMenu>> CraftingManaita = MENU_TYPES.register("manaita_crafting", () -> IForgeMenuType.create(MPGCraftingMenu::new));
    public static final RegistryObject<MenuType<MPGFurnaceMenu>> FurnaceManaita = MENU_TYPES.register("manaita_furnace", () -> IForgeMenuType.create(MPGFurnaceMenu::new));
    public static final RegistryObject<MenuType<MPGBrewingStandMenu>> BrewingStandManaita = MENU_TYPES.register("manaita_brewing", () -> IForgeMenuType.create(MPGBrewingStandMenu::new));
    public static final RegistryObject<MenuType<MPGBaublesMenu>> Baubles = MENU_TYPES.register("manaita_baubles", () -> IForgeMenuType.create(MPGBaublesMenu::new));

    public static void init() {
    }

}
