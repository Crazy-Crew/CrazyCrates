package com.ryderbelserion.api.api.properties.data;

import com.ryderbelserion.api.api.properties.interfaces.IPropertyData;
import com.ryderbelserion.api.api.properties.objects.enums.PropertyType;
import com.ryderbelserion.api.api.properties.objects.interfaces.Property;
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

    private final Map<String, Property<?>> properties;
    private final PropertyDataBuilder builder;

    public PropertyDataImpl(final PropertyDataBuilder builder, final Map<String, Property<?>> properties) {
        this.properties = properties;
        this.builder = builder;
    }

    @Override
    public <T> T getProperty(final CommentedConfigurationNode configuration, final Property<T> property) {
        final Object[] path = property.getPath();

        final String id = this.builder.parse(path);

        if (!this.properties.containsKey(id)) {
            throw new IllegalStateException("The property %s is not present with this builder for %s! Perhaps wrong config!".formatted(id, this.builder.getPath()));
        }

        final PropertyType propertyType = property.getPropertyType();
        final T propertyValue = property.getDefaultValue();
        final Object[] propertyPath = property.getPath();

        return switch (propertyType) {
            case BOOLEAN, INTEGER, STRING -> {
                try {
                    yield (T) configuration.node(propertyPath).get(property.getType(), propertyValue);
                } catch (final SerializationException exception) {
                    throw new IllegalStateException(exception);
                }
            }

            case STRING_LIST -> (T) StringUtils.getStringList(configuration.node(propertyPath), List.of(propertyValue.toString()));
        };
    }

    @Override
    public <T> void setProperty(final CommentedConfigurationNode configuration, final Property<T> property, final T value) {
        final Class<?> type = property.getType();

        if (type != value.getClass()) {
            return;
        }

        try {
            configuration.node(property.getPath()).set(type, value);

            this.fileManager.saveFile(this.builder.getPath());
        } catch (final SerializationException exception) {
            throw new IllegalStateException(exception);
        }
    }
}