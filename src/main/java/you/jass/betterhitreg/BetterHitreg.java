package you.jass.betterhitreg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Hand;
import you.jass.betterhitreg.hitreg.Hitreg;
import you.jass.betterhitreg.settings.Commands;
import you.jass.betterhitreg.ui.UIScreen;
import you.jass.betterhitreg.util.Render;

public class BetterHitreg implements ModInitializer {

    public static KeyBinding uiKey;
    public static KeyBinding handKey;
    public static int handSwitchCooldown;

    @Override
    public void onInitialize() {
        MinecraftClient client = MinecraftClient.getInstance();
        Hitreg.client = client;

        Commands.initialize();

        ClientTickEvents.START_CLIENT_TICK.register(c -> Hitreg.tick());

        uiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.betterhitreg.open_menu",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_H,
                "category.betterhitreg"
        ));

        handKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.betterhitreg.switch_hand",
                InputUtil.Type.KEYSYM,
                InputUtil.GLFW_KEY_J,
                "category.betterhitreg"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (c.player == null) return;

            while (uiKey.wasPressed() && c.currentScreen == null) {
                c.setScreen(new UIScreen());
            }

            while (handKey.wasPressed() && handSwitchCooldown == 0 && c.currentScreen == null) {
                Hand newHand = c.player.getActiveHand() == Hand.MAIN_HAND
                        ? Hand.OFF_HAND
                        : Hand.MAIN_HAND;

                c.player.swingHand(newHand);
                c.interactionManager.syncSelectedSlot();

                handSwitchCooldown = 5;
            }

            if (handSwitchCooldown > 0) {
                handSwitchCooldown--;
            }
        });

        WorldRenderEvents.END.register(Render::render);
    }
}
