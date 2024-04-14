package us.crazycrew.crazycrates.api.users;

import net.kyori.adventure.audience.Audience;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import java.util.UUID;

/**
 * A class that handles fetching users, checking virtual keys, adding virtual keys or physical keys
 * Ability to set keys, get keys, getting total keys or checking total crates opened or individual crates opened.
 *
 * @author Ryder Belserion
 * @version 1.0-snapshot
 */
public abstract class UserManager {

    /**
     * Get the player
     *
     * @param uuid The uuid of the player
     * @return player
     */
    public abstract Audience getUser(UUID uuid);

    /**
     * Get the amount of virtual keys a player has.
     *
     * @param uuid The uuid of the player
     * @param crateName The name of the crate
     * @return the amount of virtual keys
     */
    public abstract int getVirtualKeys(UUID uuid, String crateName);

    /**
     * Give a player virtual keys for a crate.
     *
     * @param amount The amount of keys you are giving them.
     * @param uuid The player you want to give the keys to.
     * @param crateName The crate of whose keys you are giving.
     */
    public abstract void addVirtualKeys(int amount, UUID uuid, String crateName);

    /**
     * Set the amount of virtual keys a player has.
     *
     * @param amount The amount the player will have.
     * @param uuid The uuid of the player you are setting the keys to.
     * @param crateName The crate of whose keys are being set.
     */
    public abstract void setKeys(int amount, UUID uuid, String crateName);

    /**
     * Give a player keys for a crate.
     *
     * @param amount The amount of keys you are giving them.
     * @param uuid The player you want to give the keys to.
     * @param crateName The crate of whose keys you are giving.
     * @param keyType The type of key you are giving to the player.
     */
    public abstract void addKeys(int amount, UUID uuid, String crateName, KeyType keyType);

    /**
     * Get the total amount of keys a player has.
     *
     * @param uuid The player you want to get keys from.
     * @param crateName The crate you want to use.
     * @return total amount of keys a player has.
     */
    public abstract int getTotalKeys(UUID uuid, String crateName);

    /**
     * Get the physical amount of keys a player has.
     *
     * @param uuid The player you want to get keys from.
     * @param crateName The crate you want to use.
     * @return the amount of physical keys
     */
    public abstract int getPhysicalKeys(UUID uuid, String crateName);

    /**
     * Take a key from a player.
     *
     * @param amount The amount of keys you wish to take.
     * @param uuid The uuid of the player you wish to take keys from.
     * @param crateName The crate key you are taking.
     * @param keyType The type of key you are taking from the player.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     * @return true if successfully taken keys and false if not.
     */
    public abstract boolean takeKeys(int amount, UUID uuid, String crateName, KeyType keyType, boolean checkHand);

    /**
     * Checks to see if the player has a physical key of the crate in their main hand or inventory.
     *
     * @param uuid The uuid of the player being checked.
     * @param crateName The crate that has the key you are checking.
     * @param checkHand If it just checks the players hand or if it checks their inventory.
     * @return true if they have the key and false if not.
     */
    public abstract boolean hasPhysicalKey(UUID uuid, String crateName, boolean checkHand);

    /**
     * Give keys to an offline player.
     *
     * @param uuid The uuid of the offline player you wish to give keys to.
     * @param crateName The crate of which key you are giving to the player.
     * @param keys The amount of keys you wish to give to the player.
     * @param type The key type used i.e. virtual or physical
     * @return true if it successfully gave the offline player a key and false if there was an error.
     */
    public abstract boolean addOfflineKeys(UUID uuid, String crateName, int keys, KeyType type);

    /**
     * Take keys from an offline player.
     *
     * @param uuid The uuid of the offline player you wish to take keys from.
     * @param crateName The crate of which key you are taking from the player.
     * @param keys The amount of keys you wish to take from the player.
     * @param type The key type used i.e. virtual or physical
     * @return Returns true if it successfully took the key from the offline player and false if there was an error.
     */
    public abstract boolean takeOfflineKeys(UUID uuid, String crateName, int keys, KeyType type);

    /**
     * Gets the total amount of crates this player opened.
     *
     * @param uuid The uuid of the player you wish to check.
     * @return Returns the amount of total crates opened.
     */
    public abstract int getTotalCratesOpened(UUID uuid);

    /**
     * Gets the amount of a specific crate this player opened.
     *
     * @param uuid The uuid of the player you wish to check.
     * @param crateName The name of the crate.
     * @return Returns the amount of times the player opened this crate.
     */
    public abstract int getCrateOpened(UUID uuid, String crateName);

    /**
     * Adds how many times a player has opened a crate.
     *
     * @param uuid The uuid of the player you wish to check.
     * @param amount The amount of times they opened.
     * @param crateName The name of the crate.
     */
    public abstract void addOpenedCrate(UUID uuid, int amount, String crateName);

    /**
     * Adds how many times a player has opened a crate.
     *
     * @param uuid The uuid of the player you wish to check.
     * @param crateName The name of the crate.
     */
    public abstract void addOpenedCrate(UUID uuid, String crateName);

}