package com.ryderbelserion.crazycrates.common.plugin.logger;

import com.ryderbelserion.vital.common.util.AdvUtil;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

public class AbstractLogger implements PluginLogger {

    private final ComponentLogger logger;

    public AbstractLogger(final ComponentLogger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        this.logger.info(AdvUtil.parse(message));
    }

    @Override
    public void warn(String message) {
        this.logger.warn(AdvUtil.parse(message));
    }

    @Override
    public void warn(String message, Throwable throwable) {
        this.logger.warn(AdvUtil.parse(message), throwable);
    }

    @Override
    public void error(String message) {
        this.logger.error(AdvUtil.parse(message));
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.logger.error(AdvUtil.parse(message), throwable);
    }
}