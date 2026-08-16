package github.com.gengyoubo.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGLightningBolt extends LightningBolt {
    public MPGLightningBolt(EntityType<? extends LightningBolt> entityType, Level level) {
        super(entityType, level);
        this.noCulling = true;
    }

    @Override
    public void tick() {
        if (this.tickCount == 0 && this.level().isClientSide()) {
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F, false);
            this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F, false);
        }
        super.tick();
        if (!(this.level() instanceof ServerLevel) && !this.isRemoved()) {
            this.level().setSkyFlashTime(2);
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }
}

