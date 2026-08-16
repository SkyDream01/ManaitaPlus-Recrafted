package github.com.gengyoubo.common.item.tool;

import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MPGShearsItemBase extends ShearsItem implements IMPGKey, IMPGDestroy, IMPGDoubling {
    public MPGShearsItemBase(int durability) {
        super(new Item.Properties().stacksTo(1).durability(durability).fireResistant());
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        if (!state.is(Blocks.COBWEB) && !state.is(BlockTags.LEAVES) && !state.is(Blocks.VINE)
                && !state.is(Blocks.GLOW_LICHEN) && !state.is(BlockTags.WOOL)) {
            return super.getDestroySpeed(stack, state);
        }
        return Float.MAX_VALUE;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = level.getBlockState(pos);
        if (applyGrowPlantAction(context, pos, state)) {
            return InteractionResult.sidedSuccess(level.isClientSide);
        }
        return super.useOn(context);
    }

    private static boolean applyGrowPlantAction(UseOnContext context, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof GrowingPlantHeadBlock growingPlant) || growingPlant.isMaxAge(state)) {
            return false;
        }

        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();
        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
        }
        level.playSound(player, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
        BlockState maxAgeState = growingPlant.getMaxAgeState(state);
        level.setBlockAndUpdate(pos, maxAgeState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, maxAgeState));
        if (player != null) {
            itemStack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
        }
        return true;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player,
                                                            @NotNull InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            if (player.isShiftKeyDown()) {
                setRange(itemStack, (getRange(itemStack) + 2) % 21, player);
            } else {
                addToolEnchantments(level, player, itemStack);
            }
        }
        return InteractionResultHolder.pass(itemStack);
    }

    private void addToolEnchantments(Level level, Player player, ItemStack itemStack) {
        Holder.Reference<Enchantment> fortune = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);
        Holder.Reference<Enchantment> silkTouch = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);
        boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemStack) > 0;
        EnchantmentHelper.updateEnchantments(itemStack, mutable -> {
            mutable.set(fortune, 10);
            if (!hasSilkTouch) {
                mutable.set(silkTouch, 1);
            }
        });
        showMessage(player, Component.literal(MPText.manaita_enchantment.formatting(
                itemStack.getDisplayName().getString() + text("info.enchantment") + ": "
                        + text(hasSilkTouch ? "enchantments.fortune" : "enchantments.silktouch"))));
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
        int normalizedRange = range == 0 ? 1 : range;
        MPGItemStackData.putInt(itemStack, MPGNBTData.Range, normalizedRange);
        if (player != null) {
            showMessage(player, Component.literal(MPText.manaita_mode.formatting(
                    "[" + text("item.manaita_plus_general.manaita_shears") + "] "
                            + text("mode.range.name") + ": " + normalizedRange + "x"
                            + normalizedRange + "x" + normalizedRange)));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        int range = getRange(stack);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                text("mode.manaita_tool") + ": " + range + "x" + range + "x" + range)));
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                text("mode.doubling") + ":" + (isDoubling(stack) ? text("info.on") : text("info.off")))));
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack) {
        toggleDoubling(itemStack);
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        boolean doubling = toggleDoubling(itemStack);
        showMessage(player, Component.literal(MPText.manaita_mode.formatting(String.format(
                "[%s] %s: %s", text("item.manaita_plus_general.manaita_shears"),
                text("mode.doubling"), doubling ? text("info.on") : text("info.off")))));
    }

    @Override
    public boolean accept(BlockState state) {
        return !state.is(BlockTags.MINEABLE_WITH_HOE);
    }

    protected void showMessage(Player player, Component message) {
        player.displayClientMessage(message, messageUsesOverlay());
    }

    protected boolean messageUsesOverlay() {
        return true;
    }

    private static String text(String key) {
        return Component.translatable(key).getString();
    }
}
