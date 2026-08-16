package github.com.gengyoubo.MPG.entity;

import github.com.gengyoubo.MPG.core.MPGEntityCore;
import github.com.gengyoubo.common.entity.MPGEntityData;
import github.com.gengyoubo.common.entity.MPGEntityArrowBase;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.level.Level;
import net.minecraftforge.entity.PartEntity;

public class MPGEntityArrow extends MPGEntityArrowBase {
    public MPGEntityArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
    }

    private MPGEntityArrow(Level level, LivingEntity owner) {
        super(MPGEntityCore.ManaitaArrow.get(), owner, level);
    }

    public static MPGEntityArrow create(Level level, LivingEntity owner) {
        return new MPGEntityArrow(level, owner);
    }

    @Override
    protected Entity unwrapTarget(Entity target) {
        while (target instanceof PartEntity<?> part) {
            target = part.getParent();
        }
        return target;
    }

    @Override
    protected void markForDeath(Entity target) {
        MPGEntityData.death.add(target);
    }
}
