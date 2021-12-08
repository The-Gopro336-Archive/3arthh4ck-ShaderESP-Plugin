package me.earth.shaderesp.mixin.minecraft;

import me.earth.shaderesp.module.ShaderESP;
import net.minecraft.client.renderer.RenderItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem
{
    @Inject(
            method = "renderEffect",
            at = @At("HEAD"),
            cancellable = true)
    private void renderEffectHook(CallbackInfo info)
    {
        if (ShaderESP.isRenderingItem)
        {
            info.cancel();
        }
    }

}
