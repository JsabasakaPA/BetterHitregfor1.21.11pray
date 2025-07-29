package you.jass.betterhitreg.hitreg;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.util.Settings;

import static you.jass.betterhitreg.util.MultiVersion.message;
import static you.jass.betterhitreg.util.MultiVersion.playParticles;

public class Hitreg {
    public static MinecraftClient client;
    public static Vec3d playKB;
    public static Vec3d playLegacy;
    public static int lastEntity;
    public static Entity targetEntity;
    public static long lastAttack;
    public static long lastAttacked;
    public static long lastKnockback;
    public static long lastAnimation;
    public static long nextAttack = -1;
    public static boolean hitEarly;
    public static boolean sprinting;
    public static boolean falling;
    public static boolean holdingSword;
    public static boolean enchanted;
    public static boolean alreadyAnimated;
    public static boolean alreadyKnockbacked;
    public static boolean wasSprinting;
    public static boolean sprintWasReset;
    public static boolean registered;
    public static boolean wasGhosted;
    public static long lastProperAttack;
    public static boolean tutorialAlreadySeen;

    public static void tick() {
        if (client.player == null || client.world == null) return;
        if (client.player.isSprinting() && !wasSprinting) sprintWasReset = true;
        wasSprinting = client.player.isSprinting();
        if (isToggled() && withinFight() && System.currentTimeMillis() >= nextAttack && nextAttack != -1) run();
        if (!registered && lastProperAttack != 0 && System.currentTimeMillis() - lastProperAttack >= 500) {
            if (Settings.isAlertGhosts()) message("hit §7was §cghosted", "/hitreg alertGhosts");
            registered = true;
            wasGhosted = true;
        }

        if (!tutorialAlreadySeen && Settings.isTutorial()) {
            message("Thanks for using BetterHitreg!", "/hitreg");
            message("use /hitreg to configure", "/hitreg");
            message("(or click on this message)", "/hitreg");
            tutorialAlreadySeen = true;
        }
    }

    public static void run() {
        Entity entity = client.world.getEntityById(lastEntity);
        if (entity == null) return;

        if (!hitEarly && !(sprinting && sprintWasReset) && falling) playParticles("CRIT", entity);

        if (enchanted || Settings.isParticlesEveryHit()) playParticles("ENCHANTED_HIT", entity);

        if (!Settings.isHideAnimations() && !hitEarly) entity.onDamaged(entity.getDamageSources().generic());

        if (Settings.isLegacySounds() && !hitEarly) client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);

        else if (hitEarly) client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 1, 1);

        else if (sprinting && sprintWasReset) {
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 1, 1);
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
        }

        else if (falling) {
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1, 1);
        }

        else if (holdingSword) {
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1, 1);
        }

        else {
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
            client.world.playSound(client.player, entity.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
        }

        nextAttack = -1;
        sprintWasReset = false;
    }

    public static boolean isToggled() {
        return Settings.isToggled() && !(Settings.isSafeRegsOnly() && wasGhosted);
    }

    public static boolean withinFight() {
        if (System.currentTimeMillis() - lastAttack > 500) return false;
        if (targetEntity != null && client.player.squaredDistanceTo(targetEntity) > 25) return false;
        return true;
    }
}