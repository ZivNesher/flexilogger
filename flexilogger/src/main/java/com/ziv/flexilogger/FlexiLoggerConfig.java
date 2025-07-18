package com.ziv.flexilogger;

public final class FlexiLoggerConfig {
    public final String appName;
    public final boolean enableCloud;

    private FlexiLoggerConfig(Builder b) {
        appName      = b.appName;
        enableCloud  = b.enableCloud;
    }
    public static class Builder {
        private final String appName;
        private boolean enableCloud = true;
        public Builder(String appName) { this.appName = appName; }
        public Builder enableCloud(boolean v) { enableCloud = v; return this; }
        public FlexiLoggerConfig build() { return new FlexiLoggerConfig(this); }
    }
}
