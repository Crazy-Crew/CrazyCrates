package us.crazycrew.crazycrates.api.users;

import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.UUID;

/**
 * A class that handles fetching users, checking virtual keys, adding virtual keys or physical keys
 * Ability to set keys, get keys, getting total keys or checking total crates opened or individual crates opened.
 *
 * @author Ryder Belserion
 * @version 0.7
 * @since 0.1
 */
public abstract class UserManager {

    /**
     * The default constructor for {@link UserManager}.
     *
     * @author Ryder Belserion
     * @since 0.9
     */
    public UserManager() {}

    /**
     * Get the player.
     *
     * @param uuid the {@link UUID} of the {@link Audience}
     * @return {@link Audience}
     * @since 0.1
     */
    public abstract Audience getUser(@NotNull final UUID uuid);

    /**
     * Get the amount of virtual keys a player has.
     *
     * @param uuid the {@link UUID} of the player
     * @param crateName the name of the crate
     * @return the amount of virtual keys
     * @since 0.1
     */
    public abstract int getVirtualKeys(@NotNull final UUID uuid, @NotNull final String crateName);

    /**
     * Give a player virtual keys for a crate.
     *
     * @param uuid the {@link UUID} of the player you want to give the keys to
     * @param crateName the crate of whose keys you are giving
     * @param amount the amount of keys you are giving them
     * @since 0.1
     */
    public abstract void addVirtualKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount);

    /**
     * Set the amount of virtual keys a player has.
     *
     * @param uuid the {@link UUID} of the player you are setting the keys to
     * @param crateName the crate of whose keys are being set
     * @param amount the amount the player will have
     * @since 0.1
     */
    public abstract void setKeys(@NotNull final UUID uuid, @NotNull final String crateName, final int amount);

    /**
     * Give a player keys for a crate.
     *
     * @param uuid the {@link UUID} of the player you want to give the keys to
     * @param crateName the crate of whose keys you are giving
     * @param keyType the type of key you are giving to the player
     * @param amount the amount of keys you are giving them
     * @since 0.1
     */
    public abstract void addKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, final int amount);

    /**
     * Get the total amount of keys a player has.
     *
     * @param uuid the {@link UUID} of the player you want to get keys from
     * @param crateName the crate you want to use
     * @return total amount of keys a player has
     * @since 0.1
     */
    public abstract int getTotalKeys(@NotNull final UUID uuid, @NotNull final String crateName);

    /**
     * Get the physical amount of keys a player has.
     *
     * @param uuid the {@link UUID} of the player you want to get keys from.
     * @param crateName the crate you want to use.
     * @return the amount of physical keys
     */
    public abstract int getPhysicalKeys(@NotNull final UUID uuid, @NotNull final String crateName);

    /**
     * Take a key from a player.
     *
     * @param amount The amount of keys you wish to take
     * @param uuid the {@link UUID} of the player you wish to take keys from
     * @param crateName the crate key you are taking
     * @param keyType the {@link KeyType} you are taking from the player
     * @param checkHand if it just checks the players hand or if it checks their inventory
     * @return true if successfully taken keys and false if not
     * @since 0.1
     */
    public abstract boolean takeKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, final int amount, final boolean checkHand);

    /**
     * Checks to see if the player has a physical key of the crate in their main hand or inventory.
     *
     * @param uuid the {@link UUID} of the player being checked
     * @param crateName the crate that has the key you are checking
     * @param checkHand if it just checks the players hand or if it checks their inventory
     * @return true if they have the key and false if not
     * @since 0.1
     */
    public abstract boolean hasPhysicalKey(@NotNull final UUID uuid, @NotNull final String crateName, final boolean checkHand);

    /**
     * Give keys to an offline player.
     *
     * @param uuid the {@link UUID} of the offline player you wish to give keys to
     * @param crateName the crate of which key you are giving to the player
     * @param keyType the {@link KeyType} used i.e. virtual or physical
     * @param amount the amount of keys you wish to give to the player
     * @return true if it successfully gave the offline player a key and false if there was an error
     * @since 0.1
     */
    public abstract boolean addOfflineKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, int amount);

    /**
     * Take keys from an offline player.
     *
     * @param uuid the {@link UUID} of the offline player you wish to take keys from
     * @param crateName the crate of which key you are taking from the player
     * @param keyType the {@link KeyType} used i.e. virtual or physical
     * @param amount the amount of keys you wish to take from the player
     * @return returns true if it successfully took the key from the offline player and false if there was an error
     * @since 0.1
     */
    public abstract boolean takeOfflineKeys(@NotNull final UUID uuid, @NotNull final String crateName, @NotNull KeyType keyType, int amount);

    /**
     * Gets the total amount of crates this player opened
     *
     * @param uuid the {@link UUID} of the player you wish to check
     * @return returns the amount of total crates opened
     * @since 0.1
     */
    public abstract int getTotalCratesOpened(@NotNull final UUID uuid);

    /**
     * Gets the amount of a specific crate this player opened
     *
     * @param uuid the {@link UUID} of the player you wish to check
     * @param crateName the name of the crate
     * @return returns the amount of times the player opened this crate
     * @since 0.1
     */
    public abstract int getCrateOpened(@NotNull final UUID uuid, @NotNull final String crateName);

    /**
     * Adds how many times a player has opened a crate.
     *
     * @param uuid the {@link UUID} of the player you wish to check
     * @param crateName the name of the crate
     * @param amount the amount of times they opened
     * @since 0.1
     */
    public abstract void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName, final int amount);

    /**
     * Adds how many times a player has opened a crate.
     *
     * @param uuid the {@link UUID} of the player you wish to check
     * @param crateName the name of the crate
     * @since 0.1
     */
    public abstract void addOpenedCrate(@NotNull final UUID uuid, @NotNull final String crateName);

}