package github.com.gengyoubo.common.config;

public class MPGConfigValues {
    public static boolean creative_range_destroy_value = false;
    public static boolean easy_mode_value = false;
    public static boolean helmet_night_vision_value = false;
    public static boolean leggings_invisibility_value = false;
    public static boolean boots_auto_jump_value = false;
    public static int boots_speed_level_value = 1;
    public static int boots_jump_level_value = 1;
    public static int item_drops_doubling_value = 64;
    public static int experience_drops_doubling_value = 4;
    public static int crafting_doubling_value = 64;
    public static int furnace_doubling_value = 64;
    public static int brewing_doubling_value = 64;
    public static int destroy_doubling_value = 64;
    public static int source_doubling_value = 64;

    protected MPGConfigValues() {
    }

    protected static void resetDefaults() {
        creative_range_destroy_value = false;
        easy_mode_value = false;
        helmet_night_vision_value = false;
        leggings_invisibility_value = false;
        boots_auto_jump_value = false;
        boots_speed_level_value = 1;
        boots_jump_level_value = 1;
        item_drops_doubling_value = 64;
        experience_drops_doubling_value = 4;
        crafting_doubling_value = 64;
        furnace_doubling_value = 64;
        brewing_doubling_value = 64;
        destroy_doubling_value = 64;
        source_doubling_value = 64;
    }
}
