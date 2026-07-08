package us.crazycrew.crazycrates.api.config.properties.objects.primitives;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class BooleanProperty extends BaseProperty {

    public BooleanProperty(final boolean defaultValue, final String alias, final Object... path) {
        super(Boolean.class, defaultValue, PropertyType.BOOLEAN, path);
    }

    public BooleanProperty(final boolean defaultValue, final Object... path) {
        this(defaultValue, "", path);
    }
}