package us.crazycrew.crazycrates.api.config.properties.objects.primitives.string;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
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