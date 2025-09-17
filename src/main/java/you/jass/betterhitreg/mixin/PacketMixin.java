package you.jass.betterhitreg.mixin;

import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.*;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.util.Settings;
import you.jass.betterhitreg.util.Sound;

import java.util.*;

import static you.jass.betterhitreg.hitreg.Hitreg.*;
import static you.jass.betterhitreg.util.MultiVersion.*;

@Mixin(ClientConnection.class)
public abstract class PacketMixin {
    @Inject(method = "handlePacket", at = @At("HEAD"), cancellable = true, require = 1)
    private static void handlePacket(Packet packet, PacketListener listener, CallbackInfo ci) {
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
                processDelayedSounds();
            }

            else if (client.player != null && client.player.getId() == damagePacket.entityId()) {
                lastAttacked = System.currentTimeMillis();
                processDelayedSounds();
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

        else if (packet instanceof PlaySoundS2CPacket soundPacket) {
            boolean vanilla = !(isToggled || Settings.isLegacySounds() || Settings.isSilenceOtherFights() || Settings.isSilenceNonHits() || Settings.isSilenceSelf() || Settings.isSilenceThem());
            if (vanilla) return;

            Sound sound = new Sound(soundPacket);
            if (sound.modern || sound.legacy) {
                if (!processSound(sound)) ci.cancel();
            }
        }
    }

    @Unique
    private static Queue<Sound> delayedSounds = new LinkedList<>();

    @Unique
    private static void processDelayedSounds() {
        if (client.world == null || client.player == null) return;
        while (!delayedSounds.isEmpty()) {
            Sound sound = delayedSounds.poll();
            if (sound.wasRecent()) {
                if (processSound(sound)) {
                    client.execute(() -> client.world.playSound(client.player, sound.packet.getX(), sound.packet.getY(), sound.packet.getZ(), sound.packet.getSound(), sound.packet.getCategory(), sound.packet.getVolume(), sound.packet.getPitch(), sound.packet.getSeed()));
                }
            }
        }
    }

    @Unique
    private static boolean processSound(Sound sound) {
        boolean isToggled = isToggled();
        boolean playerWithinFight = withinFight();
        boolean soundWithinFight = sound.withinFight();

        //block all non hit sounds in the player category if were muting them
        if (sound.packet.getCategory() == SoundCategory.PLAYERS && (!sound.sound.contains("entity.player.attack") && !sound.sound.contains("entity.player.hurt"))) return !Settings.isSilenceNonHits();

        //block all modern attack sounds if legacy sounds are enabled
        if (!sound.legacy && Settings.isLegacySounds() && !sound.sound.contains("hurt")) return false;

        //if the sound happened far away, then block it if were silencing other fights and skip it if were not
        if (!playerWithinFight && !soundWithinFight) return !Settings.isSilenceOtherFights();

        //block nodamage sounds because they don't actually register hits so we don't know who they're from
        if (sound.sound.contains("nodamage")) return false;

        //block the sound based on whether you hit them or they hit you
        if (sound.wasFromYou() && (isToggled || Settings.isSilenceSelf())) return false;
        if (sound.wasFromThem() && Settings.isSilenceThem()) return false;

        //if the sound wasn't from either of you
        if (!sound.wasFromYou() && !sound.wasFromThem()) {
            //delay knockback sounds because for some reason knockback sounds come before hit registration on most servers
            //don't delay it if its already been processed though, or it would just keep delaying indefinitely
            //if it was from either of you, no need to delay it as it came after hit registration, minemenclub sends it after
            if (!sound.processed && sound.sound.contains("knockback")) {
                sound.processed = true;
                delayedSounds.add(sound);
                return false;
            }

            //if it wasn't from either of you and were silencing other fights, silence it
            if (Settings.isSilenceOtherFights()) return false;
        }

        return true;
    }
}