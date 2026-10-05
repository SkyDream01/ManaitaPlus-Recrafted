package github.com.gengyoubo.MPG.item.portable;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.common.item.MPGPortableItemBase;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public abstract class MPGPortableItem extends MPGPortableItemBase {
    protected MPGPortableItem(Item.Properties props, String translationPrefix) {
        super(props, translationPrefix, -1);
    }

    protected final void openPortableScreen(ServerPlayer serverPlayer, ItemStack itemInHand, Level level,
                                            String titleKey, PortableMenuFactory menuFactory) {
        serverPlayer.openMenu(new MenuProvider() {
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
