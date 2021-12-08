package me.earth.shaderesp.mixin.minecraft;

import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.shaderesp.module.ShaderESP;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public abstract class MixinLayerArmorBase
{
    @Inject(
            method = "renderEnchantedGlint",
            at = @At("HEAD"),
            cancellable = true)
    private static void renderEnchantedGlintHook(CallbackInfo info)
    {
        if (ShaderESP.isRenderingArmor)
        {
            info.cancel();
        }
    }

}
