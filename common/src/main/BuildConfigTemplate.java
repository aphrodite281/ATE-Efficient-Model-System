package cn.aph281.ate.ems;

import java.time.Instant;

public interface BuildConfig {

    String MOD_VERSION = "@version@";

    Instant BUILD_TIME = Instant.ofEpochSecond(@build_time@);
}