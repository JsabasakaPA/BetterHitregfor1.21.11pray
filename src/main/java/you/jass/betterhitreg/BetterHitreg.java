package you.jass.betterhitreg;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import you.jass.betterhitreg.util.Commands;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

public class BetterHitreg implements ModInitializer {
    @Override
    public void onInitialize() {
        client = MinecraftClient.getInstance();
        Commands.initialize();
        ClientTickEvents.START_CLIENT_TICK.register(client -> tick());
    }
}