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

    public static final AttrMapping DEFAULT = new AttrMapping(ImmutableMap.<AttrType, AttrSrc>builder()
        .put(AttrType.POSITION, AttrSrc.VERTEX_BUF)
        .put(AttrType.COLOR, AttrSrc.VERTEX_BUF_OR_GLOBAL)
        .put(AttrType.UV_TEXTURE, AttrSrc.VERTEX_BUF)
        .put(AttrType.UV_OVERLAY, AttrSrc.INSTANCE_BUF)
        .put(AttrType.UV_LIGHTMAP, AttrSrc.INSTANCE_BUF)
        .put(AttrType.NORMAL, AttrSrc.VERTEX_BUF)
        .put(AttrType.MATRIX_MODEL, AttrSrc.INSTANCE_BUF)
        .build()
    );

    public ImmutableMap<AttrType, AttrSrc> sources;
    public ImmutableMap<AttrType, Integer> pointers;
    public final int strideVertex, strideInstance;
    public final int paddingVertex, paddingInstance;

    public AttrMapping(ImmutableMap<AttrType, AttrSrc> sources) { 
        this.sources = sources;

        int strideVertex = 0, strideInstance = 0;

        var builder = ImmutableMap.<AttrType, Integer>builder();
        for (Map.Entry<AttrType, AttrSrc> entry : sources.entrySet()) { 
            switch (entry.getValue()) {
                case VERTEX_BUF:
                case VERTEX_BUF_OR_GLOBAL:
                    builder.put(entry.getKey(), strideVertex);
                    strideVertex += entry.getKey().byteSize;
                    break;
                case INSTANCE_BUF:
                case INSTANCE_BUF_OR_GLOBAL:
                    builder.put(entry.getKey(), strideInstance);
                    strideInstance += entry.getKey().byteSize;
                    break;
            }
        }

        if (strideVertex % 4 != 0) {
            paddingVertex = 4 - strideVertex % 4;
            strideVertex += paddingVertex;
        } else paddingVertex = 0;
        if (strideInstance % 4 != 0) {
            paddingInstance = 4 - strideInstance % 4;
            strideInstance += paddingInstance;
        } else paddingInstance = 0;

        this.strideVertex = strideVertex;
        this.strideInstance = strideInstance;

        pointers = builder.build();
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
                    vertexBuf.bind(GL_ARRAY_BUFFER);
                    attrType.setupAttrPtr(strideVertex, pointers.get(attrType));
                    attrType.setAttrDivisor(0);
                    break;
                case INSTANCE_BUF:
                case INSTANCE_BUF_OR_GLOBAL:
                    if (instanceBuf == null) continue;
                    attrType.toggleAttrArray(true);
                    instanceBuf.bind(GL_ARRAY_BUFFER);
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
                    buf.writeFloat(vertex.position.x());
                    buf.writeFloat(vertex.position.y());
                    buf.writeFloat(vertex.position.z());
                    break;
                case COLOR:
                    buf.writeInt(vertex.color);
                    break;
                case UV_TEXTURE:
                    buf.writeFloat(vertex.u);
                    buf.writeFloat(vertex.v);
                    break;
                case NORMAL:
                    vertex.normal.normalize();
                    buf.writeFloat(vertex.normal.x());
                    buf.writeFloat(vertex.normal.y());
                    buf.writeFloat(vertex.normal.z());
                    break;
            }
        }
        for (int i = 0; i < paddingVertex; ++i) buf.writeByte(0);
    }

    public void writeInstanceBuf(ByteBuf buf, AttrState attrState) {
        for (Map.Entry<AttrType, AttrSrc> entry : sources.entrySet()) {
            AttrType attrType = entry.getKey();
            if (!entry.getValue().inInstanceBuf()) continue;
            switch (attrType) {
                case UV_OVERLAY:
                    buf.writeInt(attrState.overlayUV != null ? attrState.overlayUV : 0);
                    break;
                case UV_LIGHTMAP:
                    buf.writeInt(attrState.lightmapUV != null ? attrState.lightmapUV : 0);
                    break;
                case COLOR:
                    buf.writeInt(attrState.color);
                    break;
                case MATRIX_MODEL:
                    FloatBuffer buffer = FloatBuffer.allocate(16);
                    attrState.modelMatrix.store(buffer);
                    buffer.flip();
                    for (int i = 0; i < 16; ++i) buf.writeFloat(buffer.get());
                    break;
            }
        }
        for (int i = 0; i < paddingInstance; ++i) buf.writeByte(0);
    }
}
