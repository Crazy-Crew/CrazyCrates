package com.ryderbelserion.api.api.properties.objects.primitives;

import com.ryderbelserion.api.api.properties.objects.BaseProperty;
import com.ryderbelserion.api.api.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BooleanProperty extends BaseProperty<Boolean> {

    public BooleanProperty(final boolean defaultValue, final Object... path) {
        super(defaultValue, PropertyType.BOOLEAN, path);
    }

    @Override
    public Class<?> getType() {
        return Boolean.class;
    }
}