package github.com.gengyoubo.common.item;

import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

/** Loader-neutral bow data, text and key handling. */
public abstract class MPGBowItemBase extends Item implements IMPGKey, IMPGDoubling {
    protected MPGBowItemBase(Item.Properties props, int durability) {
        // UNBREAKABLE keeps the legacy negative-durability "never breaks" behaviour (its
        // vanilla tooltip line is hidden to keep the previous tooltip).
        super(props.durability(durability).fireResistant()
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        String state = isDoubling(stack) ? translate("info.on") : translate("info.off");
        tooltip.accept(Component.literal(MPText.manaita_mode.formatting(translate("mode.doubling") + ":" + state)));
        tooltip.accept(Component.literal(MPText.manaita_infinity.formatting(translate("info.attack"))));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(MPText.manaita_mode.formatting(translate("item.manaita_bow.name")));
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        String message = String.format("[%s%s] %s%s: %s",
                MPText.manaita_mode.formatting(translate("item.manaita_bow.name")),
                ChatFormatting.RESET, ChatFormatting.RESET, translate("mode.doubling"),
                doubling ? translate("info.on") : translate("info.off"));
        Component messageComponent = Component.literal(message);
        if (keyMessageUsesOverlay()) {
            player.sendOverlayMessage(messageComponent);
        } else {
            player.sendSystemMessage(messageComponent);
        }
    }

    protected boolean keyMessageUsesOverlay() {
        return false;
    }

    protected static String translate(String key) {
        return Component.translatable(key).getString();
    }
}
