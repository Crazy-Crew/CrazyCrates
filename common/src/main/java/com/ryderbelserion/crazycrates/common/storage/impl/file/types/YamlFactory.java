package com.ryderbelserion.crazycrates.common.storage.impl.file.types;

import com.ryderbelserion.crazycrates.common.storage.impl.file.FlatFactory;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class YamlFactory extends FlatFactory {

    public YamlFactory() {
        super("yaml");
    }

    @Override
    public void init() {}

    @Override
    public void stop() {}

    @Override
    public void save() {}
}