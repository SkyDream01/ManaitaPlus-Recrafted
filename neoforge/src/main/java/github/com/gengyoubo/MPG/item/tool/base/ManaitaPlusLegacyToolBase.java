package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.MPG.item.tier.MPGToolTier;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.List;
public class ManaitaPlusLegacyToolBase extends DiggerItem implements IMPGKey, IMPGDestroy, IMPGDoubling {
    public ManaitaPlusLegacyToolBase(TagKey<Block> tagKey) {
        super(new MPGToolTier(), tagKey, new Properties().fireResistant());
    }

    @Override
    public boolean accept(BlockState state) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ManaitaPlusLegacyToolActionHelper.appendRangeAndDoublingTooltip(tooltip, getRange(stack), isDoubling(stack));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        ManaitaPlusLegacyToolActionHelper.handleRangeOrEnchantmentUse(level, player, itemInHand, (getRange(itemInHand) + 2) % 21, range -> setRange(itemInHand, range, player));
        return InteractionResultHolder.pass(itemInHand);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos blockPos, @NotNull LivingEntity livingEntity) {
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
        if (range == 0) {
            range = 1;
        }
        MPGItemStackData.putInt(itemStack, MPGNBTData.Range, range);
        if (player != null) {
            MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(itemStack.getDisplayName().getString() + " " + I18n.get("mode.range.name") + ": " + range + "x" + range + "x" + range)));
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(itemStack.getDisplayName().getString() + " " + I18n.get("mode.doubling") + ": " + (doubling ? I18n.get("info.on") : I18n.get("info.off")))));
    }

}
