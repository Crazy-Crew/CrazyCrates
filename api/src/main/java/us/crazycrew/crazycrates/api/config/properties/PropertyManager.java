package us.crazycrew.crazycrates.api.config.properties;

import com.ryderbelserion.fusion.core.api.exceptions.FusionException;
import com.ryderbelserion.fusion.files.enums.FileAction;
import com.ryderbelserion.fusion.files.types.configurate.YamlCustomFile;
import org.jspecify.annotations.Nullable;
import org.spongepowered.configurate.ConfigurationOptions;
import us.crazycrew.crazycrates.api.config.ConfigBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyData;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.files.enums.FileType;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.nio.file.Path;
import java.util.Optional;
import java.util.function.UnaryOperator;

public final class PropertyManager {

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final UnaryOperator<ConfigurationOptions> options;
    private final IPropertyData propertyData;
    private final ConfigBuilder builder;
    private final FileType fileType;
    private final Path path;

    public PropertyManager(@NonNull final IPropertyData propertyData,
                           @Nullable final UnaryOperator<ConfigurationOptions> options,
                           @NonNull final ConfigBuilder builder,
                           @NonNull final FileType fileType,
                           @NonNull final Path path
    ) {
        this.propertyData = propertyData;
        this.fileType = fileType;
        this.options = options;
        this.builder = builder;
        this.path = path;
    }

    public @NonNull <T> T getProperty(@NonNull final Property<T> property) {
        return this.propertyData.getProperty(getConfiguration(), property);
    }

    public <T> boolean hasProperty(@NonNull final Property<T> property) {
        return this.propertyData.hasProperty(getConfiguration(), property);
    }

    public <T> PropertyManager setProperty(@NonNull final Property<T> property, @NonNull final T value) {
        this.propertyData.setProperty(getConfiguration(), property, value);

        return this;
    }

    public PropertyManager setComment(final String value, final Object... path) {
        this.propertyData.setComment(getConfiguration(), value, path);

        return this;
    }

    public PropertyManager populate() {
        this.propertyData.populate(getConfiguration());

        return this;
    }

    public CommentedConfigurationNode getConfiguration() {
        final Optional<YamlCustomFile> customFile = this.fileManager.getYamlFile(this.path);

        if (customFile.isEmpty()) {
            throw new FusionException("Could not find custom file for " + this.path);
        }

        return customFile.get().getConfiguration();
    }

    public void reload() {
        this.fileManager.reloadFile(this.path);
    }

    public void load() {
        this.fileManager.addFile(this.path, FileType.YAML, consumer -> {
            consumer.addAction(FileAction.EXTRACT_FILE);

            consumer.withHeaderMode(this.builder.getHeaderMode());
            consumer.withNodeStyle(this.builder.getNodeStyle());
            consumer.withLenient(this.builder.isLenient());
            consumer.withIndent(this.builder.getIndent());

            if (this.options != null) {
                consumer.setOptions(this.options);
            }
        });

        populate();
    }

    public void save() {
        this.fileManager.saveFile(this.path);
    }

    public @NonNull FileType getFileType() {
        return this.fileType;
    }

    public @NonNull Path getPath() {
        return this.path;
    }
}