package you.jass.betterhitreg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.option.SimpleOption;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Arm;
import org.lwjgl.glfw.GLFW;
import you.jass.betterhitreg.settings.Commands;
import you.jass.betterhitreg.ui.UIScreen;
import you.jass.betterhitreg.util.Render;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

public class BetterHitreg implements ModInitializer {
    public static KeyBinding uiKey;
    public static KeyBinding handKey;

    @Override
    public void onInitialize() {
        client = MinecraftClient.getInstance();
        Commands.initialize();
        ClientTickEvents.START_CLIENT_TICK.register(client -> tick());

        uiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Open Menu",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "Hitreg"
        ));

        handKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "Switch Hand",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                "Hitreg"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (uiKey.wasPressed() && client.currentScreen == null) client.setScreen(new UIScreen());
            while (handKey.wasPressed()) {
                client.options.getMainArm().setValue(client.options.getMainArm().getValue().getOpposite());
                client.player.setMainArm(client.options.getMainArm().getValue());
                client.options.sendClientSettings();
            }
        });

        WorldRenderEvents.END.register(Render::render);
    }
}