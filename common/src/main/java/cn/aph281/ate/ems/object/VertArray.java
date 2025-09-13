package cn.aph281.ate.ems.object;

import cn.aph281.ate.ems.util.GlStateTracker;
import com.mojang.blaze3d.systems.RenderSystem;
import static org.lwjgl.opengl.GL33.*;
import cn.aph281.ate.ems.prop.*;
import cn.aph281.ate.ems.attr.*;
import java.io.Closeable;

public class VertArray implements Closeable {
    public int id;
    
    public MaterialProp materialProp;
    public VertBuf vertBuf;
    public IndexBuf indexBuf;
    public InstanceBuf instanceBuf;
    public AttrMapping mapping;

    public VertArray() {
        id = glGenVertexArrays();
    }

    public void create(MaterialProp materialProp, VertBuf vertBuf, IndexBuf indexBuf, InstanceBuf instanceBuf, AttrMapping mapping) {
        this.mapping = mapping;
        this.materialProp = materialProp;
        this.vertBuf = vertBuf;
        this.indexBuf = indexBuf;
        this.instanceBuf = instanceBuf;
        int oldVao = glGetInteger(GL_VERTEX_ARRAY_BINDING);
        glBindVertexArray(id);
        glBindBuffer(GL_ARRAY_BUFFER, vertBuf.id);
        mapping.setupAttrsToVao(vertBuf, instanceBuf);
        glBindVertexArray(oldVao);
    }

    public void bind() {
        GlStateTracker.assertProtected();
        glBindVertexArray(id);
    }

    public static void unbind() {
        GlStateTracker.assertProtected();
        glBindVertexArray(0);
    }

    public void draw() {
        if (indexBuf == null || id == 0 || indexBuf.vertexCount == 0) return;
        if (instanceBuf == null) {
            glDrawElements(GL_TRIANGLES, indexBuf.vertexCount, indexBuf.indexType, 0L);
        } else {
            if (instanceBuf.size < 1) return;
            glDrawElementsInstanced(GL_TRIANGLES, indexBuf.vertexCount, indexBuf.indexType, 0L, instanceBuf.size);
        }
    }

    public int getFaceCount() {
        return indexBuf.faceCount * (instanceBuf == null ? 1 : instanceBuf.size);
    }

    @Override
    public void close() {
        if (RenderSystem.isOnRenderThread()) {
            glDeleteVertexArrays(id);
            id = 0;
        } else {
            RenderSystem.recordRenderCall(() -> close());
        }
    }
}