package me.earth.shaderesp.mixin.minecraft;

import me.earth.earthhack.api.event.bus.instance.Bus;
import me.earth.shaderesp.event.WorldRenderEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer
{
    @Shadow
    @Final
    private Minecraft mc;

    @Inject(method = "renderWorld", at = @At("RETURN"))
    private void renderWorldHook(CallbackInfo info)
    {
        final int guiScale = mc.gameSettings.guiScale;
        mc.gameSettings.guiScale = 1;
        Bus.EVENT_BUS.post(new WorldRenderEvent());
        mc.gameSettings.guiScale = guiScale;
    }

}
