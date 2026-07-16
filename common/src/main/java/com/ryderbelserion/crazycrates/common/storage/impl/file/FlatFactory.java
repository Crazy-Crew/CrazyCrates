package com.ryderbelserion.crazycrates.common.storage.impl.file;

import com.ryderbelserion.crazycrates.common.storage.impl.ConnectionFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FlatFactory extends ConnectionFactory {

    private final String impl;

    public FlatFactory(final String impl) {
        this.impl = impl;
    }

    @Override
    public String getImpl() {
        return this.impl;
    }
}