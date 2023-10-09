package com.badbones69.crazycrates.paper.support.structures.interfaces;

import org.bukkit.Location;

import java.util.ArrayList;

public interface SpiralControl {

    ArrayList<Location> getSpiralLocationClockwise(Location center);

    ArrayList<Location> getSpiralLocationCounterClockwise(Location center);

}