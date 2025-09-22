package you.jass.betterhitreg.settings;

import you.jass.betterhitreg.util.MultiVersion;

public enum Toggle {
    TOGGLE("toggled", "custom hitreg", true),
    ALERT_DELAYS("alertDelays", "alert delays", false),
    ALERT_GHOSTS("alertGhosts", "alert ghosts", true),
    LEGACY_SOUNDS("legacySounds", "1.8 sounds", false),
    HIDE_ANIMATIONS("hideAnimations", "hide animations", false),
    HIDE_ARMOR("hideArmor", "hide armor", false),
    HIDE_ALL_PARTICLES("hideAllParticles", "hide crit particles", false),
    HIDE_ENCHANT_PARTICLES("hideEnchantParticles", "hide enchant particles", false),
    PARTICLES_EVERY_HIT("particlesEveryHit", "particles on every hit", false),
    SILENCE_OTHER_FIGHTS("silenceOtherFights", "silence other fights", false),
    SILENCE_SELF("silenceSelf", "silence your hits", false),
    SILENCE_THEM("silenceThem", "silence their hits", false),
    SILENCE_NON_HITS("silenceNonHits", "silence non-hits", false),
    HIDE_OTHER_FIGHTS("hideOtherFights", "hide other fights", false),
    MUFFLED_HITSOUNDS("muffledHitsounds", "muffled hitsounds", false),
    RENDER_HITBOX("renderHitbox", "render target hitbox", false),
    RENDER_CROSS("renderCross", "render target cross", false),
    SAFE_REGS_ONLY("safeRegsOnly", "safe regs only", true);

    private final String key;
    private final String label;
    private final boolean defaultValue;

    Toggle(String key, String label, boolean defaultValue) {
        this.key = key;
        this.label = label;
        this.defaultValue = defaultValue;
    }

    public String key() {
        return key;
    }

    public String label() {
        return label;
    }

    public boolean defaultValue() {
        return defaultValue;
    }

    public boolean toggled() {
        return Boolean.parseBoolean(Settings.get(key));
    }

    public boolean toggle() {
        boolean newVal = Settings.toggle(key);
        String command = "/hitreg " + key;
        MultiVersion.message(label + " §7is now " + (newVal ? "§aon§7" : "§coff§7"), command);

        if (this == SAFE_REGS_ONLY) {
            MultiVersion.message("§7first hits " + (newVal ? "will now" : "will no longer") + " use custom hitreg", command);
        }

        return newVal;
    }
}