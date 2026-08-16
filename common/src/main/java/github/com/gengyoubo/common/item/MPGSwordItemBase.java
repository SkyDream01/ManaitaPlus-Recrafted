package github.com.gengyoubo.common.item;

import github.com.gengyoubo.common.item.data.IMPGDoubling;
import github.com.gengyoubo.common.item.data.IMPGKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Shared Manaita sword modes and player-only execution attack. */
public abstract class MPGSwordItemBase extends SwordItem implements IMPGKey, IMPGDoubling {
    private static final int MAX_ATTACK_AREA = 27;

    protected MPGSwordItemBase() {
        super(new ItemManaitaSwordTier(), new Item.Properties().fireResistant());
    }

    @Override
    public void inventoryTick(ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        super.inventoryTick(stack, level, entity, slot, selected);
        stack.setPopTime(0);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        tooltip.add(modeLine("mode.doubling", isDoubling(stack)));
        int area = getAttackArea(stack);
        tooltip.add(Component.literal(MPText.manaita_mode.formatting(
                translate("mode.attack_area") + ": " + area + "x" + area)));
        tooltip.add(modeLine("mode.attack_friendly_mob", attacksFriendlyMobs(stack)));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal(MPText.manaita_infinity.formatting(translate("info.attack"))));
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
        if (player.level().isClientSide) {
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
        player.displayClientMessage(Component.literal(MPText.manaita_mode.formatting(
                stack.getDisplayName().getString() + " " + translate(mode) + ": " + value)),
                keyMessageUsesOverlay());
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

    public static final class ItemManaitaSwordTier implements Tier {
        @Override
        public int getUses() {
            return -1;
        }

        @Override
        public float getSpeed() {
            return Float.MAX_VALUE;
        }

        @Override
        public float getAttackDamageBonus() {
            return Float.MAX_VALUE;
        }

        @Override
        public int getEnchantmentValue() {
            return 0;
        }

        @Override
        public @NotNull Ingredient getRepairIngredient() {
            return Ingredient.of(Items.NETHERITE_INGOT);
        }

        @Override
        public @NotNull TagKey<Block> getIncorrectBlocksForDrops() {
            return BlockTags.INCORRECT_FOR_NETHERITE_TOOL;
        }
    }
}
