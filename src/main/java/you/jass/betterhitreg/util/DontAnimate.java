package you.jass.betterhitreg.util;

import net.minecraft.entity.damage.DamageSource;

public class DontAnimate extends DamageSource {
    public final DamageSource wrapped;

    public DontAnimate(DamageSource wrapped) {
        super(wrapped.getTypeRegistryEntry());
        this.wrapped = wrapped;
    }
}