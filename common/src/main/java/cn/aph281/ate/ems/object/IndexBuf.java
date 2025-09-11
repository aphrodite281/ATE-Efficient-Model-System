package cn.aph281.ate.ems.object;

public class IndexBuf extends VertBuf {
    public int faceCount;
    public final int indexType;

    public int vertexCount;

    public IndexBuf(int faceCount, int indexType) {
        super();
        this.faceCount = faceCount;
        this.indexType = indexType;
        this.vertexCount = faceCount * 3;
    }

    public void setFaceCount(int faceCount) {
        this.faceCount = faceCount;
        this.vertexCount = faceCount * 3;
    }
}