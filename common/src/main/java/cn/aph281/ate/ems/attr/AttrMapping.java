package cn.aph281.ate.ems.attr;

import com.google.common.collect.ImmutableMap;
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

public class AttrMapping {

    public static final AttrMapping DEFAULT = new AttrMapping(ImmutableMap.of(
        AttrType.POSITION, AttrSrc.VERTEX_BUF,
        AttrType.COLOR, AttrSrc.VERTEX_BUF_OR_GLOBAL,
        AttrType.UV_TEXTURE, AttrSrc.VERTEX_BUF,
        AttrType.UV_OVERLAY, AttrSrc.INSTANCE_BUF,
        AttrType.UV_LIGHTMAP, AttrSrc.INSTANCE_BUF,
        AttrType.NORMAL, AttrSrc.VERTEX_BUF,
        AttrType.MATRIX_MODEL, AttrSrc.INSTANCE_BUF
    ));

    public ImmutableMap<AttrType, AttrSrc> sources;
    public ImmutableMap<AttrType, Integer> offsets;
    public final int strideVertex, strideInstance;
    public final int paddingVertex, paddingInstance;

    public AttrMapping(ImmutableMap<AttrType, AttrSrc> sources) { 
        this.sources = sources;

        int strideVertex = 0, strideInstance = 0;

        var builder = ImmutableMap.builder();
        for (Map.Entry<AttrType, AttrSrc> entry : sources.entrySet()) { 
            switch (entry.getValue()) {
                case VERTEX_BUF:
                case VERTEX_BUF_OR_GLOBAL:
                    builder.put(entry.getKey(), strideVertex);
                    strideVertex += attrType.byteSize;
                    break;
                case INSTANCE_BUF:
                case INSTANCE_BUF_OR_GLOBAL:
                    builder.put(entry.getKey(), strideInstance);
                    strideInstance += attrType.byteSize;
                    break;
            }
        }

        if (strideVertex % 4 != 0) {
            paddingVertex = 4 - strideVertex % 4;
            strideVertex += paddingVertex;
        }
        if (strideInstance % 4 != 0) {
            paddingInstance = 4 - strideInstance % 4;
            strideInstance += paddingInstance;
        }

        this.strideVertex = strideVertex;
        this.strideInstance = strideInstance;

        offsets = builder.build();
    }

    public void setupAttrsToVao(VertBuf vertexBuf, InstanceBuf instanceBuf) {
        for (Map.Entry<AttrType, AttrSrc> entry : sources.entrySet()) {
            AttrType attrType = entry.getKey();
            switch (entry.getValue()) {
                case GLOBAL:
                    attrType.toggleAttrArray(false);
                    break;
                case VERTEX_BUF:
                case VERTEX_BUF_OR_GLOBAL:
                    if (vertexBuf == null) continue;
                    attrType.toggleAttrArray(true);
                    vertexBuf.bind(GL33.GL_ARRAY_BUFFER);
                    attrType.setupAttrPtr(strideVertex, pointers.get(attrType));
                    attrType.setAttrDivisor(0);
                    break;
                case INSTANCE_BUF:
                case INSTANCE_BUF_OR_GLOBAL:
                    if (instanceBuf == null) continue;
                    attrType.toggleAttrArray(true);
                    instanceBuf.bind(GL33.GL_ARRAY_BUFFER);
                    attrType.setupAttrPtr(strideInstance, pointers.get(attrType));
                    attrType.setAttrDivisor(1);
                    break;
            }
        }
    }

    public void writeVertex(ByteBuf buf, Vertex vertex) {
        for (Map.Entry<AttrType, AttrSrc> entry : sources.entrySet()) {
            AttrType attrType = entry.getKey();
            if (!entry.getValue().inVertBuf()) continue;
            switch (attrType) {
                case POSITION:
                    buf.putFloat(vertex.position.x());
                    buf.putFloat(vertex.position.y());
                    buf.putFloat(vertex.position.z());
                    break;
                case COLOR:
                    buf.putInt(vertex.color);
                    break;
                case UV_TEXTURE:
                    buf.putFloat(vertex.u);
                    buf.putFloat(vertex.v);
                    break;
                case NORMAL:
                    vertex.normal.normalize();
                    buf.putFloat(vertex.normal.x);
                    buf.putFloat(vertex.normal.y);
                    buf.putFloat(vertex.normal.z);
                    break;
            }
        }
        for (int i = 0; i < paddingVertex; ++i) buf.put((byte)0);
    }

    public void writeInstanceBuf(ByteBuf buf, AttrState attrState) {
        for (Map.Entry<AttrType, AttrSrc> entry : sources.entrySet()) {
            AttrType attrType = entry.getKey();
            if (!entry.getValue().inInstanceBuf()) continue;
            switch (attrType) {
                case UV_OVERLAY:
                    buf.putInt(attrState.overlayUV != null ? attrState.overlayUV : 0);
                    break;
                case UV_LIGHTMAP:
                    buf.putInt(attrState.lightmapUV != null ? attrState.lightmapUV : 0);
                    break;
                case COLOR:
                    buf.putInt(attrState.color);
                    break;
                case MATRIX_MODEL:
                    FloatBuffer buffer = FloatBuffer.allocate(16);
                    attrState.modelMatrix.store(buffer);
                    buffer.flip();
                    for (int i = 0; i < 16; ++i) buf.putFloat(buffer.get());
                    break;
            }
        }
        for (int i = 0; i < paddingInstance; ++i) buf.put((byte)0);
    }
}