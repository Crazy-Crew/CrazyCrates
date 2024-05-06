package us.crazycrew.crazycrates.platform;

import java.util.List;

/**
 * A class containing available config options to use.
 *
 * @author Ryder Belserion
 * @version 0.5
 */
public interface IConfigOptions {

    /**
     * Decides whether a physical crate accepts virtual keys.
     *
     * @return true or false
     */
    boolean isPhysicalAcceptsVirtual();

    /**
     * Decides whether a physical crate accepts physical keys.
     *
     * @return true or false
     */
    boolean isPhysicalAcceptsPhysical();

    /**
     * Decides whether a virtual crate accepts virtual or physical keys.
     *
     * @return true or false
     */
    boolean isVirtualAcceptsPhysical();

    /**
     * @return a list of worlds crates are disabled in
     */
    List<String> getDisabledWorlds();

}