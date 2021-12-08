package me.earth.shaderesp;

import me.earth.earthhack.api.plugin.Plugin;
import me.earth.earthhack.api.register.exception.AlreadyRegisteredException;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.shaderesp.module.ShaderESP;

@SuppressWarnings("unused")
public class ShaderESPPlugin implements Plugin
{
    @Override
    public void load()
    {
        try
        {
            Managers.MODULES.register(new ShaderESP());
        }
        catch (AlreadyRegisteredException e)
        {
            e.printStackTrace();
        }
    }

}
