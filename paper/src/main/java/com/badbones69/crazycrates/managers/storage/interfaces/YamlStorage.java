package com.badbones69.crazycrates.managers.storage.interfaces;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.common.enums.Environment;
import com.badbones69.crazycrates.common.interfaces.Manifest;
import com.ryderbelserion.vital.paper.api.files.FileManager;
import org.bukkit.configuration.file.YamlConfiguration;

@Manifest(environment = Environment.DEVELOPMENT)
public abstract class YamlStorage implements Storage<YamlConfiguration> {

    protected CrazyCrates plugin = CrazyCrates.getPlugin();
    protected FileManager fileManager = this.plugin.getFileManager();

    @Override
    public void start() {
        this.fileManager.addFile(getFile());
    }

    @Override
    public void save() {
        this.fileManager.saveFile(getFile().getName());
    }

    @Override
    public void reload() {
        this.fileManager.addFile(getFile());
    }

    @Override
    public final YamlConfiguration getAccess() {
        return this.fileManager.getFile(getFile().getName()).getConfiguration();
    }
}