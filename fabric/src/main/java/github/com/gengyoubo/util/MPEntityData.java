package github.com.gengyoubo.util;

import net.minecraft.world.entity.Entity;

public enum MPEntityData {
    manaita,
    death,
    remove;

    public static final String KEY = "manaita_plus_general_type";

    private final String tagName;
    private final int flag;

    MPEntityData() {
        this.tagName = name();
        this.flag = 1 << ordinal();
    }

    public void add(Entity entity) {
        if (entity == null) {
            return;
        }

        entity.addTag(tagName);
    }

    public void remove(Entity entity) {
        if (entity == null) {
            return;
        }

        entity.removeTag(tagName);
    }

    public boolean accept(Entity entity) {
        if (entity == null) {
            return false;
        }

        return entity.getTags().contains(tagName);
    }

    public int getFlag() {
        return flag;
    }
}

