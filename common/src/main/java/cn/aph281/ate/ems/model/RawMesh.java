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
import java.io.IOException;
import java.io.DataInputStream;

public class RawMesh { 
    public MaterialProp materialProp;
    public List<Vertex> vertices;
    public List<Face> faces;

    public RawMesh() {
        this.vertices = new ArrayList<>();
        this.faces = new ArrayList<>();
    }

    public RawMesh(MaterialProp prop) {
        this();
        materialProp = prop;
    }

    public RawMesh(List<Vertex> vertices, List<Face> faces) {
        this.vertices = new ArrayList<>(vertices);
        this.faces = new ArrayList<>(faces);
    }

    public RawMesh(DataInputStream dis) throws IOException {
        this.materialProp = new MaterialProp(dis);
        int numVertices = dis.readInt();
        this.vertices = new ArrayList<>(numVertices);
        for (int i = 0; i < numVertices; i++) this.vertices.add(new Vertex(dis));
        int numFaces = dis.readInt();
        this.faces = new ArrayList<>(numFaces);
        for (int i = 0; i < numFaces; i++) this.faces.add(new Face(dis));
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
        ByteBuffer buf = ByteBuffer.allocate(faces.size() * 3 * 4);
        for (Face face : faces) {
            buf.putInt(face.vertices[0]);
            buf.putInt(face.vertices[1]);
            buf.putInt(face.vertices[2]);
        }
        return buf;
    }

    private ByteBuffer writeVertBuf(AttrMapping mapping) {
        ByteBuf buf = Unpooled.buffer();
        for (Vertex vertex : vertices) mapping.writeVertex(buf, vertex);
        return buf.nioBuffer();
    }

    public MeshBuf writeBuf(AttrMapping mapping) {
        triangulate();
        return new MeshBuf(materialProp, writeIndexBuf(), faces.size(), writeVertBuf(mapping), mapping);
    }

    public void append(RawMesh nextMesh) {
        if (nextMesh == this) throw new IllegalStateException("Mesh self-appending");
        int vertOffset = vertices.size();
        vertices.addAll(nextMesh.vertices);
        for (Face face : nextMesh.faces) {
            Face newFace = face.copy();
            for (int i = 0; i < newFace.vertices.length; ++i) {
                newFace.vertices[i] += vertOffset;
            }
            faces.add(newFace);
        }
    }
}