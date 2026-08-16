package github.com.gengyoubo.common.item;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Shared portable-item type naming and use flow. */
public abstract class MPGPortableItemBase extends Item {
    private final String translationPrefix;

    protected MPGPortableItemBase(String translationPrefix, int durability) {
        super(new Properties().durability(durability).fireResistant().stacksTo(1));
        this.translationPrefix = translationPrefix;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = MPGItemStackData.getInt(stack, MPGNBTData.ItemType);
        return Component.translatable(translationPrefix + type + ".name");
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (player instanceof ServerPlayer serverPlayer) {
            openPortableMenu(serverPlayer, itemInHand, level);
        }
        return super.use(level, player, hand);
    }

    protected abstract void openPortableMenu(ServerPlayer serverPlayer, ItemStack itemInHand, Level level);

    @FunctionalInterface
    protected interface PortableMenuFactory {
        AbstractContainerMenu create(int containerId, Inventory inventory, Player player,
                                     ItemStack itemInHand, Level level);
    }
}
