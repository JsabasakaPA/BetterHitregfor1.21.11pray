package you.jass.betterhitreg.hitreg;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.settings.Commands;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.util.MultiVersion;
import you.jass.betterhitreg.util.RegQueue;

import java.util.UUID;

import static you.jass.betterhitreg.util.MultiVersion.message;
import static you.jass.betterhitreg.util.MultiVersion.playParticles;

public class Hitreg {
    public static MinecraftClient client;
    public static int lastTarget;
    public static LivingEntity target;
    public static long lastAttack;
    public static long lastAttacked;
    public static long lastAnimation;
    public static boolean alreadyAnimated;
    public static boolean wasSprinting;
    public static boolean sprintWasReset;
    public static boolean wasGhosted = true;
    public static boolean newTarget = true;
    public static boolean hasShield = true;
    public static RegQueue last100Regs = new RegQueue(100);
    public static boolean tutorialAlreadySeen;

    public static void tick() {
        if (client.player == null || client.world == null) return;
        if (client.player.isSprinting() && !wasSprinting) sprintWasReset = true;
        wasSprinting = client.player.isSprinting();

        if (Settings.isTutorial() && !tutorialAlreadySeen) {
            message("Thanks for using BetterHitreg!", "/hitreg");
            message("use /hitreg or press " + Commands.getUIKey() + " to configure", "/hitreg");
            message("(you can click on these messages)", "/hitreg");
            tutorialAlreadySeen = true;
        }
    }

    public static int getPing(UUID uuid) {
        if (client.getNetworkHandler() == null) return -1;
        PlayerListEntry entry = client.getNetworkHandler().getPlayerListEntry(uuid);
        return entry == null ? -1 : entry.getLatency();
    }

    public static int getPlayersPing() {
        if (client.player == null) return -1;
        return getPing(client.player.getUuid());
    }

    public static int getTargetsPing() {
        if (target == null) return -1;
        return getPing(target.getUuid());
    }

    public static boolean isToggled() {
        if (!Toggle.TOGGLE.toggled()) return false;
        if (Toggle.SAFE_REGS_ONLY.toggled() && (newTarget || wasGhosted || hasShield)) return false;
        return true;
    }

    public static boolean withinFight() {
        return distanceToTarget() <= 30 && bothAlive();
    }

    public static boolean bothAlive() {
        return target != null && client.player != null && client.player.isAlive() && target.isAlive();
    }

    public static double distanceToTarget() {
        if (client.player == null || target == null) return 999;
        return distanceFrom(client.player.getPos(), target.getPos());
    }

    public static double distanceFromPlayer(Vec3d position) {
        if (client.player == null) return 999;
        return distanceFrom(client.player.getPos(), position);
    }

    public static double distanceFromTarget(Vec3d position) {
        if (target == null) return 999;
        return distanceFrom(target.getPos(), position);
    }

    public static double distanceFrom(Vec3d a, Vec3d b) {
        if (a == null || b == null) return 999;
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return dx * dx + dz * dz;
    }
}