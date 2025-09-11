package cn.aph281.ate.ems.object;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.system.MemoryUtil;
import static org.lwjgl.opengl.GL33.*;
import java.io.Closeable;
import java.nio.*;

public class VertBuf implements Closeable {
    public static final int USAGE_STATIC_DRAW = GL_STATIC_DRAW;
    public static final int USAGE_DYNAMIC_DRAW = GL_DYNAMIC_DRAW;
    public static final int USAGE_STREAM_DRAW = GL_STREAM_DRAW;
    
    public int id;
    
    public VertBuf() {
        id = glGenBuffers();    
    }

    public void bind(int target) {
        glBindBuffer(target, id);
    }

    public void upload(ByteBuffer buffer, int usage) {
        int vboPrev = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        buffer.clear();
        glBufferData(GL_ARRAY_BUFFER, buffer, usage);
        glBindBuffer(GL_ARRAY_BUFFER, vboPrev);
    }

    public void upload(ByteBuffer buffer, int size, int usage) {
        int vboPrev = glGetInteger(GL_ARRAY_BUFFER_BINDING);
        glBindBuffer(GL_ARRAY_BUFFER, id);
        buffer.clear();
        nglBufferData(GL_ARRAY_BUFFER, size, MemoryUtil.memAddress0(buffer), usage);
        glBindBuffer(GL_ARRAY_BUFFER, vboPrev);
    }

    @Override
    public void close() {
        if (RenderSystem.isOnRenderThread()) {
            glDeleteBuffers(id);
            id = 0;
        } else {
            RenderSystem.recordRenderCall(this::close);
        }
    }
}