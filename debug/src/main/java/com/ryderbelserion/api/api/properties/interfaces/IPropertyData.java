package com.ryderbelserion.api.api.properties.interfaces;

import com.ryderbelserion.api.api.properties.objects.interfaces.Property;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;

@NullMarked
public interface IPropertyData {

    <T> T getProperty(final CommentedConfigurationNode configuration, final Property<T> property);

    <T> void setProperty(final CommentedConfigurationNode configuration, final Property<T> property, final T value);

}