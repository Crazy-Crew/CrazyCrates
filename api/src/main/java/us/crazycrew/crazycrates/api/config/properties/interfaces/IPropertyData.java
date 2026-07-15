package us.crazycrew.crazycrates.api.config.properties.interfaces;

import us.crazycrew.crazycrates.api.config.properties.objects.interfaces.Property;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;

@NullMarked
public interface IPropertyData {

    <T> T getProperty(final CommentedConfigurationNode configuration, final Property<T> property);

    <T> void setProperty(final CommentedConfigurationNode configuration, final Property<T> property, final T value);

    void setComment(final CommentedConfigurationNode configuration, final String value, final Object... path);

    void populate(final CommentedConfigurationNode configuration);

}