package github.com.gengyoubo.MPG.entity;

import github.com.gengyoubo.common.entity.MPGLightningBoltBase;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MPGLightningBolt extends MPGLightningBoltBase {
    public MPGLightningBolt(EntityType<? extends MPGLightningBolt> type, Level level) {
        super(type, level);
    }

    // Entity#hurtServer is abstract in 26.3; this bolt is visual-only and never takes damage.
    @Override
    public boolean hurtServer(@NotNull ServerLevel level, @NotNull DamageSource source, float damage) {
        return false;
    }
}
