package you.jass.betterhitreg.mixin;

import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import you.jass.betterhitreg.util.Settings;

import java.util.Objects;

@Mixin(SoundSystem.class)
public class SoundMixin {
    @ModifyVariable(method = "play(Lnet/minecraft/client/sound/SoundInstance;)Lnet/minecraft/client/sound/SoundSystem$PlayResult;", at = @At("HEAD"), argsOnly = true)
    private SoundInstance onPlay(SoundInstance sound) {
        if (!Settings.isMuffledHitsounds() || sound == null || sound.getId() == null) return sound;
        if (!sound.getId().getPath().startsWith("entity.player") || Objects.equals(sound.getId().getNamespace(), "betterhitreg")) return sound;
        return new PositionedSoundInstance(
                Identifier.of("betterhitreg", sound.getId().getPath()),
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