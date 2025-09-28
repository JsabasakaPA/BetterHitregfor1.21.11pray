package you.jass.betterhitreg.mixin;

import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.hitreg.Hit;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.util.MultiVersion;
import you.jass.betterhitreg.util.Scheduler;

import static you.jass.betterhitreg.hitreg.Hitreg.*;
import static you.jass.betterhitreg.util.MultiVersion.message;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class AttackMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private static void attack(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (client.player == null || !(target instanceof LivingEntity) || !target.isAlive() || target.isInvulnerable()) return;
        Hitreg.target = (LivingEntity) target;
        lastAttackLocation = client.player.getPos();

        //hitting before 500ms is too fast to deal damage, lower it by 25 because it's not exact and can be lower
        boolean hitEarly = System.currentTimeMillis() - lastAttack < 475;
        boolean hittingNewTarget = lastTarget != target.getId();
        boolean didSprintReset = sprintWasReset;
        boolean targetHasShield = Hitreg.target.isHolding(Items.SHIELD);

        //make new targets take damage even if the hit was early
        if (!hitEarly || hittingNewTarget) {
            //if the most recent target animation that we received was more recent than the last attack we did
            boolean lastAttackWasAnimated = lastAnimation > lastAttack;

            //account for swapping to a new target which could give the previous target not enough time to take damage
            if (lastAttackWasAnimated || hittingNewTarget) wasGhosted = false;
            else {
                //don't count it if the hit was the first hit on a new target as some players may be invincible
                //also don't count it if the previous hit was ghosted or if they have a shield, since they may be invincible
                if (!wasGhosted && !newTarget && !targetHasShield) {
                    if (Toggle.ALERT_GHOSTS.toggled()) client.execute(() -> message("hit §7was §cghosted", "/hitreg alertGhosts"));
                    last100Regs.addGhost();
                }

                //this tells the current hit that the previous hit was ghosted
                wasGhosted = true;
            }

            lastAttack = System.currentTimeMillis();
            newTarget = hittingNewTarget;
            lastTarget = target.getId();
            hasShield = targetHasShield;
            sprintWasReset = false;
            alreadyAnimated = false;
        }

        if (!Hitreg.isToggled() || !withinFight() || !bothAlive()) return;

        Hit hit = new Hit();
        hit.target = Hitreg.target;
        hit.tooEarlyForDamage = hitEarly;
        hit.tooEarlyForSpecial = client.player.getAttackCooldownProgress(0.5f) <= 0.9f;
        hit.wasSprinting = client.player.isSprinting();
        hit.wasFalling = client.player.getVelocity().getY() < -0.08;
        hit.wasOnGround = client.player.isOnGround();
        hit.wasClimbing = client.player.isClimbing();
        hit.wasTouchingWater = client.player.isTouchingWater();
        hit.wasInVehicle = client.player.hasVehicle();
        hit.wasBlind = client.player.hasStatusEffect(StatusEffects.BLINDNESS);
        hit.wasHoldingSword = client.player.getMainHandStack().getItem().getName().getString().toLowerCase().contains("sword");
        hit.swordHadSharpness = MultiVersion.hasSharpness();
        hit.sprintWasReset = didSprintReset;
        Scheduler.schedule(Settings.getHitreg(), hit::run);
    }
}