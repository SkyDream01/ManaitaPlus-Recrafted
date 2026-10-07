// SPDX-License-Identifier: GPL-3.0-only
package github.com.gengyoubo.MPG.item;

import github.com.gengyoubo.common.item.data.IMPGOffhandKey;
import github.com.gengyoubo.common.util.MPGItemStackData;
import github.com.gengyoubo.common.util.MPGNBTData;
import github.com.gengyoubo.common.util.MPText;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Consumer;
import org.jetbrains.annotations.Nullable;

/** Passive, all-direction shield; the mode key changes its cube size or floating mode. */
public class MPGShieldItem extends ShieldItem implements IMPGOffhandKey {
    private static final int MAX_RANGE = 7;
    private static final double FIELD_GAP = 0.05;
    private static final double REPEL_SPEED = 0.9;
    private static final Set<Player> HOVERING = Collections.synchronizedSet(
            Collections.newSetFromMap(new WeakHashMap<>()));

    public MPGShieldItem(Item.Properties properties) {
        super(properties.stacksTo(1).fireResistant()
                .component(DataComponents.UNBREAKABLE, Unit.INSTANCE)
                .component(DataComponents.TOOLTIP_DISPLAY,
                        TooltipDisplay.DEFAULT.withHidden(DataComponents.UNBREAKABLE, true)));
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, @Nullable EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        stack.setPopTime(0);
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.literal(MPText.manaita_infinity.formatting(text("item.manaita_plus_general.manaita_shield")));
    }

    public static int range(ItemStack stack) {
        int value = MPGItemStackData.getInt(stack, MPGNBTData.ShieldRange);
        return value <= 0 ? 1 : Math.min(value | 1, MAX_RANGE);
    }

    public static boolean floating(ItemStack stack) {
        // A new shield starts with floating enabled; custom data records explicit opt-out.
        return !MPGItemStackData.contains(stack, MPGNBTData.ShieldFloating)
                || MPGItemStackData.getBoolean(stack, MPGNBTData.ShieldFloating);
    }

    private static int protectionRange(Player player) {
        int main = player.getMainHandItem().getItem() instanceof MPGShieldItem
                ? range(player.getMainHandItem()) : 0;
        int off = player.getOffhandItem().getItem() instanceof MPGShieldItem
                ? range(player.getOffhandItem()) : 0;
        return Math.max(main, off);
    }

    private static boolean hasFloatingShield(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.getItem() instanceof MPGShieldItem && floating(main)
                || off.getItem() instanceof MPGShieldItem && floating(off);
    }

    public static boolean protects(LivingEntity target) {
        // The death event runs after health reaches zero. Keep the holder protected there too.
        if (target instanceof Player player && protectionRange(player) > 0) {
            return true;
        }
        for (Player holder : target.level().players()) {
            if (!holder.isAlive() || (target != holder && !(target instanceof Player) && !holder.isAlliedTo(target))) {
                continue;
            }
            AABB domain = domain(holder);
            if (domain != null && domain.intersects(target.getBoundingBox())) {
                return true;
            }
        }
        return false;
    }

    /** Removes projectiles that start inside or cross into any active shield domain. */
    public static boolean blockProjectile(Projectile projectile, Vec3 nextPosition) {
        if (projectile.level().isClientSide()) {
            return false;
        }
        AABB projectileBox = projectile.getBoundingBox();
        Vec3 start = projectileBox.getCenter();
        for (Player holder : projectile.level().players()) {
            AABB field = domain(holder);
            if (field == null) {
                continue;
            }
            AABB barrier = field.inflate(projectileBox.getXsize() / 2.0,
                    projectileBox.getYsize() / 2.0, projectileBox.getZsize() / 2.0);
            if (barrier.contains(start)) {
                if (projectile.getOwner() instanceof Player owner
                        && field.contains(owner.getBoundingBox().getCenter())) {
                    // A player already inside the field can shoot outward.
                    continue;
                }
                projectile.discard();
                return true;
            }
            if (barrier.clip(start, nextPosition).isPresent()) {
                projectile.discard();
                return true;
            }
        }
        return false;
    }

    /** Ejects a hostile through the nearest free face, then gives it outward momentum. */
    public static void repelHostile(Entity entity) {
        if (entity.level().isClientSide() || !(entity instanceof LivingEntity hostile)
                || !(entity instanceof Enemy) || !hostile.isAlive()) {
            return;
        }
        for (Player holder : entity.level().players()) {
            AABB field = domain(holder);
            if (field == null) {
                continue;
            }
            AABB box = hostile.getBoundingBox();
            if (!field.intersects(box)) {
                continue;
            }
            double west = box.maxX - field.minX + FIELD_GAP;
            double east = field.maxX - box.minX + FIELD_GAP;
            double north = box.maxZ - field.minZ + FIELD_GAP;
            double south = field.maxZ - box.minZ + FIELD_GAP;
            double down = box.maxY - field.minY + FIELD_GAP;
            double up = field.maxY - box.minY + FIELD_GAP;
            List<Vec3> exits = List.of(new Vec3(-west, 0, 0), new Vec3(east, 0, 0),
                    new Vec3(0, 0, -north), new Vec3(0, 0, south),
                    new Vec3(0, -down, 0), new Vec3(0, up, 0)).stream()
                    .sorted(Comparator.comparingDouble(Vec3::lengthSqr)).toList();
            Vec3 displacement = exits.stream()
                    .filter(exit -> hostile.level().noCollision(hostile, box.move(exit)))
                    .findFirst().orElse(exits.getFirst());
            hostile.teleportRelative(displacement.x, displacement.y, displacement.z);
            Vec3 outward = displacement.normalize().scale(REPEL_SPEED);
            hostile.setDeltaMovement(outward.x,
                    displacement.y == 0.0 ? Math.max(0.2, hostile.getDeltaMovement().y) : outward.y,
                    outward.z);
        }
    }

    private static @Nullable AABB domain(Player holder) {
        int size = holder.isAlive() ? protectionRange(holder) : 0;
        if (size == 0) {
            return null;
        }
        double radius = size / 2.0;
        Vec3 center = holder.getBoundingBox().getCenter();
        return new AABB(center.x - radius, center.y - radius, center.z - radius,
                center.x + radius, center.y + radius, center.z + radius);
    }

    public static void tickFloating(Player player) {
        boolean enabled = hasFloatingShield(player);
        boolean hovering = enabled && player.isShiftKeyDown() && !player.onGround()
                && !player.getAbilities().flying && !player.isFallFlying()
                && !player.isPassenger() && !player.isInWater() && !player.isInLava()
                && !player.isSpectator();

        if (hovering) {
            if (!player.isNoGravity()) {
                player.setNoGravity(true);
                HOVERING.add(player);
            }
            Vec3 movement = player.getDeltaMovement();
            player.setDeltaMovement(movement.x, 0.0, movement.z);
            player.fallDistance = 0.0F;
        } else if (HOVERING.remove(player)) {
            player.setNoGravity(false);
        }

        if (enabled && !player.level().isClientSide()) {
            MobEffectInstance slowFalling = player.getEffect(MobEffects.SLOW_FALLING);
            if (slowFalling == null || slowFalling.getDuration() <= 2) {
                player.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 6, 0, false, false, true));
            }
        }
    }

    @Override
    public void onManaitaKeyPress(ItemStack stack, Player player) {
        if (player.isShiftKeyDown()) {
            MPGItemStackData.putBoolean(stack, MPGNBTData.ShieldFloating, !floating(stack));
        } else {
            int current = range(stack);
            MPGItemStackData.putInt(stack, MPGNBTData.ShieldRange, current >= MAX_RANGE ? 1 : current + 2);
        }
    }

    @Override
    public void onManaitaKeyPressOnClient(ItemStack stack, Player player) {
        onManaitaKeyPress(stack, player);
        if (player.isShiftKeyDown()) {
            showModeMessage(player, stack, "mode.shield_floating", text(floating(stack) ? "info.on" : "info.off"));
        } else {
            int size = range(stack);
            showModeMessage(player, stack, "mode.shield_range", size + "x" + size + "x" + size);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, display, tooltip, flag);
        int size = range(stack);
        tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                text("mode.shield_range") + ": " + size + "x" + size + "x" + size)));
        tooltip.accept(Component.literal(MPText.manaita_mode.formatting(
                text("mode.shield_floating") + ": " + text(floating(stack) ? "info.on" : "info.off"))));
        tooltip.accept(Component.empty());
        tooltip.accept(Component.literal(MPText.manaita_infinity.formatting(text("info.shield"))));
        tooltip.accept(Component.literal(MPText.manaita_enchantment.formatting(text("info.shield_barrier"))));
    }

    private static void showModeMessage(Player player, ItemStack stack, String mode, String value) {
        player.sendSystemMessage(Component.literal(MPText.manaita_mode.formatting(
                stack.getDisplayName().getString() + " " + text(mode) + ": " + value)));
    }

    private static String text(String key) {
        return Component.translatable(key).getString();
    }
}
