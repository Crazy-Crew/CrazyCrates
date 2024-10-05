package com.ryderbelserion.crazycrates.common.plugin.logger;

import com.ryderbelserion.vital.common.util.AdvUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class AbstractLogger implements PluginLogger {

    private final ComponentLogger logger;

    public AbstractLogger(final ComponentLogger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message, Object... args) {
        this.logger.info(AdvUtil.parse(message), args);
    }

    @Override
    public void warn(String message, Object... args) {
        this.logger.warn(AdvUtil.parse(message), args);
    }

    @Override
    public void warn(String message, Throwable throwable, Object... args) {
        this.logger.warn(AdvUtil.parse(message), throwable, args);
    }

    @Override
    public void error(String message, Object... args) {
        this.logger.error(AdvUtil.parse(message), args);
    }

    @Override
    public void error(String message, Throwable throwable, Object... args) {
        this.logger.error(AdvUtil.parse(message), throwable, args);
    }
}