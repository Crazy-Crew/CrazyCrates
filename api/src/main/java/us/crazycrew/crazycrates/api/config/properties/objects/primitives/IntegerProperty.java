package us.crazycrew.crazycrates.api.config.properties.objects.primitives;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;

@NullMarked
public final class IntegerProperty extends BaseProperty {

    public IntegerProperty(final int defaultValue, final String alias, final Object... path) {
        super(Integer.class, defaultValue, alias, PropertyType.INTEGER, path);
    }

    public IntegerProperty(final int defaultValue, final Object... path) {
        this(defaultValue, "", path);
    }
}