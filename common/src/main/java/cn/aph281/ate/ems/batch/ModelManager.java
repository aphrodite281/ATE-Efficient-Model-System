package cn.aph281.ate.ems.batch;

import net.minecraft.resources.ResourceLocation;
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

public class ModelManager {

    public static final ModelManager INSTANCE = new ModelManager();

    public HashMap<ResourceLocation, ModelBuf> writtenModelBuf = new HashMap<>();
    public HashMap<ResourceLocation, ModelCluster> uploadedVertArrays = new HashMap<>();
    public HashMap<ResourceLocation, RawModel> loadedRawModels = new HashMap<>();

    public int vaoCount = 0;
    public int vboCount = 0;

    public void clear() {
        vaoCount = 0;
        for (ModelCluster vertArrays : uploadedVertArrays.values()) {
            vertArrays.close();
        }
        uploadedVertArrays.clear();
        writtenModelBuf.clear();
        loadedRawModels.clear();
    }

    public ModelCluster uploadRawModel(RawModel rawModel) {
        ModelCluster result = uploadedVertArrays.get(rawModel.sourceLocation);
        if (result != null) return result;
        result = new ModelCluster(rawModel, this);
        ResourceLocation loc = rawModel.sourceLocation;
        if (loc == null) loc = new ResourceLocation("ate-ems-anonymous:", "vertarrays/" + UUID.randomUUID());
        uploadedVertArrays.put(loc, result);
        return result;
    }
}