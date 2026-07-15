package us.crazycrew.crazycrates.api.config.properties;

import us.crazycrew.crazycrates.api.config.annotations.Alias;
import us.crazycrew.crazycrates.api.config.annotations.Comment;
import us.crazycrew.crazycrates.api.config.properties.builders.AliasBuilder;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyData;
import us.crazycrew.crazycrates.api.config.properties.interfaces.IPropertyHolder;
import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import org.jspecify.annotations.NullMarked;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@NullMarked
public final class PropertyDataBuilder {

    private final Map<Object[], Property<?>> properties = new HashMap<>();

    private final CommentsBuilder commentsBuilder;
    private final AliasBuilder aliasBuilder;
    private final Path path;

    public PropertyDataBuilder(final Path path) {
        this.commentsBuilder = new CommentsBuilder();
        this.aliasBuilder = new AliasBuilder();
        this.path = path;
    }

    public static IPropertyData createConfiguration(final Path path, final List<Class<? extends IPropertyHolder>> classes) {
        final PropertyDataBuilder builder = new PropertyDataBuilder(path);

        return builder.collect(classes);
    }

    public IPropertyHolder getInstance(final Class<? extends IPropertyHolder> declaringClass) {
        try {
            final Constructor<? extends IPropertyHolder> constructor = declaringClass.getDeclaredConstructor();

            constructor.trySetAccessible();

            return constructor.newInstance();
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    public PropertyDataImpl collect(final List<Class<? extends IPropertyHolder>> classes) {
        for (final Class<? extends IPropertyHolder> parent : classes) {
            final List<Field> fields = List.of(parent.getDeclaredFields());

            final IPropertyHolder holder = getInstance(parent);

            holder.registerComments(this.commentsBuilder);
            holder.registerAliases(this.aliasBuilder);

            for (final Field field : fields) {
                if (!Modifier.isStatic(field.getModifiers())) continue;
                if (!Property.class.isAssignableFrom(field.getType())) continue;

                if (!field.trySetAccessible()) continue;

                try {
                    final Property<?> property = (Property<?>) field.get(null);

                    if (property != null) {
                        final Object[] path = property.getPath();

                        this.properties.putIfAbsent(path, property);

                        if (field.isAnnotationPresent(Comment.class)) {
                            final Comment comment = field.getDeclaredAnnotation(Comment.class);

                            final List<String> values = Arrays.asList(comment.value());

                            if (!values.isEmpty()) {
                                this.commentsBuilder.setComment(values, path);
                            }
                        }

                        if (field.isAnnotationPresent(Alias.class)) {
                            final Alias alias = field.getDeclaredAnnotation(Alias.class);

                            final String value = alias.value();

                            if (!value.isEmpty()) {
                                this.aliasBuilder.addAlias(value, path);
                            }
                        }
                    }
                } catch (final IllegalAccessException exception) {
                    exception.printStackTrace();
                }
            }
        }

        return new PropertyDataImpl(this, this.aliasBuilder, this.properties, this.commentsBuilder.getComments());
    }

    public String parse(final Object[] path) {
        return Arrays.stream(path).map(Object::toString).collect(Collectors.joining(", "))
                .replace(", ", ".");
    }

    public Path getPath() {
        return this.path;
    }
}