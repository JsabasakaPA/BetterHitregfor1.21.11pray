package you.jass.betterhitreg.util;

import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.util.Properties;

import java.nio.file.Files;
import java.nio.file.Path;

public class Settings {
    private static final Path settings = FabricLoader.getInstance().getConfigDir().resolve("hitreg.properties");
    private static final Properties properties = new Properties();
    private static final Properties defaults = new Properties();

    static {
        defaults.setProperty("toggled", "true");
        defaults.setProperty("hitreg", "0");
        defaults.setProperty("alertDelays", "false");
        defaults.setProperty("alertGhosts", "true");
        defaults.setProperty("legacySounds", "false");
        defaults.setProperty("hideCritParticles", "false");
        defaults.setProperty("hideEnchantParticles", "false");
        defaults.setProperty("particlesEveryHit", "false");
        defaults.setProperty("hideAnimations", "false");
        defaults.setProperty("silenceOtherFights", "false");
        defaults.setProperty("silenceSelf", "false");
        defaults.setProperty("silenceThem", "false");
        defaults.setProperty("hideOtherFights", "false");
        defaults.setProperty("muffledHitsounds", "false");
        defaults.setProperty("safeRegsOnly", "true");
        defaults.setProperty("tutorial", "true");
        properties.putAll(defaults);
        load();
    }

    public static int getHitreg() {return Integer.parseInt(get("hitreg"));}
    public static boolean isToggled() {return Boolean.parseBoolean(get("toggled"));}
    public static boolean isAlertDelays() {return Boolean.parseBoolean(get("alertDelays"));}
    public static boolean isAlertGhosts() {return Boolean.parseBoolean(get("alertGhosts"));}
    public static boolean isLegacySounds() {return Boolean.parseBoolean(get("legacySounds"));}
    public static boolean isHideCritParticles() {return Boolean.parseBoolean(get("hideCritParticles"));}
    public static boolean isHideEnchantParticles() {return Boolean.parseBoolean(get("hideEnchantParticles"));}
    public static boolean isParticlesEveryHit() {return Boolean.parseBoolean(get("particlesEveryHit"));}
    public static boolean isHideAnimations() {return Boolean.parseBoolean(get("hideAnimations"));}
    public static boolean isSilenceOtherFights() {return Boolean.parseBoolean(get("silenceOtherFights"));}
    public static boolean isSilenceSelf() {return Boolean.parseBoolean(get("silenceSelf"));}
    public static boolean isSilenceThem() {return Boolean.parseBoolean(get("silenceThem"));}
    public static boolean isHideOtherFights() {return Boolean.parseBoolean(get("hideOtherFights"));}
    public static boolean isMuffledHitsounds() {return Boolean.parseBoolean(get("muffledHitsounds"));}
    public static boolean isSafeRegsOnly() {return Boolean.parseBoolean(get("safeRegsOnly"));}
    public static boolean isTutorial() {return Boolean.parseBoolean(get("tutorial"));}

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
            for (String key : defaults.stringPropertyNames()) if (!properties.containsKey(key)) properties.setProperty(key, defaults.getProperty(key));
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