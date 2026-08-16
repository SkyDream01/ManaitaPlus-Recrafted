package github.com.gengyoubo.item.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.item.tool.base.MPToolActionHelper;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;

import java.util.List;

public class MPShearsItem extends ShearsItem implements IMPGKey, IMPGDestroy, IMPGDoubling {
    public MPShearsItem() {
        super(new Properties().stacksTo(1).durability(Integer.MAX_VALUE).fireResistant());
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
        if (MPToolActionHelper.applyGrowPlantAction(context, blockPos, blockState)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        MPToolActionHelper.handleRangeOrEnchantmentUse(level, player, itemInHand, (getRange(itemInHand) + 2) % 21, range -> setRange(itemInHand, range, player));
        return InteractionResultHolder.pass(itemInHand);
    }

    @Override
    public int getRange(ItemStack itemStack) {
        int range = github.com.gengyoubo.common.util.MPGItemStackData.getInt(itemStack, MPGNBTData.Range);
        if (range == 0) {
            github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, MPGNBTData.Range, 1);
            return 1;
        }
        return range;
    }

    public void setRange(ItemStack itemStack, int range, Player player) {
        if (range == 0) {
            range = 1;
        }
        github.com.gengyoubo.common.util.MPGItemStackData.putInt(itemStack, MPGNBTData.Range, range);
        if (player != null) {
            player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting("[" + Component.translatable("item.manaita_plus_general.manaita_shears").getString() + "] " + Component.translatable("mode.range.name").getString() + ": " + range + "x" + range + "x" + range)), true);
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        MPToolActionHelper.appendRangeAndDoublingTooltip(tooltip, getRange(stack), isDoubling(stack));
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(String.format("[%s] %s: %s", Component.translatable("item.manaita_plus_general.manaita_shears").getString(), Component.translatable("mode.doubling").getString(), (doubling ? Component.translatable("info.on").getString() : Component.translatable("info.off").getString())))), true);
    }

    @Override
    public boolean accept(BlockState state) {
        return !state.is(BlockTags.MINEABLE_WITH_HOE);
    }
}


