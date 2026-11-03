/*
 * Decompiled with CFR 0.152.
 */
package dev.neonlyrics.mixin;

import dev.neonlyrics.cosmetic.DeathFx;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={EntityRenderer.class})
public abstract class EntityRendererMixin {
    @Inject(method={"shouldRender"}, at={@At(value="HEAD")}, cancellable=true, require=0)
    private void neonlyrics$hideForDeathFx(Entity entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        if (DeathFx.hides(entity)) {
            cir.setReturnValue(false);
        }
    }
}

