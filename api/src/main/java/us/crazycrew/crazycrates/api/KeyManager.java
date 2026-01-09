package us.crazycrew.crazycrates.api;

import org.jetbrains.annotations.NotNull;

/**
 * A class for the KeyManager, It handles checking physical keys, and anything else we might need soon.
 *
 * @author Ryder Belserion
 * @version 0.9.0
 * @since 0.9.0
 */
public abstract class KeyManager<I> {

    /**
     * The default constructor for {@link KeyManager<I>}.
     *
     * @author Ryder Belserion
     * @since 0.9.0
     */
    public KeyManager() {}

    /**
     * Checks if the item is a valid key.
     *
     * @param item the ItemStack
     * @return true or false
     * @since 0.9.0
     */
    public abstract boolean isKey(@NotNull final I item);

    /**
     * Checks if an item matches another item.
     *
     * @param item the initial ItemStack
     * @param comparing the ItemStack to compare
     * @return true or false
     * @since 0.9.0
     */
    public abstract boolean isMatchingKey(@NotNull final I item, @NotNull final I comparing);

    /**
     * Gets the name of the crate the key belongs to.
     *
     * @param item the ItemStack
     * @return the name of the crate
     * @since 0.9.0
     */
    public abstract String getKey(@NotNull final I item);

}