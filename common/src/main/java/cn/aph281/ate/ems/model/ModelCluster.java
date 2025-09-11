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
import java.io.Closeable;

public class ModelCluster implements Closeable { 
    public final VertArrays uploadedOpaqueParts;
    public final RawModel opaqueParts = new RawModel();
    public final VertArrays uploadedTranslucentParts;
    public final RawModel translucentParts = new RawModel();

    public ModelCluster(RawModel rawModel, ModelManager manager) { 
        for (Map.Entry<MaterialProp, RawMesh> entry : rawModel.meshList.entrySet()) {
            if (entry.getKey().renderStage.translucent) {
                translucentParts.append(entry.getValue());
            } else {
                opaqueParts.append(entry.getValue());
            }
        }

        uploadedOpaqueParts = opaqueParts.writeBuf(AttrMapping.DEFAULT).upload(manager);
        uploadedTranslucentParts = translucentParts.writeBuf(AttrMapping.DEFAULT).upload(manager);
    }

    @Override
    public void close() {
        uploadedOpaqueParts.close();
        uploadedTranslucentParts.close();
    }
}