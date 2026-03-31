package com.badbones69.common.utils;

import com.badbones69.common.api.enums.State;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

public class ConfigUtils {

    public static @NotNull State getState(@NotNull final CommentedConfigurationNode section, @NotNull final Object... path) {
        State state = State.send_message;

        try {
            state = section.node(path).get(State.class, State.send_message);
        } catch (final SerializationException exception) {
            exception.printStackTrace();
        }

        return state;
    }

    public static void setValue(@NotNull final BasicConfigurationNode configuration, @NotNull final Object value, @NotNull final Object... path) {
        try {
            configuration.appendListNode().node(path).set(value);
        } catch (final SerializationException exception) {
            exception.printStackTrace();
        }
    }
}