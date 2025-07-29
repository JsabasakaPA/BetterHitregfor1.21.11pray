package you.jass.betterhitreg.util;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.*;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static you.jass.betterhitreg.hitreg.Hitreg.client;
import static you.jass.betterhitreg.util.MultiVersion.message;

public class Commands {
    public static void initialize() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registry) ->
                dispatcher.register(ClientCommandManager.literal("hitreg")
                        .then(ClientCommandManager.literal("toggle").executes(context -> toggle("custom hitreg")))
                        .then(ClientCommandManager.literal("alertDelays").executes(context -> toggle("alert delays")))
                        .then(ClientCommandManager.literal("alertGhosts").executes(context -> toggle("alert ghosts")))
                        .then(ClientCommandManager.literal("legacySounds").executes(context -> toggle("1.8 sounds")))
                        .then(ClientCommandManager.literal("hideAnimations").executes(context -> toggle("hide animations")))
                        .then(ClientCommandManager.literal("particlesEveryHit").executes(context -> toggle("particles on every hit")))
                        .then(ClientCommandManager.literal("silenceOtherFights").executes(context -> toggle("silence other fights")))
                        .then(ClientCommandManager.literal("hideOtherFights").executes(context -> toggle("hide other fights")))
                        .then(ClientCommandManager.literal("safeRegsOnly").executes(context -> toggle("safe regs only")))
                        .then(ClientCommandManager.literal("set").then(argument("value", IntegerArgumentType.integer()).executes(context -> set(IntegerArgumentType.getInteger(context, "value")))).executes(context -> set(0)))
                        .executes(context -> guide())));
    }

    public static int guide() {
        message("/hitreg <command> (you can click these messages)", "/hitreg toggle");
        message("custom hitreg: " + (Settings.isToggled() ? "§f" + Settings.getHitreg() + "§7ms" : "§coff"), "/hitreg set 0");
        message("alert delays: " + onOrOff(Settings.isAlertDelays()), "/hitreg alertDelays");
        message("alert ghosts: " + onOrOff(Settings.isAlertGhosts()), "/hitreg alertGhosts");
        message("1.8 sounds: " + onOrOff(Settings.isLegacySounds()), "/hitreg legacySounds");
        message("hide animations: " + onOrOff(Settings.isHideAnimations()), "/hitreg hideAnimations");
        message("particles on every hit: " + onOrOff(Settings.isParticlesEveryHit()), "/hitreg particlesEveryHit");
        message("silence other fights: " + onOrOff(Settings.isSilenceOtherFights()), "/hitreg silenceOtherFights");
        message("hide other fights: " + onOrOff(Settings.isHideOtherFights()), "/hitreg hideOtherFights");
        message("safe regs only: " + onOrOff(Settings.isSafeRegsOnly()) + " §7(first hits " + (Settings.isSafeRegsOnly() ? "will not" : "will") + " use custom hitreg)", "/hitreg safeRegsOnly");
        if (Settings.isTutorial()) Settings.set("tutorial", "false");
        return 1;
    }

    public static int toggle(String setting) {
        String name = switch (setting) {
            case "custom hitreg" -> "toggled";
            case "alert delays" -> "alertDelays";
            case "alert ghosts" -> "alertGhosts";
            case "1.8 sounds" -> "legacySounds";
            case "hide animations" -> "hideAnimations";
            case "particles on every hit" -> "particlesEveryHit";
            case "silence other fights" -> "silenceOtherFights";
            case "hide other fights" -> "hideOtherFights";
            case "safe regs only" -> "safeRegsOnly";
            default -> "";
        };

        boolean toggled = Settings.toggle(name);

        if (name.equals("toggled")) message(setting + " §7is now " + onOrOff(toggled), "/hitreg toggle");
        else message(setting + " §7is now " + onOrOff(toggled), "/hitreg " + name);
        if (name.equals("safeRegsOnly")) message("§7first hits " + (toggled ? "will now" : "will no longer") + " use custom hitreg", "/hitreg safeRegsOnly");

        return 1;
    }

    public static int set(int hitreg) {
        if (hitreg < 0) {
            Settings.set("toggled", "false");
            message("custom hitreg §7is now §coff", "/hitreg toggle");
            return 1;
        }

        Settings.set("hitreg", String.valueOf(hitreg));
        message("hitreg §7set to §f" + hitreg + "§7ms", "/hitreg set 0");
        if (Settings.get("toggled").equals("false")) toggle("custom hitreg");
        return 1;
    }

    public static String onOrOff(boolean setting) {
        return setting ? "§aon§7" : "§coff§7";
    }
}