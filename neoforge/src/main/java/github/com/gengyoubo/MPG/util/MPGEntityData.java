package github.com.gengyoubo.MPG.util;

import net.minecraft.world.entity.Entity;

public enum MPGEntityData {
    manaita(),
    death(),
    remove();

    public static final String cName = "manaita_plus_general_type";

    MPGEntityData() {
        flag = pow(2,ordinal());
        name = name();
    }

    public static int pow(int a, int b) {
        if(b == 0) return 1;
        int pow = 1;
        for (int i = 0; i < b; i++) {
            pow *= a;
        }
        return pow;
    }


    private final String name;
    private final int flag;

    public void add(Entity entity) {
        if (entity == null)
            return;
        entity.addTag(name);
        entity.getPersistentData().putInt(cName, entity.getPersistentData().getInt(cName) | flag);
    }

    public void remove(Entity entity) {
        if (entity == null) return;
        entity.removeTag(name);
        entity.getPersistentData().putInt(cName, entity.getPersistentData().getInt(cName) & ~flag);
    }


    public boolean accept(Entity entity) {
        if (entity == null) return false;
        return entity.getTags().contains(name) || (entity.getPersistentData().getInt(cName) & flag) != 0;
    }

    public int getFlag() {
        return flag;
    }
}
