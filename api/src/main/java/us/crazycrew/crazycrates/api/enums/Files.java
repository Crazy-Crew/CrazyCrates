package us.crazycrew.crazycrates.api.enums;

import com.ryderbelserion.vital.files.FileManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.platform.Server;

public enum Files {

    locations("locations.yml"),
    data("data.yml");

    private final @NotNull Server server = CratesProvider.get();

    private final @NotNull FileManager fileManager = server.getFileManager();

    private final FileConfiguration config;
    private final String fileName;

    Files(String fileName) {
        this.config = this.fileManager.getStaticFile(fileName);

        this.fileName = fileName;
    }

    public FileConfiguration getFile() {
        return this.config;
    }

    public void save() {
        this.fileManager.saveStaticFile(this.fileName);
    }

    public void reload() {
        this.fileManager.reloadStaticFile(this.fileName);
    }
}