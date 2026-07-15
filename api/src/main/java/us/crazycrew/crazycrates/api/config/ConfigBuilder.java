package us.crazycrew.crazycrates.api.config;

import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.loader.HeaderMode;
import org.spongepowered.configurate.yaml.NodeStyle;
import us.crazycrew.crazycrates.api.config.properties.PropertyManager;
import us.crazycrew.crazycrates.api.config.properties.PropertyDataBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyData;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import com.ryderbelserion.fusion.files.enums.FileType;
import org.jspecify.annotations.NonNull;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.function.UnaryOperator;

public final class ConfigBuilder {

    public static ConfigBuilder withYamlPath(final Path path) {
        return new ConfigBuilder(FileType.YAML, path);
    }

    private final FileType fileType;
    private final Path path;

    public ConfigBuilder(@NonNull final FileType fileType, @NonNull final Path path) {
        this.fileType = fileType;
        this.path = path;
    }

    private HeaderMode headerMode = HeaderMode.PRESERVE;

    private NodeStyle nodeStyle = NodeStyle.BLOCK;
    private boolean isLenient = true;
    private int indent = 4;

    private UnaryOperator<ConfigurationOptions> options;
    private IPropertyData propertyData;

    @SafeVarargs
    public @NonNull final ConfigBuilder configuration(final Class<? extends IPropertyHolder>... properties) {
        this.propertyData = PropertyDataBuilder.createConfiguration(this.path, Arrays.asList(properties));

        return this;
    }

    public @NonNull ConfigBuilder withOptions(final UnaryOperator<ConfigurationOptions> options) {
        this.options = options;

        return this;
    }

    public @NonNull ConfigBuilder withIndent(final int indent) {
        this.indent = indent;

        return this;
    }

    public int getIndent() {
        return this.indent;
    }

    public @NonNull ConfigBuilder withLenient(final boolean isLenient) {
        this.isLenient = isLenient;

        return this;
    }

    public boolean isLenient() {
        return this.isLenient;
    }

    public @NonNull ConfigBuilder withNodeStyle(final NodeStyle nodeStyle) {
        this.nodeStyle = nodeStyle;

        return this;
    }

    public @NonNull NodeStyle getNodeStyle() {
        return this.nodeStyle;
    }

    public @NonNull ConfigBuilder withHeaderMode(final HeaderMode headerMode) {
        this.headerMode = headerMode;

        return this;
    }

    public @NonNull HeaderMode getHeaderMode() {
        return this.headerMode;
    }

    public @NonNull PropertyManager create() {
        final PropertyManager propertyManager = new PropertyManager(this.propertyData,
                this.options,
                this,
                this.fileType,
                this.path
        );

        propertyManager.load();

        return propertyManager;
    }

    public @NonNull Path getPath() {
        return this.path;
    }
}