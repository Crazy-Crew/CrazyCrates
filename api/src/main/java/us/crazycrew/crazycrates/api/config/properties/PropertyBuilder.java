package us.crazycrew.crazycrates.api.config.properties;

import us.crazycrew.crazycrates.api.config.properties.objects.BeanProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.primitives.BooleanProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.primitives.IntegerProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.primitives.string.ListProperty;
import us.crazycrew.crazycrates.api.config.properties.objects.primitives.string.StringProperty;
import java.util.List;

public class PropertyBuilder {

    public static <B> BeanProperty<B> newBeanProperty(final Class<B> beanClass, final B defaultValue, final Object... path) {
        return new BeanProperty(beanClass, defaultValue, path);
    }

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