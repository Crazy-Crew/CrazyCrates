package us.crazycrew.crazycrates.api.config.properties.objects;

import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;

public class BeanProperty<T> extends BaseProperty<T> {

    public BeanProperty(final Class<T> type, final T defaultValue, final String alias, final Object[] path) {
        super(type, defaultValue, alias, PropertyType.BEAN_PROPERTY, path);
    }

    public BeanProperty(final Class<T> type, final T defaultValue, final Object[] path) {
        this(type, defaultValue, "", path);
    }
}