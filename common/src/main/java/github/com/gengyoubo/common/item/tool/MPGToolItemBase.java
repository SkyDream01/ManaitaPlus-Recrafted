package github.com.gengyoubo.common.item.tool;

import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MPGToolItemBase extends DiggerItem implements IMPGKey, IMPGDestroy, IMPGDoubling {
    public MPGToolItemBase(Tier tier, TagKey<Block> mineableTag) {
        super(tier, mineableTag, new Item.Properties().fireResistant());
    }

    @Override
    public boolean accept(BlockState state) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int range = getRange(stack);
        String rangeValue = String.valueOf(range);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                text("mode.manaita_tool") + ": " + rangeValue + "x" + rangeValue + "x" + rangeValue)));
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                text("mode.doubling") + ":" + (isDoubling(stack) ? text("info.on") : text("info.off")))));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                setRange(itemInHand, (getRange(itemInHand) + 2) % 21, player);
            } else {
                addToolEnchantments(level, player, itemInHand);
            }
        }
        return InteractionResultHolder.pass(itemInHand);
    }

    private void addToolEnchantments(Level level, Player player, ItemStack itemStack) {
        Holder.Reference<Enchantment> fortune = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);
        Holder.Reference<Enchantment> silkTouch = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);
        boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemStack) > 0;

        EnchantmentHelper.updateEnchantments(itemStack, mutable -> {
            mutable.set(fortune, 10);
            if (!hasSilkTouch) {
                mutable.set(silkTouch, 1);
            }
        });

        String enchantmentName = text(hasSilkTouch ? "enchantments.fortune" : "enchantments.silktouch");
        player.displayClientMessage(Component.literal(MPText.manaita_enchantment.formatting(
                itemStack.getDisplayName().getString() + text("info.enchantment") + ": " + enchantmentName)),
                messageUsesOverlay());
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state,
                             @NotNull BlockPos blockPos, @NotNull LivingEntity livingEntity) {
        return true;
    }

    @Override
    public int getRange(ItemStack itemStack) {
        int range = MPGItemStackData.getInt(itemStack, MPGNBTData.Range);
        if (range == 0) {
            MPGItemStackData.putInt(itemStack, MPGNBTData.Range, 1);
            return 1;
        }
        return range;
    }

    public void setRange(ItemStack itemStack, int range) {
        setRange(itemStack, range, null);
    }

    public void setRange(ItemStack itemStack, int range, Player player) {
        int normalizedRange = range == 0 ? 1 : range;
        MPGItemStackData.putInt(itemStack, MPGNBTData.Range, normalizedRange);
        if (player != null) {
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(
                    itemStack.getDisplayName().getString() + " " + text("mode.range.name") + ": "
                            + normalizedRange + "x" + normalizedRange + "x" + normalizedRange)),
                    messageUsesOverlay());
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(
                itemStack.getDisplayName().getString() + " " + text("mode.doubling") + ": "
                        + (doubling ? text("info.on") : text("info.off")))), messageUsesOverlay());
    }

    protected boolean messageUsesOverlay() {
        return true;
    }

    private static String text(String translationKey) {
        return Component.translatable(translationKey).getString();
    }
}
