package github.com.gengyoubo.MPG.menu;

import github.com.gengyoubo.MPG.core.MPGMenuCore;
import github.com.gengyoubo.common.menu.MPGBrewingStandMenuBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipePropertySet;

public class MPGBrewingStandMenu extends MPGBrewingStandMenuBase {
    @SuppressWarnings("unused")
    public MPGBrewingStandMenu(int id, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(id, inventory, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public MPGBrewingStandMenu(int id, Inventory inventory, Container container, ContainerData data) {
        // PotionBrewing is gone in 26.3; the brewing ingredient/input rules now come from
        // the recipe property sets, resolved lazily so both sides can use the menu.
        super(MPGMenuCore.BrewingStandManaita.get(), id, inventory, container, data,
                stack -> inventory.player.level().recipeAccess()
                        .propertySet(RecipePropertySet.BREWING_REAGENTS).test(stack),
                stack -> inventory.player.level().recipeAccess()
                        .propertySet(RecipePropertySet.BREWING_INPUTS).test(stack) || stack.is(Items.GLASS_BOTTLE),
                net.neoforged.neoforge.event.EventHooks::onPlayerBrewedPotion);
    }
}
