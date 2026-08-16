package github.com.gengyoubo.item;

import github.com.gengyoubo.common.item.MPGSourceItemBase;
import github.com.gengyoubo.menu.MPCraftingMenu;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPSourceItem extends MPGSourceItemBase {
    @Override
    protected void openCraftingMenu(ServerPlayer player, Level level) {
        player.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer player) {
                return BlockPos.ZERO;
            }

            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable("container.crafting");
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory,
                                                              @NotNull Player menuPlayer) {
                return new MPCraftingMenu(windowId, inventory, level);
            }
        });
    }
}
