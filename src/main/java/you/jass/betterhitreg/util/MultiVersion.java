package you.jass.betterhitreg.util;

import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.text.*;
import net.minecraft.util.math.Vec3d;

import static you.jass.betterhitreg.hitreg.Hitreg.client;

public class MultiVersion {
    public static Vec3d getPosition(Entity entity) {
        if (client.world == null || entity == null) return Vec3d.ZERO;

        //TODO 1.19.4 - 1.20.6
        //return entity.getLerpedPos(client.getTickDelta());

        //TODO 1.21 - 1.21.4
        return entity.getLerpedPos(client.getRenderTickCounter().getTickDelta(true));

        //TODO 1.21.5 - 1.21.8
        //return entity.getLerpedPos(client.getRenderTickCounter().getTickProgress(true));
    }

    public static void playParticles(String type, Entity entity) {
        if (client.world == null || entity == null) return;
        Vec3d position = getPosition(entity);
        for (int i = 0; i < 20; i++) {
            double x = Math.random() - 0.5;
            double y = Math.random() - 0.5;
            double z = Math.random() - 0.5;
            Vec3d direction = new Vec3d(x, y, z).normalize();

            //TODO 1.19.4
            //DefaultParticleType particle = ParticleTypes.ASH;

            //TODO 1.20.1 - 1.21.8
            SimpleParticleType particle = ParticleTypes.ASH;

            if (type.equals("CRIT")) particle = ParticleTypes.CRIT;
            else if (type.equals("ENCHANTED_HIT")) particle = ParticleTypes.ENCHANTED_HIT;

            //TODO 1.19.4 - 1.21.4
            client.world.addParticle(
                    particle,
                    position.x + x,
                    position.y + (entity.getHeight() / 2) + y,
                    position.z + z,
                    direction.x * 0.5,
                    direction.y * 0.5,
                    direction.z * 0.5);

            //TODO 1.21.5 - 1.21.8
            //client.world.addParticleClient(
            //particle,
            //position.x + x,
            //position.y + (entity.getHeight() / 2) + y,
            //position.z + z,
            //direction.x * 0.5,
            //direction.y * 0.5,
            //direction.z * 0.5);
        }
    }

    public static void message(String message, String command) {
        boolean settingHitreg = command.contains("set");
        Text hoverText = Text.literal("§7Click to " + (settingHitreg ? "set" :  "toggle"));
        if (command.equals("/hitreg")) hoverText = Text.literal("§7Click to configure");

        //TODO 1.19.4 - 1.21.4
        ClickEvent clickEvent = new ClickEvent(!settingHitreg ? ClickEvent.Action.RUN_COMMAND : ClickEvent.Action.SUGGEST_COMMAND, command);
        HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText);

        //TODO 1.21.5 - 1.21.8
        //ClickEvent clickEvent = !settingHitreg ? new ClickEvent.RunCommand(command) : new ClickEvent.SuggestCommand(command);
        //HoverEvent hoverEvent = new HoverEvent.ShowText(hoverText);

        Text text = Text.literal("Hitreg §8|§r " + message).setStyle(Style.EMPTY
                .withColor(TextColor.fromRgb(0xFFD700))
                .withClickEvent(clickEvent)
                .withHoverEvent(hoverEvent));
        if (client.player != null) client.player.sendMessage(text, false);
    }

    public static int getVelocityId(EntityVelocityUpdateS2CPacket packet) {
        //TODO 1.19.4 - 1.20.6
        //return packet.getId();

        //TODO 1.21 - 1.21.8
        return packet.getEntityId();
    }

    public static int getAnimationId(EntityAnimationS2CPacket packet) {
        //TODO 1.19.4 - 1.20.6
        //return packet.getId();

        //TODO 1.21 - 1.21.8
        return packet.getEntityId();
    }
}