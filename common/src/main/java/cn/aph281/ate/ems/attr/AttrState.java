package cn.aph281.ate.ems.attr;

import cn.aph281.ate.math.Matrix4f;

public class AttrState { 
    public Integer lightmapUV = null;
    public Matrix4f modelMatrix = new Matrix4f();
    public Integer color = null;
    public Integer overlayUV = null;

    public AttrState() { 
    }

    public AttrState(AttrState state) { 
        if (state == null) return;
        this.overlayUV = state.overlayUV;
        this.lightmapUV = state.lightmapUV;
        this.modelMatrix = state.modelMatrix;
        this.color = state.color;
    }

    public AttrState setColor(int r, int g, int b, int a) {
        this.color = r << 24 | g << 16 | b << 8 | a;
        return this;
    }

    public AttrState setColor(int rgba) {
        this.color = rgba;
        return this;
    }

    public AttrState setLightmapUV(Integer lightmapUV) { 
        this.lightmapUV = lightmapUV;
        return this;
    }

    public AttrState setModelMatrix(Matrix4f modelMatrix) { 
        this.modelMatrix = new Matrix4f(modelMatrix);
        return this;
    }

    public AttrState setOverlayUV(Integer overlayUV) {
        this.overlayUV = overlayUV;
        return this;
    }

    public AttrState append(AttrState state) { 
        this.lightmapUV = (this.lightmapUV | state.lightmapUV);
        this.modelMatrix.mul(state.modelMatrix);
        this.color = this.color | state.color;
        this.overlayUV = this.overlayUV | state.overlayUV;
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (obj instanceof AttrState other) {
            return lightmapUV == other.lightmapUV && modelMatrix.equals(other.modelMatrix) && color == other.color && overlayUV == other.overlayUV;
        }
    }
}