package us.crazycrew.crazycrates.api.config.properties;

import us.crazycrew.crazycrates.api.config.properties.builders.AliasBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyData;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import com.ryderbelserion.fusion.core.api.FusionProvider;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;
import java.util.List;
import java.util.Map;

@NullMarked
public final class PropertyDataImpl implements IPropertyData {

    private final FusionKyori fusion = (FusionKyori) FusionProvider.getInstance();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final PropertyDataBuilder propertyDataBuilder;
    private final Map<Object[], Property<?>> properties;
    private final Map<Object[], List<String>> comments;
    private final AliasBuilder aliasBuilder;

    public PropertyDataImpl(final PropertyDataBuilder propertyDataBuilder, final AliasBuilder aliasBuilder, final Map<Object[], Property<?>> properties, Map<Object[], List<String>> comments) {
        this.propertyDataBuilder = propertyDataBuilder;
        this.aliasBuilder = aliasBuilder;
        this.properties = properties;
        this.comments = comments;
    }

    @Override
    public <T> T getProperty(final CommentedConfigurationNode configuration, final Property<T> property) {
        final Object[] path = property.getPath();

        if (!this.properties.containsKey(path)) {
            throw new IllegalStateException("The property %s is not present with this builder for %s! Perhaps wrong config!".formatted(path, this.propertyDataBuilder.getPath()));
        }

        final PropertyType propertyType = property.getPropertyType();
        final T propertyValue = property.getDefaultValue();
        final Object[] propertyPath = property.getPath();

        return switch (propertyType) {
            case BOOLEAN, INTEGER, STRING, BEAN_PROPERTY -> {
                try {
                    yield (T) configuration.node(propertyPath).get(property.getType(), propertyValue);
                } catch (final SerializationException exception) {
                    throw new IllegalStateException(exception);
                }
            }

            case LIST -> (T) StringUtils.getStringList(configuration.node(propertyPath), List.of(propertyValue.toString()));
        };
    }

    public List<Object[]> combineArrays(final Object[] start, final Object[] end) {
        return List.of(start, end);
    }

    @Override
    public <T> void setProperty(final CommentedConfigurationNode configuration, final Property<T> property, final T value) {
        final Class<?> type = property.getType();

        if (type != value.getClass()) {
            return;
        }

        try {
            configuration.node(property.getPath()).set(type, value);
        } catch (final SerializationException exception) {
            throw new IllegalStateException(exception);
        }

        this.fileManager.saveFile(this.propertyDataBuilder.getPath());
    }

    @Override
    public void setComment(final CommentedConfigurationNode configuration, final String value, final Object... path) {
        configuration.node(path).commentIfAbsent(value);

        this.fileManager.saveFile(this.propertyDataBuilder.getPath());
    }

    @Override
    public void populate(final CommentedConfigurationNode configuration) {
        for (final Map.Entry<Object[], Property<?>> parent : this.properties.entrySet()) {
            final Property<?> property = parent.getValue();
            final Object[] id = property.getPath();

            if (configuration.hasChild(id)) continue;

            try {
                configuration.node(id).set(property.getType(), property.getDefaultValue());
            } catch (final SerializationException exception) {
                throw new IllegalStateException(exception);
            }
        }

        for (final Map.Entry<Object[], List<String>> parent : this.comments.entrySet()) {
            final List<String> list = parent.getValue();

            if (list.isEmpty()) continue;

            final Object[] id = parent.getKey();

            if (!configuration.hasChild(id)) continue;

            final CommentedConfigurationNode section = configuration.node(id);

            section.commentIfAbsent(StringUtils.toString(list));
        }

        this.fileManager.saveFile(this.propertyDataBuilder.getPath());
    }
}