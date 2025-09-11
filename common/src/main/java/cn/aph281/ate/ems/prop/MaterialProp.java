package cn.aph281.ate.ems.prop;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.google.common.collect.ImmutableMap;
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

    public void setupCompositeState() {
#if MC_VERSION <= "11903"
        RenderSystem.enableTexture();
#endif
        RenderSystem.setShaderTexture(0, texture);


        // HACK: To make cutout transparency on beacon_beam work
        if (renderStage.translucent || renderStage.cutoutHack) {
            RenderSystem.enableBlend(); // TransparentState
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                    GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        } else {
            RenderSystem.disableBlend();
        }
        RenderSystem.enableDepthTest(); // DepthTestState
        RenderSystem.depthFunc(GL33.GL_LEQUAL);
        RenderSystem.enableCull();
        Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer(); // LightmapState
        Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor(); // OverlayState
        RenderSystem.depthMask(renderStage.writeDepthBuf); // WriteMaskState
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