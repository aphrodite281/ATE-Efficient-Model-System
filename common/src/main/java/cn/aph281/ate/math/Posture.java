package cn.aph281.ate.math;

public interface Posture {
    Matrix4f getAsMatrix4f();

    Matrix3f getAsMatrix3f();

    Matrices getAsMatrices();

    Pose getAsPose();

    PoseStack getAsPoseStack();
}