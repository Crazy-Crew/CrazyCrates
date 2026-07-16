package us.crazycrew.crazycrates.api.interfaces;

import java.util.Optional;
import java.util.UUID;

public interface ICrateWorld<CL extends ICrateLocation> {

    Optional<CL> getLocationByCoordinates(final UUID identifier, final int x, final int y, final int z);

    void addLocation(final UUID crate, final UUID location, final int x, final int y, final int z);

    Optional<CL> getLocation(final UUID crate, final UUID location);

    void removeLocation(final UUID crate, final UUID location);

    UUID getIdentifier();

    String getWorldName();

}