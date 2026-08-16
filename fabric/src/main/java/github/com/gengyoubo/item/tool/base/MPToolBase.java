package github.com.gengyoubo.item.tool.base;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.item.tier.MPToolTier;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;

import java.util.List;
import java.util.Objects;

public class MPToolBase extends DiggerItem implements IMPGKey, IMPGDestroy, IMPGDoubling {
    public MPToolBase(TagKey<Block> tagKey) {
        super(new MPToolTier(), tagKey, new Properties().fireResistant());
    }

    @Override
    public boolean accept(BlockState state) {
        return true;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        MPToolActionHelper.appendRangeAndDoublingTooltip(tooltip, getRange(stack), isDoubling(stack));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        MPToolActionHelper.handleRangeOrEnchantmentUse(level, player, itemInHand, (getRange(itemInHand) + 2) % 21, range -> setRange(itemInHand, range, player));
        return InteractionResultHolder.pass(itemInHand);
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state, @NotNull BlockPos blockPos, @NotNull LivingEntity livingEntity) {
        return true;
    }

    @Override
    public int getRange(ItemStack itemStack) {
        if (!github.com.gengyoubo.common.util.MPGItemStackData.hasTag(itemStack)) {
            return 1;
        }
        int range = Objects.requireNonNull(github.com.gengyoubo.common.util.MPGItemStackData.getTag(itemStack)).getInt(MPGNBTData.Range);
        if (range == 0) {
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, MPGNBTData.Range, 1);
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
        github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, MPGNBTData.Range, range);
        if (player != null) {
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(itemStack.getDisplayName().getString() + " " + Component.translatable("mode.range.name").getString() + ": " + range + "x" + range + "x" + range)), true);
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(itemStack.getDisplayName().getString() + " " + Component.translatable("mode.doubling").getString() + ": " + (doubling ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString()))), true);
    }

}


