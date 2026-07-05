package us.crazycrew.crazycrates.api.config.properties.objects;

import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import org.jspecify.annotations.NullMarked;

@NullMarked
public abstract class BaseProperty<T> implements Property<T> {

    private final PropertyType propertyType;
    private final T defaultValue;
    private final Object[] path;

    public BaseProperty(final T defaultValue, final PropertyType propertyType, final Object[] path) {
        this.propertyType = propertyType;
        this.defaultValue = defaultValue;
        this.path = path;
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
    public Object[] getPath() {
        return this.path;
    }
}