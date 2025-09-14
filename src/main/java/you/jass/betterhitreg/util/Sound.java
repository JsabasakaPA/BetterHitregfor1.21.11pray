package you.jass.betterhitreg.util;

import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Vec3d;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

public class Sound {
    public PlaySoundS2CPacket packet;
    public String sound;
    public Vec3d location;
    public long timestamp;
    public boolean modern;
    public boolean legacy;
    public boolean processed;

    public Sound(PlaySoundS2CPacket packet) {
        this.packet = packet;
        this.location = new Vec3d(packet.getX(), packet.getY(), packet.getZ());
        this.sound = packet.getSound().getKey().isPresent() ? packet.getSound().getKey().get().toString() : "";
        this.timestamp = System.currentTimeMillis();
        this.modern = packet.getSound().getKey().isPresent() && (packet.getSound().getKey().get().toString().contains("entity.player.hurt") || packet.getSound().getKey().get().toString().contains("entity.player.attack"));
        this.legacy = packet.getSound().getType() == RegistryEntry.Type.DIRECT;
    }

    public boolean withinFight() {
        double distance = client.player != null ? client.player.squaredDistanceTo(location.x, location.y, location.z) : 0;
        return distance <= 30;
    }

    public boolean wasRecent() {
        return distanceFromTimestamp(System.currentTimeMillis()) <= 5;
    }

    public long distanceFromTimestamp(long time) {
        return Math.abs(time - timestamp);
    }

    public boolean wasFromYou() {
        long you = distanceFromTimestamp(lastAnimation);
        long them = distanceFromTimestamp(lastAttacked);
        return you <= 5 && you <= them;
    }

    public boolean wasFromThem() {
        long you = distanceFromTimestamp(lastAnimation);
        long them = distanceFromTimestamp(lastAttacked);
        return them <= 5 && them <= you;
    }
}