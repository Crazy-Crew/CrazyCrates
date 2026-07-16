package com.ryderbelserion.crazycrates.common.storage.impl;

import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class ConnectionFactory {

    public abstract String getImpl();

    public abstract void init();

    public abstract void stop();

    public abstract void save();

}