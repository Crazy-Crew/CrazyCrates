package com.ryderbelserion.api.api.properties.objects.interfaces;

import com.ryderbelserion.api.api.properties.objects.enums.PropertyType;

public interface Property<T> {

    PropertyType getPropertyType();

    T getDefaultValue();

    Object[] getPath();

    Class<?> getType();

}