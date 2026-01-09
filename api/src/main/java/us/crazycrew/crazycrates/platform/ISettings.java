package us.crazycrew.crazycrates.platform;

import java.util.List;

/**
 * A class containing available config options to use.
 *
 * @author Ryder Belserion
 * @version 0.7.0
 * @since 0.5.0
 */
public interface ISettings {

    /**
     * Decides whether a physical crate accepts virtual keys.
     *
     * @return true or false
     * @since 0.5.0
     */
    boolean isPhysicalAcceptsVirtual();

    /**
     * Decides whether a physical crate accepts physical keys.
     *
     * @return true or false
     * @since 0.5.0
     */
    boolean isPhysicalAcceptsPhysical();

    /**
     * Decides whether a virtual crate accepts virtual or physical keys.
     *
     * @return true or false
     * @since 0.5.0
     */
    boolean isVirtualAcceptsPhysical();

    /**
     * Gets a list of disabled worlds!
     *
     * @return a list of worlds crates are disabled in
     * @since 0.5.0
     */
    List<String> getDisabledWorlds();

}