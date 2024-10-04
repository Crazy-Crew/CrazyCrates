package com.ryderbelserion.crazycrates.common.plugin.logger;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class AbstractLogger implements PluginLogger {

    private final ComponentLogger logger;

    private final MiniMessage miniMessage;

    public AbstractLogger(final ComponentLogger logger) {
        this.logger = logger;

        this.miniMessage = MiniMessage.miniMessage();
    }

    @Override
    public void info(String message) {
        this.logger.info(this.miniMessage.deserialize(message));
    }

    @Override
    public void warn(String message) {
        this.logger.warn(this.miniMessage.deserialize(message));
    }

    @Override
    public void warn(String message, Throwable throwable) {
        this.logger.warn(this.miniMessage.deserialize(message), throwable);
    }

    @Override
    public void error(String message) {
        this.logger.error(this.miniMessage.deserialize(message));
    }

    @Override
    public void error(String message, Throwable throwable) {
        this.logger.error(this.miniMessage.deserialize(message), throwable);
    }
}