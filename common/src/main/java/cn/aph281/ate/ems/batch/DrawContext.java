package cn.aph281.ate.ems.batch;

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

public class DrawContext {

    public boolean drawWithBlaze = false;
    public boolean sortTranslucentFaces = false;

    public int drawCallCount = 0;
    public int batchCount = 0;
    public int singleFaceCount = 0;
    public int instancedFaceCount = 0;
    public int blazeFaceCount;

    private int drawCallCountCF = 0;
    private int batchCountCF = 0;
    private int singleFaceCountCF = 0;
    private int instancedFaceCountCF = 0;
    private int blazeFaceCountCF = 0;

    public List<String> debugInfo = new ArrayList<>();
    private List<String> debugInfoCF = new ArrayList<>();

    public void resetFrameProfiler() {
        drawCallCount = drawCallCountCF;
        batchCount = batchCountCF;
        singleFaceCount = singleFaceCountCF;
        instancedFaceCount = instancedFaceCountCF;
        blazeFaceCount = blazeFaceCountCF;
        drawCallCountCF = 0;
        batchCountCF = 0;
        singleFaceCountCF = 0;
        instancedFaceCountCF = 0;
        blazeFaceCountCF = 0;
#if DEBUG
        debugInfo = debugInfoCF;
        debugInfoCF = new ArrayList<>();
#endif
    }

    public void recordBatches(int batchCount) {
        batchCountCF += batchCount;
    }

    public void recordDrawCall(VertArray vao) {
        drawCallCountCF++;
        if (vao.instanceBuf != null) {
            instancedFaceCountCF += vao.getFaceCount();
        } else {
            singleFaceCountCF += vao.getFaceCount();
        }
    }

    public void recordBlazeAction(int faceCount) {
        blazeFaceCountCF += faceCount;
    }
}
