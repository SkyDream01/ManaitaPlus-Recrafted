package github.com.gengyoubo.MPG.item.tool.base;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.component.BlockTransformer;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.BlockTransformers;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.GrowingPlantHeadBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import github.com.gengyoubo.common.util.MPText;
import github.com.gengyoubo.MPG.util.MPUtils;

import java.util.List;
import java.util.function.IntConsumer;

public final class ManaitaPlusLegacyToolActionHelper {
    private ManaitaPlusLegacyToolActionHelper() {
    }

    @FunctionalInterface
    public interface RangeBlockAction {
        boolean apply(BlockPos.MutableBlockPos mutableBlockPos, BlockState blockState);
    }

    /** The block transform entry that applies at a block together with the state it produces. */
    private record ResolvedTransform(SoundEvent sound, BlockTransformer.TransformParticle particle, BlockState state) {
    }

    /**
     * Resolves the data-driven block transform that applies at the given block. The deleted
     * strip/till/flatten ItemAbilities are now the {@link BlockTransformers} registry entries;
     * the first matching entry wins, matching the vanilla useOn priority.
     */
    private static ResolvedTransform findTransform(UseOnContext context, BlockPos pos, ResourceKey<BlockTransformer> transformerKey) {
        Level level = context.getLevel();
        Holder<BlockTransformer> transformer = level.registryAccess()
                .lookupOrThrow(Registries.BLOCK_TRANSFORMER)
                .getOrThrow(transformerKey);
        for (BlockTransformer.BlockTransformData transformData : transformer.value().transforms()) {
            if (transformData.disallowedFaces().contains(context.getClickedFace())) {
                continue;
            }
            BlockState newState = transformData.blockStateProvider().value().getOptionalState(level, level.getRandom(), pos);
            if (newState != null) {
                return new ResolvedTransform(transformData.sound().value(), transformData.particle(), newState);
            }
        }
        return null;
    }

    public static boolean applyInRange(UseOnContext context, int range, RangeBlockAction action) {
        Level level = context.getLevel();
        BlockPos center = context.getClickedPos();
        BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
        boolean changed = false;

        for (int first = -range; first <= range; first++) {
            for (int second = -range; second <= range; second++) {
                switch (context.getClickedFace().getAxis()) {
                    case X -> mutableBlockPos.set(center.getX(), center.getY() + first, center.getZ() + second);
                    case Y -> mutableBlockPos.set(center.getX() + first, center.getY(), center.getZ() + second);
                    case Z -> mutableBlockPos.set(center.getX() + first, center.getY() + second, center.getZ());
                }
                changed |= action.apply(mutableBlockPos, level.getBlockState(mutableBlockPos));
            }
        }
        return changed;
    }

    public static boolean applyHoeTillAction(UseOnContext context, BlockPos pos, BlockState blockState) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        // HOE_TILL is gone in 26.3: the hoe behaviour moved into the data-driven
        // BlockTransformers.HOE registry entry, which is queried here instead.
        ResolvedTransform till = findTransform(context, pos, BlockTransformers.HOE);
        if (till == null) {
            return false;
        }

        level.playSound(player, pos, till.sound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!level.isClientSide()) {
            level.setBlock(pos, till.state(), 11);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, till.state()));
            damageHeldItem(context, player, context.getItemInHand());
        }
        return true;
    }

    public static void handleRangeOrEnchantmentUse(Level level, Player player, ItemStack itemInHand, int nextRange, IntConsumer rangeSetter) {
        if (level.isClientSide()) {
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

        // Strip, scrape and wax-off used to be the AXE_STRIP/AXE_SCRAPE/AXE_WAX_OFF ItemAbilities;
        // in 26.3 they are the ordered entries of the data-driven BlockTransformers.AXE registry
        // entry (stripping first, then scraping, then wax removal), so the first matching entry
        // wins exactly like the old strip -> scrape -> wax-off priority. The per-entry sound and
        // particle reproduce the old hard-coded sounds and the 3005/3004 level events.
        ResolvedTransform resolved = findTransform(context, pos, BlockTransformers.AXE);
        if (resolved == null) {
            return false;
        }

        level.playSound(player, pos, resolved.sound(), SoundSource.BLOCKS, 1.0F, 1.0F);
        resolved.particle().send(level, player, pos);

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, itemStack);
        }
        level.setBlock(pos, resolved.state(), 11);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, resolved.state()));
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

        // SHOVEL_FLATTEN is gone in 26.3: path flattening moved into the data-driven
        // BlockTransformers.SHOVEL registry entry (which also wants the block above to be free).
        ResolvedTransform flatten = findTransform(context, pos, BlockTransformers.SHOVEL);
        BlockState targetState = null;
        if (flatten != null && level.isEmptyBlock(pos.above())) {
            level.playSound(player, pos, flatten.sound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            targetState = flatten.state();
        } else if (blockState.getBlock() instanceof CampfireBlock && blockState.getValue(CampfireBlock.LIT)) {
            if (!level.isClientSide()) {
                level.levelEvent(null, 1009, pos, 0);
            }
            // CampfireBlock#dowse is spelled douse in 26.3.
            CampfireBlock.douse(context.getPlayer(), level, pos, blockState);
            targetState = blockState.setValue(CampfireBlock.LIT, Boolean.FALSE);
        }

        if (targetState == null) {
            return false;
        }

        if (!level.isClientSide()) {
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
