package you.jass.betterhitreg.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.sound.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import you.jass.betterhitreg.util.Settings;

@Mixin(LivingEntity.class)
public abstract class DamageMixin {
    @ModifyVariable(
        method = "onDamaged(Lnet/minecraft/entity/damage/DamageSource;)V",
        at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/entity/LivingEntity;getHurtSound(Lnet/minecraft/entity/damage/DamageSource;)Lnet/minecraft/sound/SoundEvent;"),
        ordinal = 0
    )
    private SoundEvent onDamaged(SoundEvent original, DamageSource damageSource) {
        return Settings.isSilenceThem() ? null : original;
    }
}