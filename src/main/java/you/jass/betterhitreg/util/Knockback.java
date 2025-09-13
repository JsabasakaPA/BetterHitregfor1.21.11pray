package you.jass.betterhitreg.util;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.util.math.Vec3d;

public class Knockback {
    public PlaySoundS2CPacket packet;
    public Vec3d location;
    public long timestamp;

    public Knockback(PlaySoundS2CPacket packet) {
        this.packet = packet;
        this.location = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        timestamp = System.currentTimeMillis();
        }
}