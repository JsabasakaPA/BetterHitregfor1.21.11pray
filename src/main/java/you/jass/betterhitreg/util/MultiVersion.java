package you.jass.betterhitreg.util;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.text.*;
import net.minecraft.util.math.Vec3d;
import you.jass.betterhitreg.ui.UIUtils;

import java.awt.*;

import static you.jass.betterhitreg.hitreg.Hitreg.client;

public class MultiVersion {
    public static double getVersion() {
        //TODO 1.19.4
        //return 19.4;
        //TODO 1.20
        //return 20;
        //TODO 1.20.1
        //return 20.1;
        //TODO 1.20.2
        //return 20.2;
        //TODO 1.20.3
        //return 20.3;
        //TODO 1.20.4
        //return 20.4;
        //TODO 1.20.5
        //return 20.5;
        //TODO 1.20.6
        //return 20.6;
        //TODO 1.21
        return 21;
        //TODO 1.21.1
        //return 21.1;
        //TODO 1.21.2
        //return 21.2;
        //TODO 1.21.3
        //return 21.3;
        //TODO 1.21.4
        //return 21.4;
        //TODO 1.21.5
        //return 21.5;
        //TODO 1.21.6
        //return 21.6;
        //TODO 1.21.7
        //return 21.7;
        //TODO 1.21.8
        //return 21.8;
    }

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

    public static int getAnimationId(EntityAnimationS2CPacket packet) {
        //TODO 1.19.4 - 1.20.6
        //return packet.getId();

        //TODO 1.21 - 1.21.8
        return packet.getEntityId();
    }

    public static void drawRectangle(Object renderer, int x, int y, int w, int h, Color c) {
        if (w <= 0 || h <= 0 || c == null) return;
        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //DrawableHelper.fill(ms, x, y, x + w, y + h, c.getRGB());

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        ctx.fill(x, y, x + w, y + h, c.getRGB());
    }

    public static void drawGradientRectangle(Object renderer, int x, int y, int w, int h, Color start, Color end) {
        if (w <= 0 || h <= 0 || start == null || end == null) return;
        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //for (int i = 0; i < h; i++) {
        //float t = (h > 1) ? (float) i / (h - 1) : 0f;
        //Color blended = UIUtils.blend(start, end, t);
        //DrawableHelper.fill(ms, x, y + i, x + w, y + i + 1, blended.getRGB());
        //}

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        for (int i = 0; i < h; i++) {
            float t = (h > 1) ? (float) i / (h - 1) : 0f;
            Color blended = UIUtils.blend(start, end, t);
            ctx.fill(x, y + i, x + w, y + i + 1, blended.getRGB());
        }
    }

    public static void drawHorizontalGradient(Object renderer, int x, int y, int w, int h, Color leftColor, Color rightColor) {
        if (w <= 0 || h <= 0 || leftColor == null || rightColor == null) return;
        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //for (int i = 0; i < w; i++) {
        //float t = (w > 1) ? (float) i / (w - 1) : 0f;
        //Color blended = UIUtils.blend(leftColor, rightColor, t);
        //DrawableHelper.fill(ms, x + i, y, x + i + 1, y + h, blended.getRGB());
        //}

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        for (int i = 0; i < w; i++) {
            float t = (w > 1) ? (float) i / (w - 1) : 0f;
            Color blended = UIUtils.blend(leftColor, rightColor, t);
            ctx.fill(x + i, y, x + i + 1, y + h, blended.getRGB());
        }
    }

    public static void drawBorder(Object renderer, int x, int y, int w, int h, Color c) {
        if (w <= 0 || h <= 0 || c == null) return;

        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //DrawableHelper.drawBorder(ms, x, y, w, h, c.getRGB());

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        ctx.drawBorder(x, y, w, h, c.getRGB());
    }

    public static void drawGradientBorder(Object renderer, int x, int y, int w, int h, Color start, Color end) {
        if (w <= 0 || h <= 0 || start == null || end == null) return;

        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //DrawableHelper.enableScissor(x, y, x + w, y + 1);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();
        //DrawableHelper.enableScissor(x, y, x + 1, y + h);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();
        //DrawableHelper.enableScissor(x, y + h - 1, x + w, y + h);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();
        //DrawableHelper.enableScissor(x + w - 1, y, x + w, y + h);
        //drawGradientRectangle(ms, x, y, w, h, start, end);
        //DrawableHelper.disableScissor();

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        ctx.enableScissor(x, y, x + w, y + 1);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
        ctx.enableScissor(x, y, x + 1, y + h);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
        ctx.enableScissor(x, y + h - 1, x + w, y + h);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
        ctx.enableScissor(x + w - 1, y, x + w, y + h);
        drawGradientRectangle(ctx, x, y, w, h, start, end);
        ctx.disableScissor();
    }

    public static void drawText(Object renderer, TextRenderer tr, String s, int x, int y, Color c, boolean center) {
        if (s == null || tr == null || c == null) return;
        if (center) x -= tr.getWidth(s) / 2;
        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //tr.drawWithShadow(ms, s, x, y, c.getRGB());

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        ctx.drawTextWithShadow(tr, s, x, y, c.getRGB());
    }

    public static void drawGradientText(Object renderer, TextRenderer tr, String s, int x, int y, Color start, Color end, boolean center) {
        if (s == null || tr == null || start == null || end == null) return;
        if (center) x -= tr.getWidth(s) / 2;
        //TODO 1.19.4
        //MatrixStack ms = (MatrixStack) renderer;
        //int last = s.length() - 1;
        //int cx = x;
        //for (int i = 0; i <= last; i++) {
        //float t = (last > 0) ? (float) i / last : 0f;
        //Color col = UIUtils.blend(start, end, t);
        //String ch = s.substring(i, i + 1);
        //tr.drawWithShadow(ms, ch, cx, y, col.getRGB());
        //cx += tr.getWidth(ch);
        //}

        //TODO 1.20 - 1.21.8
        DrawContext ctx = (DrawContext) renderer;
        int last = s.length() - 1;
        int cx = x;
        for (int i = 0; i <= last; i++) {
            float t = (last > 0) ? (float) i / last : 0f;
            Color col = UIUtils.blend(start, end, t);
            String ch = s.substring(i, i + 1);
            ctx.drawTextWithShadow(tr, ch, cx, y, col.getRGB());
            cx += tr.getWidth(ch);
        }
    }
}