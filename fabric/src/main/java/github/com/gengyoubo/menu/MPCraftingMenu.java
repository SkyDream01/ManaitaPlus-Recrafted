package github.com.gengyoubo.menu;

import github.com.gengyoubo.common.menu.MPGCraftingMenuBase;
import github.com.gengyoubo.core.MPBlockCore;
import github.com.gengyoubo.core.MPMenuCore;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;

public class MPCraftingMenu extends MPGCraftingMenuBase {
    @SuppressWarnings("unused")
    public MPCraftingMenu(int containerId, Inventory inventory, BlockPos blockPos) {
        this(containerId, inventory, ContainerLevelAccess.create(inventory.player.level(), blockPos), false);
    }

    public MPCraftingMenu(int containerId, Inventory inventory, Level level) {
        this(containerId, inventory, ContainerLevelAccess.create(level, BlockPos.ZERO), true);
    }

    public MPCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access) {
        this(containerId, inventory, access, false);
    }

    private MPCraftingMenu(int containerId, Inventory inventory, ContainerLevelAccess access, boolean alwaysValid) {
        super(MPMenuCore.CraftingManaita.get(), containerId, inventory, access,
                MPBlockCore.CraftingBlock.get(), alwaysValid);
    }
}
