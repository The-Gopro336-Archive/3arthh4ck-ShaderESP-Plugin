package me.earth.shaderesp.util;

import me.earth.earthhack.impl.Earthhack;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

public class ESPBuffer
{
    private static final String MAIN  =
        "#version 120\n" +
        "uniform sampler2D DiffuseSamper;\n" +
        "uniform vec2 TexelSize;\n" +
        "uniform int SampleRadius;\n" +
        "void main() {\n" +
            "vec4 centerCol = texture2D(DiffuseSamper, gl_TexCoord[0].st);\n" +
            "if (centerCol.a != 0.0F) {\n" +
                "gl_FragColor = vec4(0, 0, 0, 0);\n" +
                "return;\n" +
            "}\n" +
            "float closest = SampleRadius * 2.0F + 2.0F;\n" +
            "for (int xo = -SampleRadius; xo <= SampleRadius; xo++) {\n" +
                "for (int yo = -SampleRadius; yo <= SampleRadius; yo++) {\n" +
                    "vec4 currCol = texture2D(DiffuseSamper, gl_TexCoord[0].st"
                        + " + vec2(xo * TexelSize.x, yo * TexelSize.y));\n" +
                    "if (currCol.r != 0.0F || currCol.g != 0.0F"
                        + " || currCol.b != 0.0F || currCol.a != 0.0F) {\n" +
                        "float currentDist = sqrt(xo*xo*1.0f + yo*yo*1.0f);\n" +
                        "if (currentDist < closest) {\n" +
                            "closest = currentDist;\n" +
                        "}\n" +
                    "}\n" +
                "}\n" +
            "}\n" +
            "gl_FragColor = vec4(1, 1, 1, max(0, ((SampleRadius*1.0F)" +
                                " - (closest - 1)) / (SampleRadius*1.0F)));\n" +
        "}";

    private static int mainSource = -1;
    private static int sampleRadius = -1;
    private static int texelSize = -1;
    private static int id = -1;
    private static int diffuseSamper = -1;

    private final int texture;
    private final int width;
    private final int height;
    private final int heightFactor;
    private final int widthFactor;

    private float bufferFactor;
    private int renderBuffer = -1;
    private int genTextures = -1;
    private int bufferPos;
    private int frameBuffer = -1;

    public ESPBuffer(int texture,
                     int width,
                     int height,
                     int widthFactor,
                     int heightFactor,
                     float bufferFactor,
                     int bufferPos)
    {
        this.texture = texture;
        this.width = width;
        this.height = height;
        this.widthFactor = widthFactor;
        this.heightFactor = heightFactor;
        this.bufferFactor = bufferFactor;
        this.bufferPos = bufferPos;
        this.create();
        this.bind();
    }

    public void deleteFramebuffer()
    {
        if (renderBuffer > -1)
        {
            EXTFramebufferObject.glDeleteRenderbuffersEXT(renderBuffer);
        }

        if (frameBuffer > -1)
        {
            EXTFramebufferObject.glDeleteFramebuffersEXT(frameBuffer);
        }

        if (genTextures > -1)
        {
            GL11.glDeleteTextures(genTextures);
        }
    }

    private void create()
    {
        frameBuffer = EXTFramebufferObject.glGenFramebuffersEXT();
        genTextures = GL11.glGenTextures();
        renderBuffer = EXTFramebufferObject.glGenRenderbuffersEXT();
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, genTextures);
        GL11.glTexParameterf(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, 9729.0F);
        GL11.glTexParameterf(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, 9729.0F);
        GL11.glTexParameterf(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, 10496.0F);
        GL11.glTexParameterf(
                GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, 10496.0F);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, 0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, genTextures);

        GL11.glTexImage2D(GL11.GL_TEXTURE_2D,
                0,
                GL11.GL_RGBA8,
                width,
                height,
                0,
                GL11.GL_RGBA,
                GL11.GL_UNSIGNED_BYTE,
                (ByteBuffer) null);

        EXTFramebufferObject.glBindFramebufferEXT(
                EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBuffer);

        EXTFramebufferObject.glFramebufferTexture2DEXT(
                                EXTFramebufferObject.GL_FRAMEBUFFER_EXT,
                                EXTFramebufferObject.GL_COLOR_ATTACHMENT0_EXT,
                                GL11.GL_TEXTURE_2D,
                                genTextures,
                                0);
    }

    public ESPBuffer setBufferPos(int bufferPos)
    {
        this.bufferPos = bufferPos;
        return this;
    }

    private void bind()
    {
        if (id == -1)
        {
            id = ARBShaderObjects.glCreateProgramObjectARB();

            try
            {
                if (mainSource == -1)
                {
                    mainSource = applyShaderSource(MAIN, 35632);
                }
            }
            catch (Exception e)
            {
                id = -1;
                mainSource = -1;
                e.printStackTrace();
            }

            if (id != -1)
            {
                ARBShaderObjects.glAttachObjectARB(id, mainSource);
                ARBShaderObjects.glLinkProgramARB(id);
                if (ARBShaderObjects
                        .glGetObjectParameteriARB(id,
                                ARBShaderObjects.GL_OBJECT_LINK_STATUS_ARB)
                    == 0)
                {
                    Earthhack.getLogger().error(getLogInfo(id) + " : 35714");
                    return;
                }

                ARBShaderObjects.glValidateProgramARB(id);
                if (ARBShaderObjects
                        .glGetObjectParameteriARB(id,
                                ARBShaderObjects.GL_OBJECT_VALIDATE_STATUS_ARB)
                    == 0)
                {
                    Earthhack.getLogger().error(getLogInfo(id) + " : 35715");
                    return;
                }

                ARBShaderObjects.glUseProgramObjectARB(0);
                diffuseSamper = ARBShaderObjects.glGetUniformLocationARB(
                                                        id, "DiffuseSamper");
                texelSize     = ARBShaderObjects.glGetUniformLocationARB(
                                                        id, "TexelSize");
                sampleRadius  = ARBShaderObjects.glGetUniformLocationARB(
                                                        id, "SampleRadius");
            }
        }
    }

    public int getTexture()
    {
        return this.genTextures;
    }

    public void setBufferFactor()
    {
        if (frameBuffer != -1 && renderBuffer != -1 && id != -1)
        {
            EXTFramebufferObject.glBindFramebufferEXT(
                    EXTFramebufferObject.GL_FRAMEBUFFER_EXT, frameBuffer);
            GL11.glClear(16640);
            ARBShaderObjects.glUseProgramObjectARB(id);
            ARBShaderObjects.glUniform1iARB(diffuseSamper, 0);
            GL13.glActiveTexture(33984);
            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
            FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(2);
            floatBuffer.position(0);
            floatBuffer.put(1.0F / (float)width * bufferFactor);
            floatBuffer.put(1.0F / (float)height * bufferFactor);
            floatBuffer.flip();
            ARBShaderObjects.glUniform2ARB(texelSize, floatBuffer);
            IntBuffer values = BufferUtils.createIntBuffer(1);
            values.position(0);
            values.put(bufferPos);
            values.flip();
            ARBShaderObjects.glUniform1ARB(sampleRadius, values);
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_SRC_COLOR);
            GL11.glBegin(GL11.GL_POLYGON);
            GL11.glTexCoord2d(0.0, 1.0);
            GL11.glVertex2d(0.0, 0.0);
            GL11.glTexCoord2d(0.0, 0.0);
            GL11.glVertex2d(0.0, heightFactor * 2);
            GL11.glTexCoord2d(1.0, 0.0);
            GL11.glVertex2d(widthFactor * 2, heightFactor * 2);
            GL11.glTexCoord2d(1.0, 0.0);
            GL11.glVertex2d(widthFactor * 2, heightFactor * 2);
            GL11.glTexCoord2d(1.0, 1.0);
            GL11.glVertex2d(widthFactor * 2, 0.0);
            GL11.glTexCoord2d(0.0, 1.0);
            GL11.glVertex2d(0.0, 0.0);
            GL11.glEnd();
            GL11.glDisable(GL11.GL_BLEND);
            GL11.glPopMatrix();
            ARBShaderObjects.glUseProgramObjectARB(0);
        }
        else
        {
            throw new RuntimeException("Invalid IDs");
        }
    }

    public void setBufferFactor(float bufferFactor)
    {
        this.bufferFactor = bufferFactor;
    }

    public static int applyShaderSource(String source, int shaderType)
    {
        try
        {
            int shader = ARBShaderObjects.glCreateShaderObjectARB(shaderType);

            if (shader == 0)
            {
                return 0;
            }
            else
            {
                ARBShaderObjects.glShaderSourceARB(shader, source);
                ARBShaderObjects.glCompileShaderARB(shader);
                if (ARBShaderObjects
                        .glGetObjectParameteriARB(shader, 35713) == 0)
                {
                    throw new RuntimeException("Error creating shader: "
                                                + getLogInfo(shader));
                }
                else
                {
                    return shader;
                }
            }
        }
        catch (Exception exception)
        {
            ARBShaderObjects.glDeleteObjectARB(0);
            throw exception;
        }
    }

    public static String getLogInfo(int shader)
    {
        return ARBShaderObjects
                .glGetInfoLogARB(shader,
                                 ARBShaderObjects
                                    .glGetObjectParameteriARB(shader, 35716));
    }

}