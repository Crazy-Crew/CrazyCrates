package us.crazycrew.crazycrates.api.enums;

import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileAction;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.files.types.LogCustomFile;
import com.ryderbelserion.fusion.files.types.configurate.JsonCustomFile;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;

@ApiStatus.Internal
@NullMarked
public enum Files {

    crate_log("crates.log", "logs", FileType.LOG),
    key_log("keys.log", "logs", FileType.LOG),

    editor_config("editor.yml", FileType.YAML),
    config("config.yml", FileType.YAML),

    //crates("crates.json", "cache", FileType.JSON),
    version("version.json", FileType.JSON);

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final Path dataPath = this.fusion.getDataPath();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final FileType fileType;
    private final Path path;

    Files(final String fileName, final String folder, final FileType fileType) {
        this.path = this.dataPath.resolve(folder).resolve(fileName);
        this.fileType = fileType;
    }

    Files(final String fileName, final FileType fileType) {
        this.path = this.dataPath.resolve(fileName);
        this.fileType = fileType;
    }

    public final CommentedConfigurationNode getConfiguration() {
        return getYamlCustomFile().getConfiguration();
    }

    public final YamlCustomFile getYamlCustomFile() {
        final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.path);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.path);
        }

        return customFile.get();
    }

    public final LogCustomFile getLogCustomFile() {
        final Optional<LogCustomFile> customFile = this.fileManager.getLogFile(this.path);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.path);
        }

        return customFile.get();
    }

    public final BasicConfigurationNode getConfigurationNode() {
        return getJsonCustomFile().getConfiguration();
    }

    public final JsonCustomFile getJsonCustomFile() {
        final Optional<JsonCustomFile> customFile = this.fileManager.getJsonFile(this.path);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.path);
        }

        return customFile.get();
    }

    public final FileType getFileType() {
        return this.fileType;
    }

    public final void add() {
        this.fileManager.addFile(this.path, this.fileType);
    }

    public final void reload() {
        this.fileManager.addFile(this.path, this.fileType, action -> action.addAction(FileAction.RELOAD_FILE));
    }

    public final void save() {
        this.fileManager.saveFile(this.path);
    }

    public final Path getPath() {
        return this.path;
    }
}