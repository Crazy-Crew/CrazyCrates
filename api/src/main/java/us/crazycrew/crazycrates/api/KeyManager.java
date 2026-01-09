package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;

public abstract class KeyManager<I> {

    public abstract boolean isKey(@NotNull final I item);

    public abstract String getKey(@NotNull final I item);

}