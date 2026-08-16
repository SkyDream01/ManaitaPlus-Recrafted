package github.com.gengyoubo.entity;

import github.com.gengyoubo.MPG;
import github.com.gengyoubo.util.MPEntityData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class MPGEntityArrow extends Arrow {
    private static final ResourceLocation ENTITY_ID = github.com.gengyoubo.util.MPResource.id(MPG.MODID, "manaita_arrow");

    public MPGEntityArrow(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
    }

    public static Arrow create(Level level, LivingEntity owner) {
        Arrow arrow = null;
        if (BuiltInRegistries.ENTITY_TYPE.containsKey(ENTITY_ID)) {
            EntityType<?> entityType = BuiltInRegistries.ENTITY_TYPE.get(ENTITY_ID);
            if (entityType != null) {
                arrow = (Arrow) entityType.create(level);
            }
        }
        if (arrow == null) {
            arrow = new Arrow(level, owner, ItemStack.EMPTY, ItemStack.EMPTY);
        }
        arrow.setOwner(owner);
        arrow.setPos(owner.getX(), owner.getEyeY() - 0.1D, owner.getZ());
        return arrow;
    }

    @Override
    protected @NotNull ItemStack getPickupItem() {
        return ItemStack.EMPTY;
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        Entity entity = hitResult.getEntity();
        super.onHitEntity(hitResult);
        if (!level().isClientSide) {
            Entity owner = getOwner();
            if (owner instanceof Player player) {
                DamageSource source = entity.damageSources().playerAttack(player);
                entity.hurt(source, 100000.0F);
            } else if (owner instanceof LivingEntity living) {
                DamageSource source = entity.damageSources().mobAttack(living);
                entity.hurt(source, 100000.0F);
            }
            MPEntityData.death.add(entity);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult hitResult) {
        super.onHit(hitResult);
        discard();
    }

    @Override
    public double getBaseDamage() {
        return Double.MAX_VALUE;
    }

    @Override
    public void playerTouch(@NotNull Player player) {
    }
}

