package you.jass.betterhitreg.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
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
                if (Settings.isAlertDelays() && !alreadyAnimated) message("animation §7was §f" + (System.currentTimeMillis() - lastAttack) + "§7ms", "/hitreg alertDelays");
                lastAnimation = System.currentTimeMillis();
                alreadyAnimated = true;
                registered = true;
                wasGhosted = false;
                if (isToggled && withinFight) ci.cancel();
                if (!isToggled() && withinFight() && Settings.isParticlesEveryHit()) playParticles("ENCHANTED_HIT", targetEntity);
            }

            else if (client.player != null && client.player.getId() == damagePacket.entityId()) {
                lastAttacked = System.currentTimeMillis();
            }
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

        else if (packet instanceof PlaySoundS2CPacket soundPacket && soundPacket.getCategory() == SoundCategory.PLAYERS && soundPacket.getSound().getType() == RegistryEntry.Type.REFERENCE) {
            if (soundPacket.getSound().getKey().isPresent() && (soundPacket.getSound().getKey().get().toString().contains("entity.player.hurt") || soundPacket.getSound().getKey().get().toString().contains("entity.player.attack"))) {
                Vec3d location = new Vec3d(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ());
                double distance = client.player != null ? client.player.squaredDistanceTo(location.x, location.y, location.z) : 0;

                if (soundPacket.getSound().getKey().get().toString().contains("player.attack.knockback")) {
                    playKB = location;
                    lastKnockback = System.currentTimeMillis();
                }

                if (Settings.isSilenceOtherFights() && (!withinFight || distance > 25)) ci.cancel();

                else if (isToggled && withinFight && distance <= 25) {
                    if (System.currentTimeMillis() - lastAttacked > 10) ci.cancel();
                    else if (System.currentTimeMillis() - lastKnockback <= 10 && soundPacket.getSound().getKey().get().toString().contains("player.attack.strong")) ci.cancel();
                }

                if (!ci.isCancelled() && Settings.isLegacySounds() && !soundPacket.getSound().getKey().get().toString().contains("entity.player.hurt")) {
                    ci.cancel();
                }
            }
        }

        //legacy server support
        else if (packet instanceof PlaySoundS2CPacket soundPacket && soundPacket.getCategory() == SoundCategory.PLAYERS && soundPacket.getSound().getType() == RegistryEntry.Type.DIRECT) {
            Vec3d location = new Vec3d(soundPacket.getX(), soundPacket.getY(), soundPacket.getZ());
            double distance = client.player != null ? client.player.squaredDistanceTo(location.x, location.y, location.z) : 0;

            if (Settings.isSilenceOtherFights() && (!withinFight || distance > 25)) ci.cancel();
            else if (isToggled && withinFight && distance <= 25 && System.currentTimeMillis() - lastAttacked > 10) ci.cancel();
        }

        //won't work on some servers as they don't send velocity update packets
        else if (packet instanceof EntityVelocityUpdateS2CPacket velocityPacket) {
            if (lastEntity != getVelocityId(velocityPacket)) return;
            if (Settings.isAlertDelays() && !alreadyKnockbacked) message("knockback §7was §f" + (System.currentTimeMillis() - lastAttack) + "§7ms", "/hitreg alertDelays");
            alreadyKnockbacked = true;
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tick(final CallbackInfo ci) {
        if (client.world == null) return;
        if (Settings.isToggled() && !(Settings.isSafeRegsOnly() && wasGhosted) && playKB != null) {
            if (System.currentTimeMillis() - lastAttacked <= 50) {
                client.world.playSound(client.player, playKB.getX(), playKB.getY(), playKB.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, SoundCategory.PLAYERS, 1, 1);
                client.world.playSound(client.player, playKB.getX(), playKB.getY(), playKB.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, SoundCategory.PLAYERS, 1, 1);
            }
            playKB = null;
        }

        if (playLegacy != null) {
            client.world.playSound(client.player, BlockPos.ofFloored(playLegacy), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1, 1);
            playLegacy = null;
        }
    }
}
