package us.crazycrew.crazycrates.api.config.properties.interfaces;

import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.config.properties.comments.CommentsBuilder;

public interface IPropertyHolder {

    default void registerComments(@NonNull final CommentsBuilder configuration) {

    }
}