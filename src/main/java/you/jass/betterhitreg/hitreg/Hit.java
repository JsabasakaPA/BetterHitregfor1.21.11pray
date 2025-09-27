package you.jass.betterhitreg.hitreg;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.util.MultiVersion;
import you.jass.betterhitreg.util.OnlyAnimate;

import static you.jass.betterhitreg.hitreg.Hitreg.client;
import static you.jass.betterhitreg.util.MultiVersion.getPosition;
import static you.jass.betterhitreg.util.MultiVersion.playParticles;

public class Hit {
    public LivingEntity target;
    public boolean tooEarlyForDamage;
    public boolean tooEarlyForSpecial;
    public boolean wasSprinting;
    public boolean wasFalling;
    public boolean wasOnGround;
    public boolean wasClimbing;
    public boolean wasTouchingWater;
    public boolean wasInVehicle;
    public boolean wasBlind;
    public boolean wasHoldingSword;
    public boolean swordHadSharpness;
    public boolean sprintWasReset;

    //run this on the main thread
    public void run() {
        if (target == null) return;

        boolean shouldAnimate = !Toggle.HIDE_ANIMATIONS.toggled() && !tooEarlyForDamage;
        boolean shouldMakeSound = !Toggle.SILENCE_SELF.toggled();
        boolean shouldSoundBeLegacy = Toggle.LEGACY_SOUNDS.toggled();
        boolean shouldSpawnParticles = !Toggle.HIDE_ALL_PARTICLES.toggled();
        boolean shouldKnockback = !tooEarlyForSpecial && wasSprinting && this.sprintWasReset;
        boolean shouldCrit = !tooEarlyForSpecial && !shouldKnockback && wasFalling && !wasOnGround && !wasClimbing && !wasTouchingWater && !wasInVehicle && !wasBlind;
        boolean shouldSweep = !tooEarlyForSpecial && wasHoldingSword && !wasSprinting && wasOnGround;
        boolean shouldPick = !shouldKnockback && !shouldCrit && !shouldSweep;
        boolean shouldFullPick = !tooEarlyForSpecial && shouldPick;
        boolean shouldHalfPick = !shouldFullPick && shouldPick;
        boolean shouldSpawnSharpnessParticles = !Toggle.HIDE_OTHER_PARTICLES.toggled() && (swordHadSharpness || Toggle.PARTICLES_EVERY_HIT.toggled());

        if (shouldAnimate) target.onDamaged(new OnlyAnimate(target.getDamageSources().generic()));

        if (shouldMakeSound) {
            Vec3d location = getPosition(target);

            if (shouldSoundBeLegacy) {
                if (!tooEarlyForDamage) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                }
            } else {
                if (tooEarlyForDamage) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 1, 1);
                }

                else if (shouldKnockback) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 1, 1);
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                }

                else if (shouldCrit) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, SoundCategory.PLAYERS, 1, 1);
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                }

                else if (shouldSweep) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1, 1);
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                }

                else if (shouldFullPick) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                }

                else if (shouldHalfPick) {
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
                }
            }
        }

        if (shouldSpawnParticles) {
            if (shouldCrit) playParticles("CRIT", target);
            if (shouldSpawnSharpnessParticles) playParticles("ENCHANTED_HIT", target);
        }
    }
}
