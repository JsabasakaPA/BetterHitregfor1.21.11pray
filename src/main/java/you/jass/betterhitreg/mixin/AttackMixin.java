package you.jass.betterhitreg.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.settings.Commands;
import you.jass.betterhitreg.settings.Settings;
import you.jass.betterhitreg.settings.Toggle;
import you.jass.betterhitreg.util.MultiVersion;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class AttackMixin {
    @Inject(method = "attackEntity", at = @At("HEAD"))
    private static void attack(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (client.player == null || !target.isAlive()) return;
        hitEarly = System.currentTimeMillis() - lastProperAttack <= 450;
        lastAttack = System.currentTimeMillis();
        if (!hitEarly) {
            hitHadCooldown = client.player.getAttackCooldownProgress(0.5f) < 0.9;
            sprinting = client.player.isSprinting();
            falling = client.player.getVelocity().getY() < -0.08;
            holdingSword = client.player.getMainHandStack().getName().getString().toLowerCase().contains("sword");
            enchanted = MultiVersion.hasSharpness();
            targetEntity = target;
            newTarget = lastEntity != target.getId();
            lastEntity = target.getId();
            nextAttack = Hitreg.isToggled() ? System.currentTimeMillis() + Settings.getHitreg() : -1;
            lastProperAttack = System.currentTimeMillis();
            alreadyAnimated = false;
            alreadyKnockbacked = false;
            registered = false;
        } else {
            client.execute(() -> {
                if (!Toggle.SILENCE_SELF.toggled()) {
                    Vec3d location = MultiVersion.getPosition(target);
                    client.world.playSound(client.player, location.x, location.y, location.z, SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, SoundCategory.PLAYERS, 1, 1);
                }
            });
        }
    }
}