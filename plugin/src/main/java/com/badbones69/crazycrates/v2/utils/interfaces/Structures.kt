package com.badbones69.crazycrates.v2.utils.interfaces

import org.bukkit.Location
import org.bukkit.Material

interface Structures {

    // Paste a structure using available .nbt files
    fun pasteStructure()

    // Save a new nbt structure
    fun saveStructure(location: Array<Location>)

    // Remove a structure
    fun removeStructure(location: Array<Location>)

    // Get the structure location
    fun getStructureLocation(): Location

    // Get a list of black-listed blocks
    fun getBlackList(): List<Material>

}