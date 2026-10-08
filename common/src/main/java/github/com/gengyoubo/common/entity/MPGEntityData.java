package github.com.gengyoubo.common.entity;

import net.minecraft.world.entity.Entity;

/** Shared entity flags. Scoreboard tags are vanilla, synced and saved on every loader. */
public enum MPGEntityData {
    manaita,
    death,
    remove;

    public static final String KEY = "manaita_plus_recrafted_type";
    private final int flag = 1 << ordinal();

    public void add(Entity entity) {
        if (entity != null) {
            entity.addTag(name());
        }
    }

    public void remove(Entity entity) {
        if (entity != null) {
            entity.removeTag(name());
        }
    }

    public boolean accept(Entity entity) {
        return entity != null && entity.entityTags().contains(name());
    }

    public int getFlag() {
        return flag;
    }
}
