package you.jass.betterhitreg.mixin;

import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.util.PacketProcessor;

@Mixin(PlaySoundS2CPacket.class)
public abstract class ServerSoundMixin {
    @Inject(method = "apply(Lnet/minecraft/network/listener/ClientPlayPacketListener;)V", at = @At("HEAD"), cancellable = true)
    private void onApply(ClientPlayPacketListener listener, CallbackInfo ci) {
        if (!PacketProcessor.process((Packet<?>) this)) ci.cancel();
    }
}