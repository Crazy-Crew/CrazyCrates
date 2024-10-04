package com.ryderbelserion.crazycrates.common.plugin.logger;

public interface PluginLogger {

    void info(String message);

    void warn(String message);

    void warn(String message, Throwable throwable);

    void error(String message);

    void error(String message, Throwable throwable);

}