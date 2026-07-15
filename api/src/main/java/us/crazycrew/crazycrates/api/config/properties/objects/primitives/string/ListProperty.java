package us.crazycrew.crazycrates.api.config.properties.objects.primitives.string;

import us.crazycrew.crazycrates.api.config.properties.objects.BaseProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.enums.PropertyType;
import org.jspecify.annotations.NullMarked;
import java.util.List;

@NullMarked
public final class ListProperty extends BaseProperty {

    public ListProperty(final List<String> defaultValue, final String alias, final Object... path) {
        super(String.class, defaultValue, alias, PropertyType.STRING_LIST, path);
    }

    public ListProperty(final List<String> defaultValue, final Object... path) {
        this(defaultValue, "", path);
    }
}