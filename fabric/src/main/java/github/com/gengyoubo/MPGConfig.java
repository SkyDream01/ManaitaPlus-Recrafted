package github.com.gengyoubo;

import github.com.gengyoubo.common.config.MPGConfigValues;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class MPGConfig extends MPGConfigValues {
    private MPGConfig() {
    }

    public static void load() {
        initDefaults();
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve("manaita_plus_legacy-common.toml");
        ensureConfigExists(configPath);
        if (!Files.exists(configPath)) {
            MPG.LOGGER.warn("Config file {} does not exist; using defaults", configPath);
            return;
        }

        try {
            List<String> lines = Files.readAllLines(configPath, StandardCharsets.UTF_8);
            for (String rawLine : lines) {
                String line = rawLine.trim();
                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                int separator = line.indexOf('=');
                if (separator < 0) {
                    continue;
                }

                String key = line.substring(0, separator).trim();
                String value = line.substring(separator + 1).trim();
                applyValue(key, value);
            }
        } catch (IOException e) {
            MPG.LOGGER.error("Failed to read config {}; using defaults", configPath, e);
        }
    }

    public static void initDefaults() {
        resetDefaults();
    }

    private static void ensureConfigExists(Path configPath) {
        if (Files.exists(configPath)) {
            return;
        }

        try {
            Files.createDirectories(configPath.getParent());
            try (InputStream inputStream = MPGConfig.class.getClassLoader().getResourceAsStream("defaultconfigs/manaita_plus_legacy-common.toml")) {
                if (inputStream == null) {
                    MPG.LOGGER.warn("Default config template not found; using hardcoded defaults");
                    return;
                }
                Files.copy(inputStream, configPath);
            }
        } catch (IOException e) {
            MPG.LOGGER.error("Failed to create config file {}", configPath, e);
        }
    }

    private static void applyValue(String key, String value) {
        switch (key) {
            case "creative_range_destroy" -> creative_range_destroy_value = Boolean.parseBoolean(value);
            case "easy_mode" -> easy_mode_value = Boolean.parseBoolean(value);
            case "item_drops_doubling_value" -> item_drops_doubling_value = parseInt(value, item_drops_doubling_value);
            case "experience_drops_doubling_value" -> experience_drops_doubling_value = parseInt(value, experience_drops_doubling_value);
            case "crafting_doubling_value" -> crafting_doubling_value = parseInt(value, crafting_doubling_value);
            case "furnace_doubling_value" -> furnace_doubling_value = parseInt(value, furnace_doubling_value);
            case "brewing_doubling_value" -> brewing_doubling_value = parseInt(value, brewing_doubling_value);
            case "destroy_doubling_value" -> destroy_doubling_value = parseInt(value, destroy_doubling_value);
            case "source_doubling_value" -> source_doubling_value = parseInt(value, source_doubling_value);
            default -> {
            }
        }
    }

    private static int parseInt(String value, int fallback) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }
}
