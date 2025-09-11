package cn.aph281.ate.ems.model;

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

public class RawMesh { 
    public MaterialProp materialProp;
    public List<Vertex> vertices;
    public List<Face> faces;

    public RawMesh() {
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    public RawMesh(List<Vertex> vertices, List<Face> faces) {
        this.vertices = new ArrayList<>(vertices);
        this.faces = new ArrayList<>(faces);
    }

    public RawMesh(RawMesh mesh) {
        this(mesh.vertices, mesh.faces);
    }

    public void triangulate() {
        List<Face> result = new ArrayList<>();
        for (Face face : faces) {
            face.triangulate(result, false);
        }
        this.faces = result;
    }

    private ByteBuffer writeIndexBuf() {
        triangulate();
        ByteBuffer buf = ByteBuffer.allocate(faces.size() * 3 * 4);
        for (Face face : faces) {
            buf.putInt(face.vertices[0]);
            buf.putInt(face.vertices[1]);
            buf.putInt(face.vertices[2]);
        }
        return buf;
    }

    private ByteBuffer writeVertBuf(AttrMapping mapping) {
        ByteBuffer buf = ByteBuffer.allocate(vertices.size() * mapping.strideVertex);
        for (Vertex vertex : vertices) mapping.writeVertex(buf, vertex);
    }

    public MeshBuf writeBuf(AttrMapping mapping) {
        return new MeshBuf(materialProp, writeIndexBuf(), writeVertBuf(mapping), mapping);
    }
}