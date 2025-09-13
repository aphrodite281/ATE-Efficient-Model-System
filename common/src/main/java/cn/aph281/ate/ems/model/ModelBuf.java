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

public class ModelBuf {
    Map<MaterialProp, MeshBuf> meshList = new HashMap<>();

    public VertArrays upload(ModelManager manager) {
        VertArrays arrays = new VertArrays();
        for (MeshBuf meshBuf : meshList.values()) {
            arrays.meshList.add(meshBuf.upload(manager));
        }
        return arrays;
    }
}