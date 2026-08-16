package github.com.gengyoubo.core;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.RegistryObject;
import github.com.gengyoubo.menu.MPBrewingStandMenu;
import github.com.gengyoubo.menu.MPCraftingMenu;
import github.com.gengyoubo.menu.MPFurnaceMenu;
import static github.com.gengyoubo.MPG.MENU_TYPES;

public class MPMenuCore {
    public static final RegistryObject<MenuType<MPCraftingMenu>> CraftingManaita = cast(MENU_TYPES.register("manaita_crafting", () -> IForgeMenuType.create(MPCraftingMenu::new)));
    public static final RegistryObject<MenuType<MPFurnaceMenu>> FurnaceManaita = cast(MENU_TYPES.register("manaita_furnace", () -> IForgeMenuType.create(MPFurnaceMenu::new)));
    public static final RegistryObject<MenuType<MPBrewingStandMenu>> BrewingStandManaita = cast(MENU_TYPES.register("manaita_brewing", () -> IForgeMenuType.create(MPBrewingStandMenu::new)));

    @SuppressWarnings("unchecked")
    private static <T> RegistryObject<T> cast(RegistryObject<?> value) {
        return (RegistryObject<T>) value;
    }

    public static void init() {
    }

}


