package us.crazycrew.crazycrates.api.config.properties.objects.primitives.string;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class StringProperty extends BaseProperty {

    public StringProperty(final String defaultValue, final String alias, final Object... path) {
        super(String.class, defaultValue, alias, PropertyType.STRING, path);
    }

    public StringProperty(final String defaultValue, final Object... path) {
        this(defaultValue, "", path);
    }
}