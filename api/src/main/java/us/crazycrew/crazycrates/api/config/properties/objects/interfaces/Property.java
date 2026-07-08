package us.crazycrew.crazycrates.api.config.properties.objects.interfaces;

import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;

public interface Property<T> {

    PropertyType getPropertyType();

    T getDefaultValue();

    Object[] getPath();

    String getAlias();

    Class<?> getType();

}