package you.jass.betterhitreg.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import you.jass.betterhitreg.settings.Toggle;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

@Mixin(EntityRenderer.class)
public abstract class RenderMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void shouldRender(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!Toggle.HIDE_OTHER_FIGHTS.toggled() || client.player == null || entity == null || entity.getId() == lastEntity) return;
        if (distanceFromPlayer(entity.getPos()) <= 30 || distanceToTarget() > 100 || System.currentTimeMillis() - lastAttack > 5000 || !bothAlive()) return;
        cir.setReturnValue(false);
    }
}