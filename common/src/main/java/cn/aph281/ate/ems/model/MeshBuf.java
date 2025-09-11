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

public class MeshBuf {
    public MaterialProp materialProp;
    public ByteBuffer indexBuf;
    public ByteBuffer vertBuf;
    public AttrMapping mapping;

    public MeshBuf(MaterialProp materialProp, ByteBuffer indexBuf, ByteBuffer vertBuf, AttrMapping mapping) {
        this.materialProp = materialProp;
        this.indexBuf = indexBuf;
        this.vertBuf = vertBuf;
        this.mapping = mapping;
    }

    public VertArray upload(ModelManager manager) {
        VertBuf vbo = new VertBuf();
        vbo.upload(vertBuf, VertBuf.USAGE_STATIC_DRAW);
        IndexBuf ebo = new IndexBuf();
        ebo.upload(indexBuf, IndexBuf.USAGE_STATIC_DRAW);
        manager.vboCount += 3;
        manager.vaoCount += 1;
        return new VertArray().create(materialProp, vbo, ebo, new InstanceBuf(0), mapping);
    }
}