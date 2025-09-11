package cn.aph281.ate.ems.attr;

public enum AttrSrc {
    /** In MaterialProp or EnqueueProp, MaterialProp has priority */
    GLOBAL,

    /** In vertex VBO */
    VERTEX_BUF,

    /** If specified in EnqueueProp or MaterialProp use global, otherwise use vertex VBO */
    VERTEX_BUF_OR_GLOBAL,

    /** In instance VBO */
    INSTANCE_BUF,

    /** If specified in EnqueueProp or MaterialProp use global, otherwise use instance VBO */
    INSTANCE_BUF_OR_GLOBAL;

    public boolean isToggleable() {
        return this == VERTEX_BUF_OR_GLOBAL || this == INSTANCE_BUF_OR_GLOBAL;
    }

    public boolean inVertBuf() {
        return this == VERTEX_BUF || this == VERTEX_BUF_OR_GLOBAL;
    }

    public boolean inInstanceBuf() {
        return this == INSTANCE_BUF || this == INSTANCE_BUF_OR_GLOBAL;
    }
}