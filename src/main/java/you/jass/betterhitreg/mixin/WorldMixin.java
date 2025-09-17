package you.jass.betterhitreg.mixin;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import you.jass.betterhitreg.util.Settings;

import static you.jass.betterhitreg.hitreg.Hitreg.client;

@Mixin(WorldRenderer.class)
public class WorldMixin {
    @Inject(method = "spawnParticle(Lnet/minecraft/particle/ParticleEffect;ZZDDDDDD)Lnet/minecraft/client/particle/Particle;", at = @At("HEAD"), cancellable = true)
    private void spawnParticle(ParticleEffect parameters, boolean alwaysSpawn, boolean canSpawnOnMinimal, double x, double y, double z, double velocityX, double velocityY, double velocityZ, CallbackInfoReturnable<Particle> cir) {
        if (Settings.isHideCritParticles() && parameters.getType() == ParticleTypes.CRIT) cir.cancel();
        if (Settings.isHideEnchantParticles() && parameters.getType() == ParticleTypes.ENCHANTED_HIT) cir.cancel();
        if (Settings.isHideOtherFights() && client.player != null && client.player.squaredDistanceTo(x, y, z) > 30) cir.cancel();
    }
}