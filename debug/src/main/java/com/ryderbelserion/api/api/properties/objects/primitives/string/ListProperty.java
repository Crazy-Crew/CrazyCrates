package com.ryderbelserion.api.api.properties.objects.primitives.string;

import com.ryderbelserion.api.api.properties.objects.BaseProperty;
import com.ryderbelserion.api.api.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;
import java.util.List;

@NullMarked
public final class ListProperty extends BaseProperty<List<String>> {

    public ListProperty(final List<String> defaultValue, final Object... path) {
        super(defaultValue, PropertyType.STRING_LIST, path);
    }

    @Override
    public Class<?> getType() {
        return List.class;
    }
}