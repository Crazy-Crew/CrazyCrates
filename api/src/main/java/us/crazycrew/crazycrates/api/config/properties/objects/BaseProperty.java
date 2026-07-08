package us.crazycrew.crazycrates.api.config.properties.objects;

import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class BaseProperty<T> implements Property<T> {

    private final PropertyType propertyType;
    private final T defaultValue;
    private final Object[] path;
    private final Class<T> type;
    private final String alias;

    public BaseProperty(final Class<T> type, final T defaultValue, final String alias, final PropertyType propertyType, final Object[] path) {
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
        this.alias = alias;
        this.type = type;
        this.path = path;
    }

    public BaseProperty(final Class<T> type, final T defaultValue, final PropertyType propertyType, final Object[] path) {
        this(type, defaultValue, "", propertyType, path);
    }

    @Override
    public PropertyType getPropertyType() {
        return this.propertyType;
    }

    @Override
    public T getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public String getAlias() {
        return this.alias;
    }

    @Override
    public Object[] getPath() {
        return this.path;
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }
}