package you.jass.betterhitreg.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import you.jass.betterhitreg.util.Settings;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

@Mixin(PlayerInteractEntityC2SPacket.class)
public abstract class AttackMixin {
    @Inject(method = "attack", at = @At("HEAD"))
    private static void attack(Entity entity, boolean playerSneaking, CallbackInfoReturnable<PlayerInteractEntityC2SPacket> cir) {
        if (client.player == null || !entity.isAlive()) return;
        hitEarly = System.currentTimeMillis() - lastProperAttack < 500;
        sprinting = client.player.isSprinting();
        falling = client.player.getVelocity().getY() < -0.08;
        holdingSword = client.player.getMainHandStack().getName().getString().toLowerCase().contains("sword");
        enchanted = client.player.getMainHandStack().hasEnchantments();
        lastEntity = entity.getId();
        targetEntity = entity;
        lastAttack = System.currentTimeMillis();
        nextAttack = lastAttack + Settings.getHitreg();
        if (!hitEarly) {
            newTarget = lastEntity != entity.getId();
            lastProperAttack = System.currentTimeMillis();
            alreadyAnimated = false;
            alreadyKnockbacked = false;
            registered = false;
        }
    }
}