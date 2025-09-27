package you.jass.betterhitreg.util;

import net.minecraft.entity.damage.DamageSource;

public class OnlyAnimate extends DamageSource {
    public final DamageSource wrapped;

    public OnlyAnimate(DamageSource wrapped) {
        super(wrapped.getTypeRegistryEntry());
        this.wrapped = wrapped;
    }
}