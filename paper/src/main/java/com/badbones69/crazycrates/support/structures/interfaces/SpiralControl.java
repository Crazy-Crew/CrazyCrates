package com.badbones69.crazycrates.support.structures.interfaces;

import org.bukkit.Location;
import java.util.List;

public interface SpiralControl {

    List<Location> getSpiralLocationClockwise(Location center);

    List<Location> getSpiralLocationCounterClockwise(Location center);

}