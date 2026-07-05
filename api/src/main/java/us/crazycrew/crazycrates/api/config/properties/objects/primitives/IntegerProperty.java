package us.crazycrew.crazycrates.api.config.properties.objects.primitives;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
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