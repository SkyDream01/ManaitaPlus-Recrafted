package github.com.gengyoubo.MPG.menu;

import github.com.gengyoubo.MPG.core.MPGBlockCore;
import github.com.gengyoubo.MPG.core.MPGMenuCore;
import github.com.gengyoubo.common.menu.MPGCraftingMenuBase;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class MPGCraftingMenu extends MPGCraftingMenuBase {
    @SuppressWarnings("unused")
    public MPGCraftingMenu(int containerId, Inventory inventory, RegistryFriendlyByteBuf extraData) {
        this(containerId, inventory, ContainerLevelAccess.NULL, false);
    }

    public MPGCraftingMenu(int containerId, Inventory inventory, Level level) {
        this(containerId, inventory, ContainerLevelAccess.create(level, BlockPos.ZERO), true);
    }

    public MPGCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        this(containerId, inventory, access, false);
    }

    private MPGCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access, boolean alwaysValid) {
        super(MPGMenuCore.CraftingManaita.get(), containerId, inventory, access,
                MPGBlockCore.CraftingBlock.get(), alwaysValid);
    }
}
