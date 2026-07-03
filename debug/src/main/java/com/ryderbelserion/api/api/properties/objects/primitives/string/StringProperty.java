package com.ryderbelserion.api.api.properties.objects.primitives.string;

import com.ryderbelserion.api.api.properties.objects.BaseProperty;
import com.ryderbelserion.api.api.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class StringProperty extends BaseProperty<String> {

    public StringProperty(final String defaultValue, final Object... path) {
        super(defaultValue, PropertyType.STRING, path);
    }

    @Override
    public Class<?> getType() {
        return String.class;
    }
}