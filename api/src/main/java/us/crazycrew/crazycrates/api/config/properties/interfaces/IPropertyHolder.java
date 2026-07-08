package us.crazycrew.crazycrates.api.config.properties.interfaces;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.properties.builders.AliasBuilder;
import us.crazycrew.crazycrates.api.config.properties.builders.CommentsBuilder;

public interface IPropertyHolder {

    default void registerComments(@NonNull final CommentsBuilder builder) {

    }

    default void registerAliases(@NonNull final AliasBuilder builder) {

    }
}