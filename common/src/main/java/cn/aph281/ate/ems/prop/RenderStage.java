package cn.aph281.ate.ems.prop;

import net.minecraft.resources.ResourceLocation;
import cn.aph281.ate.ems.shader.BlazeRenderType;
import net.minecraft.client.renderer.RenderType;


public enum RenderStage {
    EXTERIOR("rendertype_entity_cutout", false, true, false, null),
    EXTERIORTRANSLUCENT("rendertype_entity_translucent_cull", true, true, false, null),
    INTERIOR("rendertype_entity_cutout", false, true, false, 15 << 4 | 15 << 20),
    INTERIORTRANSLUCENT("rendertype_entity_translucent_cull", true, true, false, 15 << 4 | 15 << 20),
    LIGHT("rendertype_beacon_beam", false, true, true, null),
    LIGHTTRANSLUCENT("rendertype_beacon_beam", true, false, false, null),;

    public final String shaderName;
    public final boolean translucent;
    public final boolean writeDepthBuf;
    public final boolean cutoutHack;
    public final Integer fixedLightUV;
    public final RenderBatch renderBatch;

    RenderStage(String shaderName, boolean translucent, boolean writeDepthBuf, boolean cutoutHack, Integer fixedLightUV) {
        this.shaderName = shaderName;
        this.translucent = translucent;
        this.writeDepthBuf = writeDepthBuf;
        this.cutoutHack = cutoutHack;
        this.fixedLightUV = fixedLightUV;
        if (translucent) renderBatch = RenderBatch.THIRD;
        else if (cutoutHack) renderBatch = RenderBatch.SECOND;
        else renderBatch = RenderBatch.FIRST;
    }

    public static RenderStage parse(String name) {
        return valueOf(name.toUpperCase());
    }

    public static final ResourceLocation WHITE_TEXTURE_LOCATION = new ResourceLocation("minecraft:textures/misc/white.png");

    public RenderType getRenderType(ResourceLocation textureToUse) {
        textureToUse = textureToUse == null ? WHITE_TEXTURE_LOCATION : textureToUse;
        switch (this) {
            default:
            case EXTERIOR:
            case INTERIOR:
                return BlazeRenderType.entityCutout(textureToUse);
            case EXTERIORTRANSLUCENT: 
            case INTERIORTRANSLUCENT:
                return BlazeRenderType.entityTranslucentCull(textureToUse);
            case LIGHT:
            case LIGHTTRANSLUCENT:
                return BlazeRenderType.beaconBeam(textureToUse, translucent);
        }
    }

    public static enum RenderBatch {
        FIRST, SECOND, THIRD;
    }
}