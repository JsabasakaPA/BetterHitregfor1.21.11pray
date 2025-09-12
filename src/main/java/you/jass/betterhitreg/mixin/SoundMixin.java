package you.jass.betterhitreg.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.*;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import you.jass.betterhitreg.BetterHitreg;
import you.jass.betterhitreg.util.Settings;

import java.util.Objects;

@Mixin(SoundSystem.class)
public class SoundMixin {
    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)V", at = @At("HEAD"), argsOnly = true)
    private SoundInstance onPlay(SoundInstance sound) {
        if (!Settings.isMuffledHitsounds() || sound == null || sound.getId() == null) return sound;
        if (!sound.getId().getPath().startsWith("entity.player") || Objects.equals(sound.getId().getNamespace(), "betterhitreg")) return sound;
        Identifier muffledId;

        //TODO 1.19.4
//        muffledId = new Identifier("betterhitreg", sound.getId().getPath());

        //TODO 1.20 - 1.21.8
        muffledId = Identifier.of("betterhitreg", sound.getId().getPath());

        return new PositionedSoundInstance(
                muffledId,
                SoundCategory.PLAYERS,
                sound.getId().getPath().equals("entity.player.hurt") ? 0.5f : 0.3f,
                1,
                Random.create(),
                sound.isRepeatable(),
                sound.getRepeatDelay(),
                sound.getAttenuationType(),
                sound.getX(),
                sound.getY(),
                sound.getZ(),
                sound.isRelative()
        );
    }
}