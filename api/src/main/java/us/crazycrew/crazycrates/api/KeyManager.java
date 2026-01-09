package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;

public abstract class KeyManager<I> {

    public abstract boolean isKey(@NotNull final I item);

    public abstract boolean isMatchingKey(@NotNull final I item, @NotNull final I comparing);

    public abstract String getKey(@NotNull final I item);

}