package com.badbones69.crazycrates.modules.config;

import com.badbones69.crazycrates.modules.config.files.ConfigFile;
import com.google.inject.AbstractModule;
import com.google.inject.name.Names;

public class ConfigBinder extends AbstractModule {

    @Override
    protected void configure() {
        bindConfig(new ConfigFile(), "config");
    }

    private void bindConfig(ConfigFile config, String name) {
        bind(ConfigFile.class).annotatedWith(Names.named(name)).toInstance(config);
    }
}