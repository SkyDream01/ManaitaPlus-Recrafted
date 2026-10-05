package github.com.gengyoubo.MPG.item;

import net.minecraft.world.item.Item;
import github.com.gengyoubo.MPG.menu.MPGCraftingMenu;
import github.com.gengyoubo.common.item.MPGSourceItemBase;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.Level;

public class MPGSourceItem extends MPGSourceItemBase {
    public MPGSourceItem(Item.Properties props) {
        super(props);
    }

    @Override
    protected void openCraftingMenu(ServerPlayer player, Level level) {
        player.openMenu(new SimpleMenuProvider(
                (windowId, inventory, menuPlayer) -> new MPGCraftingMenu(windowId, inventory, level),
                Component.translatable("container.crafting")));
    }
}
