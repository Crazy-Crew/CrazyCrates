package com.ryderbelserion.api.api.properties;

import com.ryderbelserion.api.api.properties.objects.primitives.BooleanProperty;
import com.ryderbelserion.api.api.properties.objects.primitives.IntegerProperty;
import com.ryderbelserion.api.api.properties.objects.primitives.string.ListProperty;
import com.ryderbelserion.api.api.properties.objects.primitives.string.StringProperty;
import java.util.List;

public class PropertyBuilder {

    public static ListProperty newProperty(final List<String> defaultValue, final Object... path) {
        return new ListProperty(defaultValue, path);
    }

    public static IntegerProperty newProperty(final int defaultValue, final Object... path) {
        return new IntegerProperty(defaultValue, path);
    }

    public static BooleanProperty newProperty(final boolean defaultValue, final Object... path) {
        return new BooleanProperty(defaultValue, path);
    }

    public static StringProperty newProperty(final String defaultValue, final Object... path) {
        return new StringProperty(defaultValue, path);
    }
}