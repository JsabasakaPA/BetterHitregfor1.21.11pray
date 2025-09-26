package you.jass.betterhitreg.hitreg;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.Entity;
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
    public static int lastEntity;
    public static Entity targetEntity;
    public static long lastAttack;
    public static long lastAttacked;
    public static long lastAnimation;
    public static boolean hitEarly;
    public static boolean hitHadCooldown;
    public static boolean sprinting;
    public static boolean falling;
    public static boolean holdingSword;
    public static boolean enchanted;
    public static boolean alreadyAnimated;
    public static boolean alreadyKnockbacked;
    public static boolean wasSprinting;
    public static boolean sprintWasReset;
    public static boolean registered;
    public static boolean wasGhosted = true;
    public static boolean newTarget;
    public static long lastProperAttack;
    public static RegQueue last100Regs = new RegQueue(100);
    public static boolean tutorialAlreadySeen;

    public static void tick() {
        if (client.player == null || client.world == null) return;
        if (client.player.isSprinting() && !wasSprinting) sprintWasReset = true;
        wasSprinting = client.player.isSprinting();

        if (!wasGhosted && !registered && withinFight() && bothAlive() && lastProperAttack != 0 && System.currentTimeMillis() - lastProperAttack > 450) {
            if (!newTarget) {
                if (Toggle.ALERT_GHOSTS.toggled()) message("hit §7was §cghosted", "/hitreg alertGhosts");
                last100Regs.addGhost();
            }

            registered = true;
            wasGhosted = true;
        }

        if (!tutorialAlreadySeen && Settings.isTutorial()) {
            message("Thanks for using BetterHitreg!", "/hitreg");
            message("use /hitreg or press " + Commands.getUIKey() + " to configure", "/hitreg");
            message("(you can click on these messages)", "/hitreg");
            tutorialAlreadySeen = true;
        }
    }

    public static void run() {
        Entity entity = client.world.getEntityById(lastEntity);
        if (entity == null) return;

        if (!Toggle.HIDE_ALL_PARTICLES.toggled()) {
            if (!hitEarly && !hitHadCooldown && !(sprinting && sprintWasReset) && falling) playParticles("CRIT", entity);
            if (!Toggle.HIDE_OTHER_PARTICLES.toggled() && (enchanted || Toggle.PARTICLES_EVERY_HIT.toggled())) playParticles("ENCHANTED_HIT", entity);
        }

        if (!Toggle.HIDE_ANIMATIONS.toggled() && !hitEarly) entity.onDamaged(entity.getDamageSources().generic());

        if (!Toggle.SILENCE_SELF.toggled()) {
            Vec3d location = MultiVersion.getPosition(entity);

            if (Toggle.LEGACY_SOUNDS.toggled() && !hitEarly) client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);

            else if (hitHadCooldown) {
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
            }

            else if (sprinting && sprintWasReset) {
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 1, 1);
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
            }

            else if (falling) {
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1, 1);
            }

            else if (holdingSword) {
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1, 1);
            }
        }

        sprintWasReset = false;
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
        if (targetEntity == null) return -1;
        return getPing(targetEntity.getUuid());
    }

    public static boolean isToggled() {
        if (!Toggle.TOGGLE.toggled()) return false;
        if (Toggle.SAFE_REGS_ONLY.toggled() && (newTarget || wasGhosted)) return false;
        return true;
    }

    public static boolean withinFight() {
        return distanceToTarget() <= 30 && bothAlive();
    }

    public static boolean bothAlive() {
        return targetEntity != null && client.player != null && client.player.isAlive() && targetEntity.isAlive();
    }

    public static double distanceToTarget() {
        if (client.player == null || targetEntity == null) return 999;
        return distanceFrom(client.player.getPos(), targetEntity.getPos());
    }

    public static double distanceFromPlayer(Vec3d position) {
        if (client.player == null) return 999;
        return distanceFrom(client.player.getPos(), position);
    }

    public static double distanceFromTarget(Vec3d position) {
        if (targetEntity == null) return 999;
        return distanceFrom(targetEntity.getPos(), position);
    }

    public static double distanceFrom(Vec3d a, Vec3d b) {
        if (a == null || b == null) return 999;
        double dx = a.x - b.x;
        double dz = a.z - b.z;
        return dx * dx + dz * dz;
    }
}