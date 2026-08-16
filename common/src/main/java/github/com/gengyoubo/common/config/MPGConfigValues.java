package github.com.gengyoubo.common.config;

public class MPGConfigValues {
    public static boolean creative_range_destroy_value = false;
    public static boolean easy_mode_value = false;
    public static int item_drops_doubling_value = 4;
    public static int experience_drops_doubling_value = 4;
    public static int crafting_doubling_value = 64;
    public static int furnace_doubling_value = 64;
    public static int brewing_doubling_value = 64;
    public static int destroy_doubling_value = 4;
    public static int source_doubling_value = 64;

    protected MPGConfigValues() {
    }

    protected static void resetDefaults() {
        creative_range_destroy_value = false;
        easy_mode_value = false;
        item_drops_doubling_value = 4;
        experience_drops_doubling_value = 4;
        crafting_doubling_value = 64;
        furnace_doubling_value = 64;
        brewing_doubling_value = 64;
        destroy_doubling_value = 4;
        source_doubling_value = 64;
    }
}
