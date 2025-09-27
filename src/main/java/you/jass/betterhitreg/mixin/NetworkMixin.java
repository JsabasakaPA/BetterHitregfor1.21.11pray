package you.jass.betterhitreg.mixin;

import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.util.DontAnimate;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class NetworkMixin {
    @Redirect(method = "onEntityDamage(Lnet/minecraft/network/packet/s2c/play/EntityDamageS2CPacket;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;onDamaged(Lnet/minecraft/entity/damage/DamageSource;)V"))
    private void onEntityDamage(Entity instance, DamageSource damageSource) {
        if (Hitreg.target != null && lastTarget == instance.getId() && isToggled() && withinFight()) instance.onDamaged(new DontAnimate(damageSource));
        else instance.onDamaged(damageSource);
    }
}