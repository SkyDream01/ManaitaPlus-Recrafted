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
}
