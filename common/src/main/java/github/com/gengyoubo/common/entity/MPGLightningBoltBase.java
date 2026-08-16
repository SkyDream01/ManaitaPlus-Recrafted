package github.com.gengyoubo.common.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

/** Shared visual-only lightning lifecycle used by the god sword. */
public abstract class MPGLightningBoltBase extends Entity {
    private int life;
    public long seed;
    private int flashes;

    protected MPGLightningBoltBase(EntityType<?> type, Level level) {
        super(type, level);
        noCulling = true;
        life = 2;
        seed = random.nextLong();
        flashes = random.nextInt(3) + 1;
    }

    @Override
    public @NotNull SoundSource getSoundSource() {
        return SoundSource.WEATHER;
    }

    @Override
    public void tick() {
        super.tick();
        if (life == 2 && level().isClientSide()) {
            level().playLocalSound(getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER,
                    SoundSource.WEATHER, 10000.0F, 0.8F + random.nextFloat() * 0.2F, false);
            level().playLocalSound(getX(), getY(), getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT,
                    SoundSource.WEATHER, 2.0F, 0.5F + random.nextFloat() * 0.2F, false);
        }

        life--;
        if (life < 0) {
            if (flashes == 0) {
                discard();
            } else if (life < -random.nextInt(10)) {
                flashes--;
                life = 1;
                seed = random.nextLong();
            }
        }

        if (life >= 0 && !(level() instanceof ServerLevel)) {
            level().setSkyFlashTime(2);
        }
    }

    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        double range = 64.0D * getViewScale();
        return distance < range * range;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
    }
}
