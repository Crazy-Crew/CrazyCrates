package us.crazycrew.crazycrates.api.config.properties.data;

import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyData;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import org.jspecify.annotations.NullMarked;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
public final class PropertyDataBuilder {

    private final Map<String, Property<?>> properties = new HashMap<>();

    private final Path path;

    public PropertyDataBuilder(final Path path) {
        this.path = path;
    }

    public static IPropertyData createConfiguration(final Path path, final List<Class<? extends IPropertyHolder>> classes) {
        final PropertyDataBuilder builder = new PropertyDataBuilder(path);

        return builder.collect(classes);
    }

    public PropertyDataImpl collect(final List<Class<? extends IPropertyHolder>> classes) {
        final List<Field> fields = classes.stream().map(Class::getDeclaredFields).flatMap(Arrays::stream).toList();

        for (final Field field : fields) {
            if (!Modifier.isStatic(field.getModifiers())) continue;
            if (!Property.class.isAssignableFrom(field.getType())) continue;

            if (!field.trySetAccessible()) continue;

            try {
                final Property<?> property = (Property<?>) field.get(null);

                if (property != null) {
                    this.properties.putIfAbsent(parse(property.getPath()), property);
                }
            } catch (final IllegalAccessException exception) {
                exception.printStackTrace();
            }
        }

        return new PropertyDataImpl(this, this.properties);
    }

    public String parse(final Object[] path) {
        return Arrays.stream(path).map(Object::toString).collect(Collectors.joining(", "))
                .replace(", ", ".");
    }

    public Path getPath() {
        return this.path;
    }
}