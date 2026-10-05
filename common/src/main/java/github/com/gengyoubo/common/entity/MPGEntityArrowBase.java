package github.com.gengyoubo.common.entity;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

/** Shared infinite-damage, non-pickup arrow behavior. */
public abstract class MPGEntityArrowBase extends AbstractArrow {
    protected MPGEntityArrowBase(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        setBaseDamage(Double.MAX_VALUE);
    }

    protected MPGEntityArrowBase(EntityType<? extends AbstractArrow> type, LivingEntity owner, Level level) {
        super(type, owner, level, ItemStack.EMPTY, ItemStack.EMPTY);
        setBaseDamage(Double.MAX_VALUE);
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected @NotNull ItemStack getDefaultPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity target = unwrapTarget(hitResult.getEntity());
        super.onHitEntity(hitResult);
        if (level().isClientSide()) {
            return;
        }

        Entity owner = getOwner();
        DamageSource damageSource = null;
        if (owner instanceof Player player) {
            damageSource = target.damageSources().playerAttack(player);
        } else if (owner instanceof LivingEntity livingEntity) {
            damageSource = target.damageSources().mobAttack(livingEntity);
        }
        if (damageSource != null) {
            target.hurtServer((ServerLevel) level(), damageSource, 100000.0F);
        }
        markForDeath(target);
    }

    protected Entity unwrapTarget(Entity target) {
        return target;
    }

    protected abstract void markForDeath(Entity target);

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        discard();
    }

    @Override
    public void playerTouch(@NotNull Player player) {
    }
}
