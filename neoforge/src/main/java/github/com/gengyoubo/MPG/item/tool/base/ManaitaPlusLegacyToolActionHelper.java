package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.common.ItemAbilities;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.List;
import java.util.Optional;
import java.util.function.IntConsumer;

public final class ManaitaPlusLegacyToolActionHelper {
    private ManaitaPlusLegacyToolActionHelper() {
    }

    @FunctionalInterface
    public interface RangeBlockAction {
        boolean apply(BlockPos.MutableBlockPos mutableBlockPos, BlockState blockState);
    }

    public static boolean applyInRange(UseOnContext context, int range, RangeBlockAction action) {
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        int xM = center.getX() + range;
        int yM = center.getY() + range;
        int zM = center.getZ() + range;
        boolean changed = false;

        for (int x = center.getX() - range; x <= xM; x++) {
            for (int y = center.getY() - range; y <= yM; y++) {
                for (int z = center.getZ() - range; z <= zM; z++) {
                    mutableBlockPos.set(x, y, z);
                    changed |= action.apply(mutableBlockPos, level.getBlockState(mutableBlockPos));
                }
            }
        }
        return changed;
    }

    public static boolean applyHoeTillAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockState tillState = blockState.getToolModifiedState(context, ItemAbilities.HOE_TILL, false);
        if (tillState == null) {
            return false;
        }

        level.playSound(player, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide) {
            level.setBlock(pos, tillState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, tillState));
            damageHeldItem(context, player, context.getItemInHand());
        }
        return true;
    }

    public static void handleRangeOrEnchantmentUse(Level level, Player player, ItemStack itemInHand, int nextRange, IntConsumer rangeSetter) {
        if (level.isClientSide) {
            return;
        }
        if (player.isShiftKeyDown()) {
            rangeSetter.accept(nextRange);
            return;
        }

        Holder.Reference<net.minecraft.world.item.enchantment.Enchantment> fortune =
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FORTUNE);
        Holder.Reference<net.minecraft.world.item.enchantment.Enchantment> silkTouch =
                level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.SILK_TOUCH);
        boolean hasSilkTouch = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemInHand) > 0;
        String enchantName = I18n.get("enchantments.fortune");
        EnchantmentHelper.updateEnchantments(itemInHand, mutable -> {
            mutable.set(fortune, 10);
            if (!hasSilkTouch) {
                mutable.set(silkTouch, 1);
            }
        });
        if (!hasSilkTouch) {
            enchantName = I18n.get("enchantments.silktouch");
        }
        MPUtils.chat(player, Component.literal(MPText.manaita_enchantment.formatting(itemInHand.getDisplayName().getString() + I18n.get("info.enchantment") + ": " + enchantName)));
    }

    public static void appendRangeAndDoublingTooltip(List<Component> tooltip, int range, boolean doubling) {
        String rangeValue = String.valueOf(range);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(I18n.get("mode.manaita_tool") + ": " + rangeValue + "x" + rangeValue + "x" + rangeValue)));
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(I18n.get("mode.doubling") + ":" + (doubling ? I18n.get("info.on") : I18n.get("info.off")))));
    }

    public static boolean applyAxeActions(UseOnContext context, BlockPos pos, BlockState blockState) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        Optional<BlockState> stripped = Optional.ofNullable(blockState.getToolModifiedState(context, ItemAbilities.AXE_STRIP, false));
        Optional<BlockState> scraped = stripped.isPresent() ? Optional.empty() : Optional.ofNullable(blockState.getToolModifiedState(context, ItemAbilities.AXE_SCRAPE, false));
        Optional<BlockState> waxOff = stripped.isPresent() || scraped.isPresent() ? Optional.empty() : Optional.ofNullable(blockState.getToolModifiedState(context, ItemAbilities.AXE_WAX_OFF, false));
        Optional<BlockState> targetState = Optional.empty();

        if (stripped.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            targetState = stripped;
        } else if (scraped.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3005, pos, 0);
            targetState = scraped;
        } else if (waxOff.isPresent()) {
            level.playSound(player, pos, SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.levelEvent(player, 3004, pos, 0);
            targetState = waxOff;
        }

        if (targetState.isEmpty()) {
            return false;
        }

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
        }
        level.setBlock(pos, targetState.get(), 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, targetState.get()));
        damageHeldItem(context, player, itemStack);
        return true;
    }

    public static boolean applyGrowPlantAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        if (!(blockState.getBlock() instanceof GrowingPlantHeadBlock growingPlantHeadBlock) || growingPlantHeadBlock.isMaxAge(blockState)) {
            return false;
        }

        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack itemStack = context.getItemInHand();

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
        }
        level.playSound(player, pos, SoundEvents.GROWING_PLANT_CROP, SoundSource.BLOCKS, 1.0F, 1.0F);
        BlockState maxAgeState = growingPlantHeadBlock.getMaxAgeState(blockState);
        level.setBlockAndUpdate(pos, maxAgeState);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, maxAgeState));
        damageHeldItem(context, player, itemStack);
        return true;
    }

    public static boolean applyShovelAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        if (context.getClickedFace() == Direction.DOWN) {
            return false;
        }

        Level level = context.getLevel();
        Player player = context.getPlayer();

        BlockState flattenState = blockState.getToolModifiedState(context, ItemAbilities.SHOVEL_FLATTEN, false);
        BlockState targetState = null;
        if (flattenState != null && level.isEmptyBlock(pos.above())) {
            level.playSound(player, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
            targetState = flattenState;
        } else if (blockState.getBlock() instanceof CampfireBlock && blockState.getValue(CampfireBlock.LIT)) {
            if (!level.isClientSide()) {
                level.levelEvent(null, 1009, pos, 0);
            }
            CampfireBlock.dowse(context.getPlayer(), level, pos, blockState);
            targetState = blockState.setValue(CampfireBlock.LIT, Boolean.FALSE);
        }

        if (targetState == null) {
            return false;
        }

        if (!level.isClientSide) {
            level.setBlock(pos, targetState, 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, targetState));
            damageHeldItem(context, player, context.getItemInHand());
        }
        return true;
    }

    private static void damageHeldItem(UseOnContext context, Player player, ItemStack itemStack) {
        if (player != null) {
            EquipmentSlot slot = context.getHand() == net.minecraft.world.InteractionHand.MAIN_HAND
                    ? EquipmentSlot.MAINHAND
                    : EquipmentSlot.OFFHAND;
            itemStack.hurtAndBreak(1, player, slot);
        }
    }
}

