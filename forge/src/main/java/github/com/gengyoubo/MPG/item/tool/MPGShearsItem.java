package github.com.gengyoubo.MPG.item.tool;

import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.MPG.item.tool.base.ManaitaPlusLegacyToolActionHelper;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.List;

public class MPGShearsItem extends ShearsItem implements IMPGKey, IMPGDestroy, IMPGDoubling {
    public MPGShearsItem() {
        super(new Properties().stacksTo(1).durability(-1).fireResistant());
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (!state.is(Blocks.COBWEB) && !state.is(BlockTags.LEAVES) && !state.is(Blocks.VINE) && !state.is(Blocks.GLOW_LICHEN) && !state.is(BlockTags.WOOL)) {
            return super.getDestroySpeed(stack, state);
        }
        return Float.MAX_VALUE;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockPos = context.getClickedPos();
        BlockState blockState = level.getBlockState(blockPos);
        if (ManaitaPlusLegacyToolActionHelper.applyGrowPlantAction(context, blockPos, blockState)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        ManaitaPlusLegacyToolActionHelper.handleRangeOrEnchantmentUse(level, player, itemInHand, (getRange(itemInHand) + 2) % 21, range -> setRange(itemInHand, range, player));
        return InteractionResultHolder.pass(itemInHand);
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

    public void setRange(ItemStack itemStack, int range, Player player) {
        if (range == 0) {
            range = 1;
        }
        MPGItemStackData.putInt(itemStack, MPGNBTData.Range, range);
        if (player != null) {
            MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting("[" + I18n.get("item.manaita_plus_general.manaita_shears") + "] " + I18n.get("mode.range.name") + ": " + range + "x" + range + "x" + range)));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        ManaitaPlusLegacyToolActionHelper.appendRangeAndDoublingTooltip(tooltip, getRange(stack), isDoubling(stack));
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        MPUtils.chat(player, Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s", I18n.get("item.manaita_plus_general.manaita_shears"), I18n.get("mode.doubling"), (doubling ? I18n.get("info.on") : I18n.get("info.off"))))));
    }

    @Override
    public boolean accept(BlockState state) {
        return !state.is(BlockTags.MINEABLE_WITH_HOE);
    }
}
