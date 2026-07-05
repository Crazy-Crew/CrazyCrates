package us.crazycrew.crazycrates.api.config.properties.objects.primitives;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
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