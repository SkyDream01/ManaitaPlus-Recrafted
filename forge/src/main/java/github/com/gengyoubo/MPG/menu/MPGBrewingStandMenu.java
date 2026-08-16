package github.com.gengyoubo.MPG.menu;

import github.com.gengyoubo.MPG.core.MPGMenuCore;
import github.com.gengyoubo.common.menu.MPGBrewingStandMenuBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;

public class MPGBrewingStandMenu extends MPGBrewingStandMenuBase {
    @SuppressWarnings("unused")
    public MPGBrewingStandMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, new SimpleContainer(5), new SimpleContainerData(2));
    }

    public MPGBrewingStandMenu(int id, Inventory inventory, Container container, ContainerData data) {
        this(id, inventory, container, data, inventory.player.level().potionBrewing());
    }

    private MPGBrewingStandMenu(int id, Inventory inventory, Container container, ContainerData data,
                                PotionBrewing potionBrewing) {
        super(MPGMenuCore.BrewingStandManaita.get(), id, inventory, container, data,
                potionBrewing::isIngredient,
                stack -> potionBrewing.isContainerIngredient(stack) || stack.is(Items.GLASS_BOTTLE),
                net.minecraftforge.event.ForgeEventFactory::onPlayerBrewedPotion);
    }
}
