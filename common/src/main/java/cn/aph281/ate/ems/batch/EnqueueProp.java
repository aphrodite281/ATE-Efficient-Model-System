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

public class EnqueueProp {

    public static EnqueueProp DEFAULT = new EnqueueProp(null, null);

    /** The vertex attribute values to use for those specified with VertAttrSrc ENQUEUE. */
    public AttrState attrState;
    public ResourceLocation texture;

    public EnqueueProp(AttrState attrState, ResourceLocation texture) {
        this.attrState = attrState;
        this.texture = texture;
    }
}
