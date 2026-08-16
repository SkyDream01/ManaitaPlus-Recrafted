package github.com.gengyoubo.item.portable;

import github.com.gengyoubo.common.item.MPGPortableItemBase;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class MPGPortableItem extends MPGPortableItemBase {
    protected MPGPortableItem(String translationPrefix) {
        super(translationPrefix, Integer.MAX_VALUE);
    }

    protected final void openPortableScreen(ServerPlayer serverPlayer, ItemStack itemInHand, Level level,
                                            String titleKey, PortableMenuFactory menuFactory) {
        serverPlayer.openMenu(new ExtendedScreenHandlerFactory<BlockPos>() {
            @Override
            public BlockPos getScreenOpeningData(ServerPlayer player) {
                return BlockPos.ZERO;
            }

            @Override
            public @NotNull Component getDisplayName() {
                return Component.translatable(titleKey);
            }

            @Override
            public @NotNull AbstractContainerMenu createMenu(int containerId, @NotNull Inventory inventory,
                                                              @NotNull Player player) {
                return menuFactory.create(containerId, inventory, player, itemInHand, level);
            }
        });
    }
}
