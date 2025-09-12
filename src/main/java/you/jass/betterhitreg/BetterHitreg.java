package you.jass.betterhitreg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import you.jass.betterhitreg.ui.UIScreen;
import you.jass.betterhitreg.util.Commands;

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
    }
}