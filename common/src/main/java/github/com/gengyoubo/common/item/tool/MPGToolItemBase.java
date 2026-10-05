package github.com.gengyoubo.common.item.tool;

import github.com.gengyoubo.common.item.data.IMPGDestroy;
import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class MPGToolItemBase extends Item implements IMPGKey, IMPGDestroy, IMPGDoubling {
    private static final int MAX_DEPTH = 27;
    private final MPGToolProfile profile;

    public MPGToolItemBase(Item.Properties props, ToolMaterial material, TagKey<Block> mineableTag) {
        this(props, material, mineableTag, MPGToolProfile.GENERIC);
    }

    public MPGToolItemBase(Item.Properties props, ToolMaterial material, TagKey<Block> mineableTag, MPGToolProfile profile) {
        // 0/0 attack baselines keep the old base damage and swing speed (the items used to
        // register no attribute modifiers); the material's infinite bonus still applies.
        // UNBREAKABLE restores the old uses = -1 "never breaks" behaviour, with its vanilla
        // tooltip line hidden to keep the previous tooltip.
        super(props.fireResistant()
                .tool(material, mineableTag, 0.0F, 0.0F, 0.0F)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));
        this.profile = profile;
    }

    @Override
    public boolean accept(BlockState state) {
        return profile != MPGToolProfile.PAXEL;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(modeLine("mode.doubling", isDoubling(stack)));
        if (profile.supportsSilkTouch()) {
            tooltip.accept(modeLine("mode.silk_touch", isSilkTouch(stack)));
        }
        if (profile.supportsCanHarvest()) {
            tooltip.accept(modeLine("mode.can_harvest", canHarvest(stack)));
        }
        int range = getRange(stack);
        tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                text("mode.dig_range") + ": " + range + "x" + range)));
        if (profile.supportsDepth()) {
            tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                    text("mode.dig_depth") + ": " + getDepth(stack))));
        }
        if (profile.supportsDigUnderPlayer()) {
            tooltip.accept(modeLine("mode.dig_under_player", isDigUnderPlayer(stack)));
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull Level level, Player player,
                                          @NotNull InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (!level.isClientSide()) {
            if (player.isSprinting() && profile.supportsDepth()) {
                setDepth(itemInHand, getDepth(itemInHand) >= MAX_DEPTH ? 1 : getDepth(itemInHand) + 1, player);
            } else if (player.isShiftKeyDown()) {
                setRange(itemInHand, getRange(itemInHand) >= profile.maxRange() ? 1 : getRange(itemInHand) + 2, player);
            } else if (profile.supportsSilkTouch()) {
                setSilkTouch(itemInHand, !isSilkTouch(itemInHand), player);
                syncToolEnchantments(level, itemInHand);
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity,
                              @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (!level.isClientSide() && entity instanceof Player) {
            syncToolEnchantments(level, stack);
        }
    }

    private void syncToolEnchantments(Level level, ItemStack itemStack) {
        Holder.Reference<Enchantment> fortune = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FORTUNE);
        Holder.Reference<Enchantment> silkTouch = level.registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SILK_TOUCH);
        int fortuneLevel = EnchantmentHelper.getItemEnchantmentLevel(fortune, itemStack);
        int silkTouchLevel = EnchantmentHelper.getItemEnchantmentLevel(silkTouch, itemStack);
        int expectedSilkTouchLevel = profile.supportsSilkTouch() && isSilkTouch(itemStack) ? 1 : 0;
        if (fortuneLevel == 10 && silkTouchLevel == expectedSilkTouchLevel) {
            return;
        }
        EnchantmentHelper.updateEnchantments(itemStack, mutable -> {
            mutable.set(fortune, 10);
            mutable.set(silkTouch, expectedSilkTouchLevel);
        });
    }

    @Override
    public boolean mineBlock(@NotNull ItemStack stack, @NotNull Level level, @NotNull BlockState state,
                             @NotNull BlockPos blockPos, @NotNull LivingEntity livingEntity) {
        return true;
    }

    @Override
    public boolean canDestroyBlock(@NotNull ItemStack stack, @NotNull BlockState state, @NotNull Level level,
                                   @NotNull BlockPos pos, @NotNull LivingEntity user) {
        // canAttackBlock's replacement: both call sites pass the main-hand stack.
        return canHarvest(stack);
    }

    @Override
    public float getDestroySpeed(@NotNull ItemStack stack, BlockState state) {
        return canHarvest(stack) ? super.getDestroySpeed(stack, state) : 0.0F;
    }

    @Override
    public int getRange(ItemStack itemStack) {
        int range = MPGItemStackData.getInt(itemStack, MPGNBTData.Range);
        return range <= 0 ? 1 : Math.min(range, profile.maxRange());
    }

    public void setRange(ItemStack itemStack, int range) {
        setRange(itemStack, range, null);
    }

    public void setRange(ItemStack itemStack, int range, Player player) {
        int normalizedRange = Math.max(1, Math.min(profile.maxRange(), range));
        MPGItemStackData.putInt(itemStack, MPGNBTData.Range, normalizedRange);
        showValue(player, itemStack, "mode.dig_range", normalizedRange + "x" + normalizedRange);
    }

    @Override
    public int getDepth(ItemStack itemStack) {
        if (!profile.supportsDepth()) {
            return 1;
        }
        int depth = MPGItemStackData.getInt(itemStack, MPGNBTData.Depth);
        return depth <= 0 ? 1 : Math.min(depth, MAX_DEPTH);
    }

    public void setDepth(ItemStack itemStack, int depth, Player player) {
        int normalizedDepth = Math.max(1, Math.min(MAX_DEPTH, depth));
        MPGItemStackData.putInt(itemStack, MPGNBTData.Depth, normalizedDepth);
        showValue(player, itemStack, "mode.dig_depth", String.valueOf(normalizedDepth));
    }

    public boolean isSilkTouch(ItemStack itemStack) {
        return profile.supportsSilkTouch()
                && MPGItemStackData.getBoolean(itemStack, MPGNBTData.SilkTouch);
    }

    public void setSilkTouch(ItemStack itemStack, boolean enabled, Player player) {
        MPGItemStackData.putBoolean(itemStack, MPGNBTData.SilkTouch, enabled);
        showToggle(player, itemStack, "mode.silk_touch", enabled);
    }

    @Override
    public boolean canHarvest(ItemStack itemStack) {
        if (!profile.supportsCanHarvest()) {
            return true;
        }
        return !MPGItemStackData.contains(itemStack, MPGNBTData.CanHarvest)
                || MPGItemStackData.getBoolean(itemStack, MPGNBTData.CanHarvest);
    }

    public boolean toggleCanHarvest(ItemStack itemStack) {
        boolean enabled = !canHarvest(itemStack);
        MPGItemStackData.putBoolean(itemStack, MPGNBTData.CanHarvest, enabled);
        return enabled;
    }

    public boolean isDigUnderPlayer(ItemStack itemStack) {
        return profile.supportsDigUnderPlayer()
                && MPGItemStackData.getBoolean(itemStack, MPGNBTData.DigUnderPlayer);
    }

    @Override
    public boolean canDigUnderPlayer(ItemStack itemStack) {
        return !profile.supportsDigUnderPlayer() || isDigUnderPlayer(itemStack);
    }

    public boolean toggleDigUnderPlayer(ItemStack itemStack) {
        boolean enabled = !isDigUnderPlayer(itemStack);
        MPGItemStackData.putBoolean(itemStack, MPGNBTData.DigUnderPlayer, enabled);
        return enabled;
    }

    public boolean usesDedicatedDoublingKey() {
        return profile.usesDedicatedDoublingKey();
    }

    @Override
    public boolean usesStandardModeKey() {
        return !profile.usesDedicatedDoublingKey();
    }

    @Override
    public boolean requiresShiftForDoubling() {
        return profile != MPGToolProfile.PAXEL;
    }

    @Override
    public void onManaitaKeyPress(ItemStack itemStack, Player player) {
        if (player.isSprinting() && profile.supportsDigUnderPlayer()) {
            toggleDigUnderPlayer(itemStack);
        } else if (player.isShiftKeyDown() && profile.supportsCanHarvest()) {
            toggleCanHarvest(itemStack);
        } else {
            toggleDoubling(itemStack);
        }
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack itemStack, Player player) {
        if (player.isSprinting() && profile.supportsDigUnderPlayer()) {
            showToggle(player, itemStack, "mode.dig_under_player", toggleDigUnderPlayer(itemStack));
        } else if (player.isShiftKeyDown() && profile.supportsCanHarvest()) {
            showToggle(player, itemStack, "mode.can_harvest", toggleCanHarvest(itemStack));
        } else {
            showToggle(player, itemStack, "mode.doubling", toggleDoubling(itemStack));
        }
    }

    protected boolean messageUsesOverlay() {
        return true;
    }

    private void showToggle(Player player, ItemStack itemStack, String translationKey, boolean enabled) {
        showValue(player, itemStack, translationKey, enabled ? text("info.on") : text("info.off"));
    }

    private void showValue(Player player, ItemStack itemStack, String translationKey, String value) {
        if (player != null) {
            Component message = Component.literal(MPText.manaita_mode.formatting(
                    itemStack.getDisplayName().getString() + " " + text(translationKey) + ": " + value));
            if (messageUsesOverlay()) {
                player.sendOverlayMessage(message);
            } else {
                player.sendSystemMessage(message);
            }
        }
    }

    private static Component modeLine(String translationKey, boolean enabled) {
        return Component.literal(MPText.manaita_mode.formatting(
                text(translationKey) + ": " + text(enabled ? "info.on" : "info.off")));
    }

    private static String text(String translationKey) {
        return Component.translatable(translationKey).getString();
    }
}
