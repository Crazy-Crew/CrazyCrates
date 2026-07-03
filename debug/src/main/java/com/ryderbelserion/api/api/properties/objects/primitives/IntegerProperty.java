package com.ryderbelserion.api.api.properties.objects.primitives;

import com.ryderbelserion.api.api.properties.objects.BaseProperty;
import com.ryderbelserion.api.api.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class IntegerProperty extends BaseProperty<Integer> {

    public IntegerProperty(final int defaultValue, final Object... path) {
        super(defaultValue, PropertyType.INTEGER, path);
    }

    @Override
    public Class<?> getType() {
        return Integer.class;
    }
}