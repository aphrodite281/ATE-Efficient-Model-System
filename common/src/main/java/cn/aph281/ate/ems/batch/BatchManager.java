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

public class BatchManager { 
    public final HashMap<MaterialProp, HashMap<VertArray, ByteBuf>> batches = new HashMap<>();
    public int count = 0;

    public void enqueue(VertArrays model, EnqueueProp enqueueProp, ShaderProp shaderProp, int lightmapUV, Matrix4f modelMatrix) {
        for (VertArray mesh : model.meshList) {
            enqueue(mesh, enqueueProp, shaderProp, lightmapUV, modelMatrix);
        }
    }

    public void enqueue(VertArray model, EnqueueProp enqueueProp, ShaderProp shaderProp, int lightmapUV, Matrix4f modelMatrix) {
        count++;
        ResourceLocation texture = (enqueueProp.texture | model.materialProp.texture) | RenderStage.WHITE_TEXTURE_LOCATION;
        AttrState attrState = new AttrState();
        attrState.setModelMatrix(modelMatrix);
        attrState.append(enqueueProp.attrState);
        attrState.append(model.materialProp.attrState);

        if (model.materialProp.renderStage.fixedLightUV != null)
            attrState.setLightmapUV(model.materialProp.renderStage.fixedLightUV);
        else 
            attrState.setLightmapUV(attrState.lightmapUV | lightmapUV);

        BatchTuple tuple = new BatchTuple(texture, model.materialProp.renderStage, shaderProp);

        HashMap<VertArray, ByteBuf> batch1 = batches.get(tuple);
        if (batch1 == null) {
            batch1 = new HashMap<>();
            batches.put(model.materialProp, batch1);
        }
        ByteBuf instanceBuf = batch1.get(model);
        if (instanceBuf == null) {
            instanceBuf = Unpooled.buffer();
            batch1.put(model, instanceBuf);
        }
        model.mapping.writeInstanceBuf(instanceBuf, attrState);
    }

    public void drawAll(ShaderManager shaderManager, DrawContext drawContext) {
        drawContext.recordBatches(count);

        
    }

    private void drawBatch(RenderBatch renderBatch, ShaderManager shaderManager, DrawContext drawContext) {
        for (HashMap.Entry<BatchTuple, HashMap<VertArray, ByteBuf>> entry : batches.entrySet()) {
            BatchTuple tuple = entry.getKey();
            if (tuple.renderStage.renderBatch != renderBatch) continue;
            shaderManager.setupShaderBatchState(tuple.texture, tuple.renderStage, tuple.shaderProp);
            for (HashMap.Entry<VertArray, ByteBuf> meshEntry : entry.getValue().entrySet()) {
                VertArray mesh = meshEntry.getKey();
                ByteBuf instanceBuf = meshEntry.getValue();
                if (instanceBuf.readableBytes() == 0) continue;
                int instanceCount = instanceBuf.readableBytes() / mesh.mapping.instanceSize;
                mesh.instanceBuf.size = instanceCount;
                glBindVertexArray(mesh.id);
                mesh.instanceBuf.upload(instanceBuf.nioBuffer(), VertBuf.USAGE_DYNAMIC_DRAW);
                drawContext.recordDrawCall(mesh, instanceCount);
                glDrawElementsInstanced(GL_TRIANGLES, indexBuf.vertexCount, indexBuf.indexType, 0L, instanceBuf.size);
                glBindVertexArray(0);
                instanceBuf.clear();
            }
        }
    }
    
    private static class BatchTuple {

        public ResourceLocation texture;
        public RenderStage renderStage;
        public ShaderProp shaderProp;

        public BatchTuple(ResourceLocation texture, RenderStage renderStage, ShaderProp shaderProp) {
            this.texture = texture;
            this.renderStage = renderStage;
            this.shaderProp = shaderProp;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null) return false;
            if (o instanceof BatchTuple that) {
                return texture.equals(that.texture) && renderStage.equals(that.renderStage) && shaderProp.equals(that.shaderProp);
            }
            return materialProp.equals(that.materialProp) && shaderProp.equals(that.shaderProp);
        }

        @Override
        public int hashCode() {
            return Objects.hash(materialProp, shaderProp);
        }
    }
}