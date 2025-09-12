package you.jass.betterhitreg.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.util.Settings;

import static you.jass.betterhitreg.hitreg.Hitreg.*;
import static you.jass.betterhitreg.util.MultiVersion.*;

@Mixin(ClientConnection.class)
public abstract class PacketMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true, require = 1)
    private static void readPacket(Packet packet, PacketListener listener, CallbackInfo ci) {
        if (client.world == null || client.player == null) return;
        boolean isToggled = isToggled();
        boolean withinFight = withinFight();

        if (packet instanceof EntityDamageS2CPacket damagePacket) {
            if (Settings.isHideAnimations()) ci.cancel();

            if (lastEntity == damagePacket.entityId()) {
                long delay = System.currentTimeMillis() - lastProperAttack;
                if (Settings.isAlertDelays() && !alreadyAnimated && delay <= 500) message("hitreg §7was §f" + delay + "§7ms", "/hitreg alertDelays");
                if (wasGhosted || delay <= 500) last100Regs.add(!wasGhosted ? (int) delay : -1);
                lastAnimation = System.currentTimeMillis();
                alreadyAnimated = true;
                registered = true;
                wasGhosted = false;
                if (isToggled && withinFight) ci.cancel();
                if (!isToggled && withinFight && Settings.isParticlesEveryHit()) playParticles("ENCHANTED_HIT", targetEntity);
            }

            else if (client.player != null && client.player.getId() == damagePacket.entityId()) lastAttacked = System.currentTimeMillis();
        }

        else if (packet instanceof EntityAnimationS2CPacket animationPacket) {
            if (lastEntity != getAnimationId(animationPacket)) return;

            //crit particle
            if (animationPacket.getAnimationId() == 4) {
                if (isToggled && withinFight) ci.cancel();
            }

            //enchanted particle
            else if (animationPacket.getAnimationId() == 5) {
                if ((Settings.isParticlesEveryHit() || isToggled) && withinFight) ci.cancel();
            }
        }

        else if (packet instanceof PlaySoundS2CPacket soundPacket && soundPacket.getCategory() == SoundCategory.PLAYERS) {
            boolean isLegacy = soundPacket.getSound().getType() == RegistryEntry.Type.DIRECT;
            boolean isModern = soundPacket.getSound().getKey().isPresent() && (soundPacket.getSound().getKey().get().toString().contains("entity.player.hurt") || soundPacket.getSound().getKey().get().toString().contains("entity.player.attack"));
            if (isModern || isLegacy) {
                Vec3d location = new Vec3d(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ());
                double distance = client.player != null ? client.player.squaredDistanceTo(location.x, location.y, location.z) : 0;

                long timeSinceAttack = System.currentTimeMillis() - lastAnimation;
                long timeSinceAttacked = System.currentTimeMillis() - lastAttacked;
                boolean outsideFight = distance > 30 || (timeSinceAttack > 5 && timeSinceAttacked > 5);
                boolean theyHit = timeSinceAttacked <= timeSinceAttack;
                boolean youHit = timeSinceAttack <= timeSinceAttacked;

                if (isModern && Settings.isLegacySounds() && !soundPacket.getSound().getKey().get().toString().contains("entity.player.hurt")) ci.cancel();
                if (Settings.isSilenceOtherFights() && (!withinFight || outsideFight)) ci.cancel();
                if (withinFight && !outsideFight) {
                    if (youHit && (isToggled || Settings.isSilenceSelf())) ci.cancel();
                    if (theyHit && Settings.isSilenceThem()) ci.cancel();
                }
            }
        }
    }
}
