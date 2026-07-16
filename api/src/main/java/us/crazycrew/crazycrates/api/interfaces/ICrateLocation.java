package us.crazycrew.crazycrates.api.interfaces;

import java.util.UUID;

public interface ICrateLocation {

    UUID getIdentifier();

    int getX();

    int getY();

    int getZ();

}