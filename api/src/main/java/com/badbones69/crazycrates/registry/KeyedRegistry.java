package com.badbones69.crazycrates.registry;

import org.jetbrains.annotations.Nullable;
import com.badbones69.crazycrates.util.keys.Key;
import com.badbones69.crazycrates.util.keys.Keyed;

/**
 * Represents a registry of keyed objects.
 *
 * @param <T> keyed type
 *
 * @author BillyGalbreath
 */
public class KeyedRegistry<T extends Keyed> extends Registry<Key, T> {

    /**
     * Register a new entry.
     * <p>
     * Will return null if the entry is already registered.
     *
     * @param entry entry to register
     * @return registered entry or null
     */
    @Nullable
    public T register(@Nullable T entry) {
        if (entry == null) return null;

        if (this.entries.containsKey(entry.getKey())) return null;

        this.entries.put(entry.getKey(), entry);

        return entry;
    }

    /**
     * Unregister the specified entry.
     * <p>
     * Will return null if entry is not registered.
     *
     * @param entry entry to unregister
     * @return unregistered entry or null
     */
    @Nullable
    public T unregister(@Nullable T entry) {
        if (entry == null) return null;

        return unregister(entry.getKey());
    }
}