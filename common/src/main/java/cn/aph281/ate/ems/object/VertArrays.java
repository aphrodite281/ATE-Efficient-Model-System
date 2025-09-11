package cn.aph281.ate.ems.object;

import java.util.*;
import java.io.Closeable;

public class VertArrays implements Closeable {
    public List<VertArray> meshList = new ArrayList<>();

    @Override
    public void close() {
        for (VertArray mesh : meshList) {
            mesh.close();
        }
    }
}