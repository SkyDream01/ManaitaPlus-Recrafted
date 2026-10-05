package github.com.gengyoubo.common.item;

import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

/** Shared Manaita sword modes and player-only execution attack. */
public abstract class MPGSwordItemBase extends Item implements IMPGKey, IMPGDoubling {
    private static final int MAX_ATTACK_AREA = 27;
    // Replaces the deleted nested Tier: same values, with the netherite-ingot repair
    // Ingredient mapped onto the vanilla netherite repair tag (exactly netherite_ingot).
    private static final ToolMaterial MANAITA_SWORD_MATERIAL = new ToolMaterial(
            // enchantmentValue 1 replaces the old zero: Enchantable now rejects
            // non-positive values, and 1 keeps isEnchantable() true as before.
            BlockTags.INCORRECT_FOR_NETHERITE_TOOL, -1, Float.MAX_VALUE, Float.MAX_VALUE, 1,
            ItemTags.NETHERITE_TOOL_MATERIALS);

    protected MPGSwordItemBase(Item.Properties props) {
        // 0/0 attack baselines keep the old base damage and swing speed (the sword used to
        // register no attribute modifiers); the material's max bonus still applies.
        // UNBREAKABLE restores the old uses = -1 "never breaks" behaviour, with its vanilla
        // tooltip line hidden to keep the previous tooltip.
        super(props.fireResistant()
                .sword(MANAITA_SWORD_MATERIAL, 0.0F, 0.0F)
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull ServerLevel level, @NotNull Entity entity,
                              @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        stack.setPopTime(0);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull Item.TooltipContext context,
                                @NotNull TooltipDisplay display, @NotNull Consumer<Component> tooltip,
                                @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        tooltip.accept(modeLine("mode.doubling", isDoubling(stack)));
        int area = getAttackArea(stack);
        tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                translate("mode.attack_area") + ": " + area + "x" + area)));
        tooltip.accept(modeLine("mode.attack_friendly_mob", attacksFriendlyMobs(stack)));
        tooltip.accept(Component.empty());
        tooltip.accept(Component.literal(MPText.manaita_infinity.formatting(translate("info.attack"))));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.literal(MPText.manaita_infinity.formatting(translate("item.manaita_sword.name")));
    }

    public int getAttackArea(ItemStack stack) {
        int area = MPGItemStackData.getInt(stack, MPGNBTData.AttackArea);
        return area <= 0 ? 1 : Math.min(area, MAX_ATTACK_AREA);
    }

    public void setAttackArea(ItemStack stack, int area) {
        MPGItemStackData.putInt(stack, MPGNBTData.AttackArea,
                Math.max(1, Math.min(MAX_ATTACK_AREA, area)));
    }

    public boolean attacksFriendlyMobs(ItemStack stack) {
        return MPGItemStackData.getBoolean(stack, MPGNBTData.AttackFriendlyMob);
    }

    public boolean toggleAttackFriendlyMobs(ItemStack stack) {
        boolean enabled = !attacksFriendlyMobs(stack);
        MPGItemStackData.putBoolean(stack, MPGNBTData.AttackFriendlyMob, enabled);
        return enabled;
    }

    /** Called only from player attack hooks; mobs receive the normal weapon attack without execution. */
    public final void performPlayerAttack(Player player, ItemStack stack, LivingEntity primaryTarget) {
        if (player.level().isClientSide()) {
            return;
        }

        executeTarget(player, primaryTarget);
        int area = getAttackArea(stack);
        int radius = area / 2;
        if (radius > 0) {
            AABB attackBox = player.getBoundingBox().inflate(radius, Math.max(4, radius), radius);
            boolean attackFriendly = attacksFriendlyMobs(stack);
            for (LivingEntity target : player.level().getEntitiesOfClass(LivingEntity.class, attackBox,
                    target -> target != player && target != primaryTarget && target.isAlive())) {
                if (!attackFriendly && !(target instanceof Enemy)) {
                    continue;
                }
                executeTarget(player, target);
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_ATTACK_SWEEP, player.getSoundSource(), 1.0F, 1.0F);
    }

    private static void executeTarget(Player player, LivingEntity target) {
        if (target == null || !target.isAlive()) {
            return;
        }
        DamageSource source = target.damageSources().playerAttack(player);
        target.hurt(source, Math.max(0.0F, target.getHealth()));
        if (!target.isDeadOrDying()) {
            target.setHealth(0.0F);
            target.die(source);
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack stack, Player player) {
        if (player.isShiftKeyDown()) {
            toggleAttackFriendlyMobs(stack);
        } else {
            int current = getAttackArea(stack);
            setAttackArea(stack, current >= MAX_ATTACK_AREA ? 1 : current + 2);
        }
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack stack, Player player) {
        if (player.isShiftKeyDown()) {
            boolean enabled = toggleAttackFriendlyMobs(stack);
            showModeMessage(player, stack, "mode.attack_friendly_mob",
                    translate(enabled ? "info.on" : "info.off"));
        } else {
            int current = getAttackArea(stack);
            setAttackArea(stack, current >= MAX_ATTACK_AREA ? 1 : current + 2);
            int area = getAttackArea(stack);
            showModeMessage(player, stack, "mode.attack_area", area + "x" + area);
        }
    }

    @Override
    public boolean usesDedicatedDoublingKey() {
        return true;
    }

    @Override
    public void onDedicatedDoublingKey(ItemStack stack, Player player) {
        toggleDoubling(stack);
    }

    @Override
    public void onDedicatedDoublingKeyOnClient(ItemStack stack, Player player) {
        boolean enabled = toggleDoubling(stack);
        showModeMessage(player, stack, "mode.doubling", translate(enabled ? "info.on" : "info.off"));
    }

    @Override
    public boolean shouldMultiplyDrops(ItemStack stack, Player player) {
        return isDoubling(stack) && player.isShiftKeyDown();
    }

    @Override
    public boolean shouldMultiplyExperience(ItemStack stack, Player player) {
        return false;
    }

    private void showModeMessage(Player player, ItemStack stack, String mode, String value) {
        Component message = Component.literal(MPText.manaita_mode.formatting(
                stack.getDisplayName().getString() + " " + translate(mode) + ": " + value));
        if (keyMessageUsesOverlay()) {
            player.sendOverlayMessage(message);
        } else {
            player.sendSystemMessage(message);
        }
    }

    protected boolean keyMessageUsesOverlay() {
        return false;
    }

    private static Component modeLine(String translationKey, boolean enabled) {
        return Component.literal(MPText.manaita_mode.formatting(
                translate(translationKey) + ": " + translate(enabled ? "info.on" : "info.off")));
    }

    protected static String translate(String key) {
        return Component.translatable(key).getString();
    }
}
