package me.earth.shaderesp.module;

import me.earth.earthhack.api.cache.ModuleCache;
import me.earth.earthhack.api.module.Module;
import me.earth.earthhack.api.module.util.Category;
import me.earth.earthhack.api.setting.Setting;
import me.earth.earthhack.api.setting.settings.BooleanSetting;
import me.earth.earthhack.api.setting.settings.ColorSetting;
import me.earth.earthhack.impl.modules.Caches;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.util.minecraft.EntityType;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.shaderesp.util.ESPBuffer;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.awt.*;

public class ShaderESP extends Module
{
    protected static final ModuleCache<ESP> ESP = Caches.getModule(ESP.class);

    public static boolean isRenderingItem;
    public static boolean isRenderingArmor;

    protected final ColorSetting color = register(
            new ColorSetting("Color", new Color(255, 255, 255, 255)));
    protected final Setting<Boolean> players  =
            register(new BooleanSetting("Players", true));
    protected final Setting<Boolean> monsters =
            register(new BooleanSetting("Monsters", false));
    protected final Setting<Boolean> animals  =
            register(new BooleanSetting("Animals", false));
    protected final Setting<Boolean> vehicles =
            register(new BooleanSetting("Vehicles", false));
    protected final Setting<Boolean> misc     =
            register(new BooleanSetting("Other", false));

    protected Framebuffer framebuffer;
    protected ESPBuffer espBuffer;

    public ShaderESP()
    {
        super("ShaderESP", Category.Render);
        this.listeners.add(new ListenerWorldClient(this));
        this.listeners.add(new ListenerWorldRender(this));
    }

    @Override
    protected void onDisable()
    {
        isRenderingArmor = false;
        isRenderingItem = false;

        if (framebuffer != null)
        {
            framebuffer.unbindFramebuffer();
        }

        framebuffer = null;

        if (espBuffer != null)
        {
            espBuffer.deleteFramebuffer();
        }

        espBuffer = null;
    }

    protected boolean isValid(Entity entity)
    {
        Entity renderEntity = RenderUtil.getEntity();
        return entity != null
                && !entity.equals(mc.player)
                && !entity.isDead
                && !entity.equals(mc.player.getRidingEntity())
                && !entity.equals(renderEntity)
                && (EntityType.isAnimal(entity) && animals.getValue()
                || EntityType.isMonster(entity) && monsters.getValue()
                || entity instanceof EntityEnderCrystal && misc.getValue()
                || entity instanceof EntityPlayer && players.getValue()
                || EntityType.isVehicle(entity) && vehicles.getValue());
    }

}
