package com.badbones69.crazycrates;

import com.badbones69.crazycrates.data.DataManager;
import com.ryderbelserion.FusionApi;
import com.ryderbelserion.api.enums.FileType;
import com.ryderbelserion.paper.files.FileManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CrazyCrates extends JavaPlugin {

    public static CrazyCrates getPlugin() {
        return JavaPlugin.getPlugin(CrazyCrates.class);
    }

    private final FusionApi api = FusionApi.get();

    private FileManager fileManager;
    private DataManager dataManager;

    @Override
    public void onEnable() {
        this.api.enable(this);

        this.fileManager = this.api.getFileManager();

        this.fileManager.addFile("respin-gui.yml", "guis", false, FileType.YAML)
                .addFile("crates.log", "logs", false, FileType.NONE)
                .addFile("keys.log", "logs", false, FileType.NONE)
                .addFolder("crates", FileType.YAML)
                .addFolder("schematics", FileType.NONE);

        this.dataManager = new DataManager().init();
    }

    @Override
    public void onDisable() {
        this.api.disable();
    }

    public FileManager getFileManager() {
        return this.fileManager;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public FusionApi getApi() {
        return this.api;
    }
}