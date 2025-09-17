package you.jass.betterhitreg;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.*;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import you.jass.betterhitreg.ui.UIScreen;
import you.jass.betterhitreg.util.Commands;
import you.jass.betterhitreg.util.MultiVersion;
import you.jass.betterhitreg.util.Render;

import java.awt.*;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

public class BetterHitreg implements ModInitializer {
    public static KeyBinding UIKey;

    @Override
    public void onInitialize() {
        client = MinecraftClient.getInstance();
        Commands.initialize();
        ClientTickEvents.START_CLIENT_TICK.register(client -> tick());

        UIKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "Hitreg"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (UIKey.wasPressed()) {
                if (client.currentScreen == null) {
                    client.setScreen(new UIScreen());
                }
            }
        });

        WorldRenderEvents.END.register(Render::hitbox);
    }
}