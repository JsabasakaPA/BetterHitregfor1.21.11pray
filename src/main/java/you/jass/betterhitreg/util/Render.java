package you.jass.betterhitreg.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import java.awt.*;

import static you.jass.betterhitreg.hitreg.Hitreg.client;
import static you.jass.betterhitreg.hitreg.Hitreg.targetEntity;

public class Render {
    public static void hitbox(WorldRenderContext context) {
        if (!Settings.isRenderHitbox() || targetEntity == null || !targetEntity.isAlive()) return;
        box(context, getBoundingBox(targetEntity), 3, new Color(255, 255, 255).getRGB());
        cross(context, getBoundingBox(targetEntity), getClosestPoint(client.player, targetEntity), 3, new Color(255, 0, 0).getRGB());
    }

    public static Box getBoundingBox(Entity entity) {
        Vec3d lerpedPos = MultiVersion.getPosition(entity);
        Vec3d actualPos = entity.getPos();
        Vec3d delta = lerpedPos.subtract(actualPos);
        Box box = entity.getBoundingBox();
        return box.offset(delta);
    }

    public static void box(WorldRenderContext context, Box box, float thickness, int rgba) {
        //corners
        Vec3d c0 = new Vec3d(box.minX, box.minY, box.minZ);
        Vec3d c1 = new Vec3d(box.maxX, box.minY, box.minZ);
        Vec3d c2 = new Vec3d(box.maxX, box.minY, box.maxZ);
        Vec3d c3 = new Vec3d(box.minX, box.minY, box.maxZ);
        Vec3d c4 = new Vec3d(box.minX, box.maxY, box.minZ);
        Vec3d c5 = new Vec3d(box.maxX, box.maxY, box.minZ);
        Vec3d c6 = new Vec3d(box.maxX, box.maxY, box.maxZ);
        Vec3d c7 = new Vec3d(box.minX, box.maxY, box.maxZ);

        //bottom face
        line(context, c0, c1, thickness, rgba);
        line(context, c1, c2, thickness, rgba);
        line(context, c2, c3, thickness, rgba);
        line(context, c3, c0, thickness, rgba);
        //top face
        line(context, c4, c5, thickness, rgba);
        line(context, c5, c6, thickness, rgba);
        line(context, c6, c7, thickness, rgba);
        line(context, c7, c4, thickness, rgba);
        //vertical walls
        line(context, c0, c4, thickness, rgba);
        line(context, c1, c5, thickness, rgba);
        line(context, c2, c6, thickness, rgba);
        line(context, c3, c7, thickness, rgba);
    }

    public static void line(WorldRenderContext ctx, Vec3d p0, Vec3d p1, float pixelThickness, int rgba) {
        Camera cam = ctx.camera();
        Vec3d camPos = cam.getPos();

        MatrixStack ms = new MatrixStack();
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(cam.getPitch()));
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(cam.getYaw() + 180f));
        Matrix4f mat = ms.peek().getPositionMatrix();

        Vec3d a = p0.subtract(camPos);
        Vec3d b = p1.subtract(camPos);

        Vec3d mid = a.add(b).multiply(0.5);
        float distance = (float) mid.length();
        float worldHalfWidth = pixelThickness * distance * 0.001f;

        Vec3d dir  = b.subtract(a).normalize();
        Vec3d view = mid.normalize().multiply(-1);
        Vec3d perpendicular = dir.crossProduct(view).normalize().multiply(worldHalfWidth);

        Vec3d v0 = a.add(perpendicular);
        Vec3d v1 = a.subtract(perpendicular);
        Vec3d v2 = b.subtract(perpendicular);
        Vec3d v3 = b.add(perpendicular);

        MultiVersion.renderLine(mat, v0, v1, v2, v3, rgba);
    }

    public static void cross(WorldRenderContext ctx, Box box, Vec3d center, float pixelThickness, int rgba) {
        //x axis
        line(ctx, new Vec3d(box.minX, center.y, center.z), new Vec3d(box.maxX, center.y, center.z), pixelThickness, rgba);

        //y axis
        line(ctx, new Vec3d(center.x, box.minY, center.z), new Vec3d(center.x, box.maxY, center.z), pixelThickness, rgba);

        //z axis
        line(ctx, new Vec3d(center.x, center.y, box.minZ), new Vec3d(center.x, center.y, box.maxZ), pixelThickness, rgba);
    }

    public static Vec3d getClosestPoint(Entity entity1, Entity entity2) {
        Vec3d eye = MultiVersion.getPosition(entity1).add(0, entity1.getEyeHeight(entity1.getPose()), 0);
        Box box = getBoundingBox(entity2);

        double closestX = clamp(eye.x, box.getMin(Direction.Axis.X), box.getMax(Direction.Axis.X));
        double closestY = clamp(eye.y, box.getMin(Direction.Axis.Y), box.getMax(Direction.Axis.Y));
        double closestZ = clamp(eye.z, box.getMin(Direction.Axis.Z), box.getMax(Direction.Axis.Z));

        return new Vec3d(closestX, closestY, closestZ);
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(value, max));
    }
}
