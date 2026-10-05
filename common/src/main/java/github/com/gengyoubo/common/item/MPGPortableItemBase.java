package github.com.gengyoubo.common.item;

import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Shared portable-item type naming and use flow. */
public abstract class MPGPortableItemBase extends Item {
    private final String translationPrefix;

    protected MPGPortableItemBase(Item.Properties props, String translationPrefix, int durability) {
        // UNBREAKABLE keeps the legacy negative-durability "never breaks" behaviour (its
        // vanilla tooltip line is hidden to keep the previous tooltip).
        super(props.durability(durability).fireResistant().stacksTo(1)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));
        this.translationPrefix = translationPrefix;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = MPGItemStackData.getInt(stack, MPGNBTData.ItemType);
        return Component.translatable(translationPrefix + type + ".name");
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, @NotNull Player player,
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
