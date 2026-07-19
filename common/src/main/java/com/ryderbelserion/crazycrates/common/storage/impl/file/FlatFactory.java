package com.ryderbelserion.crazycrates.common.storage.impl.file;

import com.ryderbelserion.crazycrates.common.CrazyCratesPlugin;
import com.ryderbelserion.crazycrates.common.storage.impl.ConnectionFactory;
import com.ryderbelserion.fusion.core.FusionCore;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class FlatFactory extends ConnectionFactory {

    protected final CrazyCratesPlugin plugin;
    protected final FusionCore fusion;

    private final String impl;

    public FlatFactory(final CrazyCratesPlugin plugin, final String impl) {
        this.fusion = plugin.getFusion();
        this.plugin = plugin;
        this.impl = impl;
    }

    @Override
    public String getImpl() {
        return this.impl;
    }
}