package cn.aph281.ate.ems.model;

import cn.aph281.ate.math.*;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Arrays;

import java.util.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Face { 
    public int[] vertices;

    public Face() {
        this.vertices = new int[0];
    }

    public Face(Face other) {
        this.vertices = ArrayUtils.clone(other.vertices);
    }

    public Face(int[] vertices) {
        this.vertices = ArrayUtils.clone(vertices);
    }

    public Face(int vi1, int vi2, int vi3) {
        this.vertices = new int[]{vi1, vi2, vi3};
    }

    public Face(DataInputStream in) throws IOException {
        this(in.readInt(), in.readInt(), in.readInt());
    }

    public void serializeTo(DataOutputStream out) throws IOException {
        out.writeInt(vertices[0]);
        out.writeInt(vertices[1]);
        out.writeInt(vertices[2]);
    }

    public void flip() {
        ArrayUtils.reverse(vertices);
    }

    public void triangulate(List<Face> out, boolean createOpposite) {
        if (vertices.length < 3) return;
        if (vertices.length == 3) {
            out.add(this.copy());
            return;
        }
        for (int i = 2; i < vertices.length; i++) {
            out.add(new Face(new int[] { vertices[0], vertices[i - 1], vertices[i]}));
        }
        if (createOpposite) {
            flip();
            triangulate(out, false);
            flip();
        }
    }

    @Override
    public boolean equals(Object obj) { 
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof Face) {
            return Arrays.equals(vertices, ((Face) obj).vertices);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(vertices);
    }

    public Face copy() {
        return new Face(this);
    }
}