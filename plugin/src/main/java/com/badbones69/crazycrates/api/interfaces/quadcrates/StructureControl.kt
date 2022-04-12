package com.badbones69.crazycrates.api.interfaces.quadcrates

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block

interface StructureControl {

    // Paste a structure using available .nbt files
    fun pasteStructure(location: Location)

    // Save a new nbt structure
    fun saveStructure(location: ArrayList<Location>)

    // Remove a structure
    fun removeStructure(location: Location)

    fun getStructureX(): Double

    fun getStructureZ(): Double

    // Add & get the blocks from/to the hashmap
    fun getStructureBlocks(location: Location): ArrayList<Block>

    // Get blocks prior to spawning in the structure
    fun getNearbyBlocks(location: Location): ArrayList<Block>

    // Get a list of black-listed blocks
    fun getBlackList(): List<Material>

}