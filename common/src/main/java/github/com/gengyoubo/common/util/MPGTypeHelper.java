package github.com.gengyoubo.common.util;

/** Maps the stored Manaita tier id to its translation-key segment. */
public final class MPGTypeHelper {
    private static final String[] TYPES = {
            "", "wooden.", "stone.", "iron.", "gold.",
            "diamond.", "emerald.", "redstone.", "netherite."
    };

    private MPGTypeHelper() {
    }

    public static String getTypes(int type) {
        return type >= 1 && type < TYPES.length ? TYPES[type] : "";
    }

    /** Maps the stored tier id (0-8) to Minecraft's clamped item-model predicate range. */
    public static float toModelPredicate(int type) {
        return type >= 1 && type < TYPES.length ? type / (float) (TYPES.length - 1) : 0.0F;
    }
}
