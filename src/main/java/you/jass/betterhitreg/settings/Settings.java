package you.jass.betterhitreg.settings;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public class Settings {
    private static final Path settings = FabricLoader.getInstance().getConfigDir().resolve("hitreg.properties");
    private static final Properties properties = new Properties();
    private static final Properties defaults = new Properties();

    static {
        for (Toggle toggle : Toggle.values()) defaults.setProperty(toggle.key(), String.valueOf(toggle.defaultValue()));

        defaults.setProperty("hitreg", "0");
        defaults.setProperty("tutorial", "true");

        properties.putAll(defaults);
        load();
    }

    public static int getHitreg() {
        return Integer.parseInt(get("hitreg"));
    }
    public static boolean isTutorial() {
        return Boolean.parseBoolean(get("tutorial"));
    }

    public static String get(String key) {
        return properties.getProperty(key);
    }

    public static void set(String key, String value) {
        properties.setProperty(key, value);
        save();
    }

    public static boolean toggle(String key) {
        boolean toggled = !Boolean.parseBoolean(get(key));
        set(key, String.valueOf(toggled));
        return toggled;
    }

    private static void load() {
        if (!Files.exists(settings)) {
            save();
            return;
        }

        try (InputStream input = Files.newInputStream(settings)) {
            properties.load(input);
            for (String key : defaults.stringPropertyNames()) {
                if (!properties.containsKey(key)) properties.setProperty(key, defaults.getProperty(key));
            }
        } catch (IOException e) {
            System.err.println("Couldn't load file: " + e.getMessage());
            properties.putAll(defaults);
        }
    }

    private static void save() {
        try {
            Files.createDirectories(settings.getParent());
            try (OutputStream output = Files.newOutputStream(settings)) {
                properties.store(output, "Hitreg Settings");
            }
        } catch (IOException e) {
            System.err.println("Couldn't save file: " + e.getMessage());
        }
    }
}
