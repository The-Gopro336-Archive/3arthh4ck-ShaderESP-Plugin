package me.earth.shaderesp.module;

import me.earth.earthhack.impl.core.ducks.IMinecraft;
import me.earth.earthhack.impl.core.ducks.entity.IEntityRenderer;
import me.earth.earthhack.impl.core.ducks.render.IRenderItem;
import me.earth.earthhack.impl.event.listeners.ModuleListener;
import me.earth.earthhack.impl.managers.Managers;
import me.earth.earthhack.impl.modules.render.esp.ESP;
import me.earth.earthhack.impl.util.math.RotationUtil;
import me.earth.earthhack.impl.util.render.Interpolation;
import me.earth.earthhack.impl.util.render.RenderUtil;
import me.earth.shaderesp.event.WorldRenderEvent;
import me.earth.shaderesp.util.ESPBuffer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.util.Iterator;

final class ListenerWorldRender extends
        ModuleListener<ShaderESP, WorldRenderEvent>
{
    public ListenerWorldRender(ShaderESP module)
    {
        super(module, WorldRenderEvent.class);
    }

    @Override
    public void invoke(WorldRenderEvent worldRenderEvent)
    {
        if (ShaderESP.ESP.isEnabled())
        {
            return;
        }

        try
        {
            Entity renderEntity = RenderUtil.getEntity();
            ShaderESP.isRenderingItem = true;
            if (module.framebuffer != null)
            {
                module.framebuffer.framebufferClear();
            }

            if (module.framebuffer == null)
            {
                module.framebuffer =
                        new Framebuffer(mc.displayWidth,
                                mc.displayHeight, false);
            }
            else
            {
                if (module.framebuffer.framebufferWidth != mc.displayWidth
                        || module.framebuffer
                        .framebufferHeight != mc.displayHeight)
                {
                    module.framebuffer.unbindFramebuffer();
                    module.framebuffer =
                            new Framebuffer(mc.displayWidth,
                                    mc.displayHeight, false);

                    if (module.espBuffer != null)
                    {
                        module.espBuffer.deleteFramebuffer();
                        module.espBuffer =
                                new ESPBuffer(
                                        module.framebuffer.framebufferTexture,
                                        mc.displayWidth,
                                        mc.displayHeight,
                                        mc.displayWidth / 2,
                                        mc.displayHeight / 2,
                                        1.0f,
                                        3);
                    }
                }
            }

            if (module.espBuffer == null)
            {
                module.espBuffer =
                        new ESPBuffer(module.framebuffer.framebufferTexture,
                                mc.displayWidth,
                                mc.displayHeight,
                                mc.displayWidth / 2,
                                mc.displayHeight / 2,
                                1.0f,
                                3);
            }

            module.espBuffer.setBufferFactor(1.0f);

            float partialTicks =
                    ((IMinecraft) mc).getTimer().renderPartialTicks;

            ((IEntityRenderer) mc.entityRenderer)
                    .invokeSetupCameraTransform(partialTicks, 0);

            RenderHelper.enableStandardItemLighting();
            Vec3d interpolation =
                    Interpolation.interpolateEntity(renderEntity);

            double x = interpolation.x;
            double y = interpolation.y;
            double z = interpolation.z;
            module.framebuffer.bindFramebuffer(false);
            GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            GL11.glClear(16640);

            for (Entity entity : mc.world.loadedEntityList)
            {
                if (!module.isValid(entity)
                        || entity instanceof EntityPlayer
                            && Managers.FRIENDS.contains(entity.getName())
                        || !RotationUtil.inFov(entity))
                {
                    continue;
                }

                mc.entityRenderer.disableLightmap();
                RenderHelper.disableStandardItemLighting();
                Vec3d inter = Interpolation.interpolateEntity(entity);
                double iX = inter.x;
                double iY = inter.y;
                double iZ = inter.z;
                GL11.glPushMatrix();
                Render<Entity> render =
                        mc.getRenderManager().getEntityRenderObject(entity);
                if (render != null)
                {
                    ShaderESP.isRenderingArmor = true;
                    ((IRenderItem) mc.getRenderItem())
                            .setNotRenderingEffectsInGUI(false);
                    ESP.isRendering = true;

                    render.doRender(entity,
                                    iX - x,
                                    iY - y,
                                    iZ - z,
                                    0.0f,
                                    partialTicks);

                    ESP.isRendering = false;
                    ((IRenderItem) mc.getRenderItem())
                            .setNotRenderingEffectsInGUI(true);
                    ShaderESP.isRenderingArmor = false;
                }

                GL11.glPopMatrix();
            }

            mc.entityRenderer.disableLightmap();
            RenderHelper.disableStandardItemLighting();
            mc.entityRenderer.setupOverlayRendering();
            module.espBuffer.setBufferFactor();
            module.framebuffer.unbindFramebuffer();
            mc.getFramebuffer().bindFramebuffer(true);

            //Iterator var21 = Pg.f$a().iterator();
            //TODO: Clean this up some time
            Iterator var21 = mc.world.loadedEntityList.iterator();

            label80:
            while(true) {
                Iterator var25 = var21;

                while(true)
                {
                    Vec3d var26;
                    while(var25.hasNext())
                    {
                        Entity var10 = (Entity)var21.next();
                        if (!module.isValid(var10) || var10 instanceof EntityPlayer && Managers.FRIENDS.contains(var10.getName()) || !RotationUtil.inFov(var10))
                        {
                            continue;
                        }

                        mc.entityRenderer.disableLightmap();
                        RenderHelper.disableStandardItemLighting();
                        var26 = Interpolation.interpolateEntity(var10);//Uh.f$E(var10);
                        double var12 = var26.x;
                        double var14 = var26.y;
                        double var16 = var26.z;
                        GL11.glPushMatrix();
                        Render var18;
                        if ((var18 = mc.getRenderManager().getEntityRenderObject(var10)) != null)
                        {
                            ShaderESP.isRenderingArmor = true;
                            ((IRenderItem) mc.getRenderItem()).setNotRenderingEffectsInGUI(false);
                            ESP.isRendering = true;
                            var18.doRender(var10, var12 - x, var14 - y, var16 - z, 0.0F, partialTicks);
                            ESP.isRendering = false;
                            ((IRenderItem) mc.getRenderItem()).setNotRenderingEffectsInGUI(true);
                            ShaderESP.isRenderingArmor = false;
                        }

                        GL11.glPopMatrix();
                        var25 = var21;
                    }

                    mc.entityRenderer.disableLightmap();
                    RenderHelper.disableStandardItemLighting();
                    mc.entityRenderer.setupOverlayRendering();
                    module.espBuffer.setBufferFactor();
                    module.framebuffer.unbindFramebuffer();
                    mc.getFramebuffer().bindFramebuffer(true);
                    GL11.glPushMatrix();

                    GL11.glColor3f(module.color.getR(),
                                   module.color.getG(),
                                   module.color.getB());

                    GlStateManager.bindTexture(module.espBuffer.getTexture());
                    GL11.glBegin(9);
                    GL11.glTexCoord2d(0.0D, 1.0D);
                    GL11.glVertex2d(0.0D, 0.0D);
                    GL11.glTexCoord2d(0.0D, 0.0D);
                    GL11.glVertex2d(0.0D, mc.displayHeight);
                    GL11.glTexCoord2d(1.0D, 0.0D);
                    GL11.glVertex2d(mc.displayWidth, mc.displayHeight);
                    GL11.glTexCoord2d(1.0D, 0.0D);
                    GL11.glVertex2d(mc.displayWidth, mc.displayHeight);
                    GL11.glTexCoord2d(1.0D, 1.0D);
                    GL11.glVertex2d(mc.displayWidth, 0.0D);
                    GL11.glTexCoord2d(0.0D, 1.0D);
                    GL11.glVertex2d(0.0D, 0.0D);
                    GL11.glEnd();
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();

                    ((IEntityRenderer) mc.entityRenderer).invokeSetupCameraTransform(partialTicks, 0);
                    RenderHelper.enableStandardItemLighting();
                    module.framebuffer.bindFramebuffer(false);
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glClear(16640);
                    Iterator var24;
                    var25 = var24 = mc.world.playerEntities.iterator();//Pg.f$E().iterator();

                    while(var25.hasNext())
                    {
                        EntityPlayer var11 = (EntityPlayer)var24.next();
                        if (!module.isValid(var11) || !RotationUtil.inFov(var11))
                        {
                            var25 = var24;
                        }
                        else
                        {
                            mc.entityRenderer.disableLightmap();
                            RenderHelper.disableStandardItemLighting();
                            var26 = Interpolation.interpolateEntity(var11);//Uh.f$E((Entity)var11);
                            double var13 = var26.x;
                            double var15 = var26.y;
                            double var17 = var26.z;
                            GL11.glPushMatrix();
                            Render var23;
                            if ((var23 =
                                    mc.getRenderManager().getEntityRenderObject(var11)) != null && Managers.FRIENDS.contains(var11))
                            {
                                ShaderESP.isRenderingArmor = true;
                                ((IRenderItem) mc.getRenderItem()).setNotRenderingEffectsInGUI(false);
                                ESP.isRendering = true;
                                var23.doRender(var11, var13 - x, var15 - y, var17 - z, 0.0F, partialTicks);
                                ESP.isRendering = false;
                                ((IRenderItem) mc.getRenderItem()).setNotRenderingEffectsInGUI(true);
                                ShaderESP.isRenderingArmor = false;
                            }

                            GL11.glPopMatrix();
                            var25 = var24;
                        }
                    }

                    mc.entityRenderer.disableLightmap();
                    RenderHelper.disableStandardItemLighting();
                    mc.entityRenderer.setupOverlayRendering();
                    module.espBuffer.setBufferFactor();
                    module.framebuffer.unbindFramebuffer();
                    mc.getFramebuffer().bindFramebuffer(true);
                    GL11.glPushMatrix();
                    GL11.glColor3f(0.27F, 0.7F, 1.0F);
                    GlStateManager.bindTexture(module.espBuffer.getTexture());
                    GL11.glBegin(9);
                    GL11.glTexCoord2d(0.0D, 1.0D);
                    GL11.glVertex2d(0.0D, 0.0D);
                    GL11.glTexCoord2d(0.0D, 0.0D);
                    GL11.glVertex2d(0.0D, mc.displayHeight);
                    GL11.glTexCoord2d(1.0D, 0.0D);
                    GL11.glVertex2d(mc.displayWidth, mc.displayHeight);
                    GL11.glTexCoord2d(1.0D, 0.0D);
                    GL11.glVertex2d(mc.displayWidth, mc.displayHeight);
                    GL11.glTexCoord2d(1.0D, 1.0D);
                    GL11.glVertex2d(mc.displayWidth, 0.0D);
                    GL11.glTexCoord2d(0.0D, 1.0D);
                    GL11.glVertex2d(0.0D, 0.0D);
                    GL11.glEnd();
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glPopMatrix();
                    ShaderESP.isRenderingItem = false;
                    return;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            ESP.isRendering      = false;
            ShaderESP.isRenderingArmor = false;
            ShaderESP.isRenderingItem  = false;
        }
    }

}
