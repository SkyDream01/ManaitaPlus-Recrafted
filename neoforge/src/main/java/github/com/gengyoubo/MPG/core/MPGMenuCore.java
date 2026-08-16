package github.com.gengyoubo.MPG.core;

import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import github.com.gengyoubo.MPG.menu.MPGBrewingStandMenu;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.MPG.menu.MPGFurnaceMenu;
import static github.com.gengyoubo.MPG.MPG.MENU_TYPES;
import static github.com.gengyoubo.common.registry.MPGRegistryIds.*;

public class MPGMenuCore {
    public static final DeferredHolder<MenuType<?>, MenuType<MPGCraftingMenu>> CraftingManaita = MENU_TYPES.register(CRAFTING_MENU, () -> IMenuTypeExtension.create(MPGCraftingMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MPGFurnaceMenu>> FurnaceManaita = MENU_TYPES.register(FURNACE_MENU, () -> IMenuTypeExtension.create(MPGFurnaceMenu::new));
    public static final DeferredHolder<MenuType<?>, MenuType<MPGBrewingStandMenu>> BrewingStandManaita = MENU_TYPES.register(BREWING_MENU, () -> IMenuTypeExtension.create(MPGBrewingStandMenu::new));

    public static void init() {
    }

}
