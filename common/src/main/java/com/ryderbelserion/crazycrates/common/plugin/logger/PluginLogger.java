package com.ryderbelserion.crazycrates.common.plugin.logger;

public interface PluginLogger {

    void info(String message, Object... args);

    void warn(String message, Object... args);

    void warn(String message, Throwable throwable, Object... args);

    void error(String message, Object... args);

    void error(String message, Throwable throwable, Object... args);

}