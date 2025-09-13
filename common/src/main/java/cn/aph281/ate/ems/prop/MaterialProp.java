package cn.aph281.ate.ems.prop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.Minecraft;
import com.mojang.blaze3d.systems.RenderSystem;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import static org.lwjgl.opengl.GL33.*;
import cn.aph281.ate.ems.attr.*;
import cn.aph281.ate.ems.batch.*;
import cn.aph281.ate.ems.model.*;
import cn.aph281.ate.ems.object.*;
import cn.aph281.ate.ems.prop.*;
import cn.aph281.ate.math.*;
import cn.aph281.ate.ems.shader.*;
import cn.aph281.ate.ems.prop.RenderStage.*;

import java.util.*;
import java.nio.*;
import java.io.IOException;
import java.io.DataInputStream;
import java.nio.charset.StandardCharsets;

public class MaterialProp {
    public ResourceLocation texture;
    public RenderStage renderStage = RenderStage.EXTERIOR;
    public AttrState attrState;

    public MaterialProp() {
        attrState = new AttrState();
    }

    public MaterialProp(ResourceLocation texture, RenderStage renderStage) {
        this.texture = texture;
        this.renderStage = renderStage;
        attrState = new AttrState();
    }

    public MaterialProp(ResourceLocation texture, String renderStage) {
        this.texture = texture;
        this.renderStage = RenderStage.parse(renderStage);
        attrState = new AttrState();
    }

    public MaterialProp(ResourceLocation texture, RenderStage renderStage, AttrState attrState) {
        this.texture = texture;
        this.renderStage = renderStage;
        this.attrState = new AttrState(attrState);
    }

    public MaterialProp(MaterialProp other) {
        this.texture = other.texture;
        this.renderStage = other.renderStage;
        this.attrState = new AttrState(other.attrState);
    }

    public MaterialProp(DataInputStream dis) throws IOException {
        int len = dis.readInt();
        String content = new String(dis.readNBytes(len), StandardCharsets.UTF_8);
        JsonObject mtlObj = (JsonObject)new JsonParser().parse(content);
//        this.shaderName = mtlObj.get("shaderName").getAsString();
        this.texture = mtlObj.get("texture").isJsonNull() ? null : new ResourceLocation(mtlObj.get("texture").getAsString());
        this.attrState.color = mtlObj.get("color").isJsonNull() ? null : mtlObj.get("color").getAsInt();
        this.attrState.lightmapUV = mtlObj.get("lightmapUV").isJsonNull() ? null : mtlObj.get("lightmapUV").getAsInt();
/*        this.translucent = mtlObj.has("translucent") && mtlObj.get("translucent").getAsBoolean();
        this.writeDepthBuf = mtlObj.has("writeDepthBuf") && mtlObj.get("writeDepthBuf").getAsBoolean();
        boolean isBillboard = mtlObj.has("billboard") && mtlObj.get("billboard").getAsBoolean();
        attrState.setMatixProcess(VertAttrState.BILLBOARD);
        this.cutoutHack = mtlObj.has("cutoutHack") && mtlObj.get("cutoutHack").getAsBoolean();
        checkShaderName();*/
    }

    public RenderType getRenderType() {
        return renderStage.getRenderType(texture);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj == null) return false;
        if (obj instanceof MaterialProp) {
            MaterialProp other = (MaterialProp) obj;
            return texture.equals(other.texture) && renderStage == other.renderStage && attrState.equals(other.attrState);
        }
        return false;
    }
}