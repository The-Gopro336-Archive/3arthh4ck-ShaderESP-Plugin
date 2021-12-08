package me.earth.shaderesp.mixin.optifine;

import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "Config", remap = false)
public abstract class MixinConfig
{
    @Dynamic
    @Inject(method = "isFastRender", at = @At("HEAD"), cancellable = true)
    private static void isFastRenderHook(CallbackInfoReturnable<Boolean> info)
    {
        info.setReturnValue(false);
    }

}
