package me.earth.shaderesp.module;

import me.earth.earthhack.impl.event.events.network.WorldClientEvent;
import me.earth.earthhack.impl.event.listeners.ModuleListener;

final class ListenerWorldClient extends
        ModuleListener<ShaderESP, WorldClientEvent.Load>
{
    public ListenerWorldClient(ShaderESP module)
    {
        super(module, WorldClientEvent.Load.class);
    }

    @Override
    public void invoke(WorldClientEvent.Load load)
    {
        if (module.framebuffer != null)
        {
            module.framebuffer.unbindFramebuffer();
        }

        module.framebuffer = null;

        if (module.espBuffer != null)
        {
            module.espBuffer.deleteFramebuffer();
        }

        module.espBuffer = null;
    }

}
