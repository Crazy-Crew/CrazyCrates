package us.crazycrew.crazycrates.platform;

import java.util.List;

/**
 * A class containing available config options to use.
 *
 * @author Ryder Belserion
 * @version 0.7
 * @since 0.5
 */
public interface ISettings {

    /**
     * Decides whether a physical crate accepts virtual keys.
     *
     * @return true or false
     * @since 0.5
     */
    boolean isPhysicalAcceptsVirtual();

    /**
     * Decides whether a physical crate accepts physical keys.
     *
     * @return true or false
     * @since 0.5
     */
    boolean isPhysicalAcceptsPhysical();

    /**
     * Decides whether a virtual crate accepts virtual or physical keys.
     *
     * @return true or false
     * @since 0.5
     */
    boolean isVirtualAcceptsPhysical();

    /**
     * @return a list of worlds crates are disabled in
     * @since 0.5
     */
    List<String> getDisabledWorlds();

}