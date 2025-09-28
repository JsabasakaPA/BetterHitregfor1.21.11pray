package you.jass.betterhitreg.util;

import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BundleS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.sound.SoundCategory;
import org.spongepowered.asm.mixin.Unique;
import you.jass.betterhitreg.settings.Toggle;

import java.util.LinkedList;
import java.util.Queue;

import static you.jass.betterhitreg.hitreg.Hitreg.alreadyAnimated;
import static you.jass.betterhitreg.hitreg.Hitreg.client;
import static you.jass.betterhitreg.hitreg.Hitreg.isToggled;
import static you.jass.betterhitreg.hitreg.Hitreg.last100Regs;
import static you.jass.betterhitreg.hitreg.Hitreg.lastAnimation;
import static you.jass.betterhitreg.hitreg.Hitreg.lastAttack;
import static you.jass.betterhitreg.hitreg.Hitreg.lastAttacked;
import static you.jass.betterhitreg.hitreg.Hitreg.lastTarget;
import static you.jass.betterhitreg.hitreg.Hitreg.target;
import static you.jass.betterhitreg.hitreg.Hitreg.withinFight;
import static you.jass.betterhitreg.util.MultiVersion.*;

public class PacketProcessor {
    public static boolean process(Packet packet) {
        if (client.world == null || client.player == null) return true;

        if (packet instanceof EntityDamageS2CPacket damagePacket) {
            if (lastTarget == damagePacket.entityId()) {
                boolean isToggled = isToggled();
                boolean withinFight = withinFight();
                long delay = System.currentTimeMillis() - lastAttack;
                if (Toggle.ALERT_DELAYS.toggled() && !alreadyAnimated && delay <= 500) client.execute(() -> message("hitreg §7was §f" + delay + "§7ms", "/hitreg alertDelays"));
                if (delay <= 500) last100Regs.addDelay((int) delay);
                lastAnimation = System.currentTimeMillis();
                alreadyAnimated = true;
                if (!isToggled && withinFight && Toggle.PARTICLES_EVERY_HIT.toggled()) client.execute(() -> playParticles("ENCHANTED_HIT", target));
                processDelayedSounds();
            }

            else if (client.player != null && client.player.getId() == damagePacket.entityId()) {
                lastAttacked = System.currentTimeMillis();
                processDelayedSounds();
            }
        }

        else if (packet instanceof EntityAnimationS2CPacket animationPacket) {
            if (lastTarget != getAnimationId(animationPacket)) return true;
            boolean isToggled = isToggled();
            boolean withinFight = withinFight();

            //crit particle
            if (animationPacket.getAnimationId() == 4) {
                if (isToggled && withinFight) return false;
            }

            //enchanted particle
            else if (animationPacket.getAnimationId() == 5) {
                if ((Toggle.PARTICLES_EVERY_HIT.toggled() || isToggled) && withinFight) return false;
            }
        }

        else if (packet instanceof PlaySoundS2CPacket soundPacket) {
            boolean vanilla = !(isToggled() || Toggle.LEGACY_SOUNDS.toggled() || Toggle.SILENCE_OTHER_FIGHTS.toggled() || Toggle.SILENCE_SELF.toggled() || Toggle.SILENCE_THEM.toggled());
            if (vanilla) return true;

            Sound sound = new Sound(soundPacket);
            if (sound.modern || sound.legacy) {
                return processSound(sound);
            }
        }

        return true;
    }

    private static final Queue<Sound> delayedSounds = new LinkedList<>();

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

    private static boolean processSound(Sound sound) {
        boolean isToggled = isToggled();
        boolean playerWithinFight = withinFight();
        boolean soundWithinFight = sound.withinFight();

        //block all non hit sounds in the player category if were muting them
        if (sound.packet.getCategory() == SoundCategory.PLAYERS && (!sound.sound.contains("entity.player.attack") && !sound.sound.contains("entity.player.hurt"))) return !Toggle.SILENCE_NON_HITS.toggled();

        //block all modern attack sounds if legacy sounds are enabled
        if (!sound.legacy && Toggle.LEGACY_SOUNDS.toggled() && !sound.sound.contains("hurt")) return false;

        //if the sound happened far away, then block it if were silencing other fights and skip it if were not
        if (!playerWithinFight && !soundWithinFight) return !Toggle.SILENCE_OTHER_FIGHTS.toggled();

        //block nodamage sounds because they don't actually register hits so we don't know who they're from
        if (sound.sound.contains("nodamage")) return false;

        //block the sound based on whether you hit them or they hit you
        if (sound.wasFromYou() && (isToggled || Toggle.SILENCE_SELF.toggled())) return false;
        if (sound.wasFromThem() && Toggle.SILENCE_THEM.toggled()) return false;

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
            if (Toggle.SILENCE_OTHER_FIGHTS.toggled()) return false;
        }

        return true;
    }
}
