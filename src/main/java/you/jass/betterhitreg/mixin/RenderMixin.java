package you.jass.betterhitreg.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import you.jass.betterhitreg.util.Settings;

import static you.jass.betterhitreg.hitreg.Hitreg.*;

@Mixin(EntityRenderer.class)
public abstract class RenderMixin {
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private void render(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (!Settings.isHideOtherFights() || client.player == null || entity == null || entity.getId() == lastEntity) return;
        if (client.player.squaredDistanceTo(entity) > 25 && System.currentTimeMillis() - lastAttack < 5000) cir.setReturnValue(false);
    }
}