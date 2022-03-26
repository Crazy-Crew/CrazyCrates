package com.badbones69.crazycrates.v2.utils.quadcrates

import com.badbones69.crazycrates.getPlugin
import com.badbones69.crazycrates.v2.utils.interfaces.Structures
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.structure.Mirror
import org.bukkit.block.structure.StructureRotation
import java.io.File
import java.util.*

class StructuresHandler(file: File) : Structures {

    private val structureManager = getPlugin().server.structureManager.loadStructure(file)

    override fun pasteStructure(location: Location) {
        runCatching {
            structureManager.place(location.clone(), false, StructureRotation.NONE, Mirror.NONE, 0, 1F, Random())

            getStructureBlocks(location.clone())
        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun saveStructure(location: Array<Location>) {
        runCatching {

        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun removeStructure(location: Array<Location>) {
        runCatching {

        }.onFailure { getPlugin().logger.warning(it.message) }
    }

    override fun getStructureBlocks(location: Location): ArrayList<Block> {
        val blockLocation = ArrayList<Block>()
        for (x in 0 until structureManager.size.x.toInt()) {
            for (y in 0 until structureManager.size.y.toInt()) {
                for (z in 0 until structureManager.size.z.toInt()) {
                    blockLocation.add(location.block.getRelative(x, y, z))
                }
            }
        }
        return blockLocation
    }

    override fun getNearbyBlocks(location: Location): List<Block> {
        val blocks = ArrayList<Block>()
        for (x in 0 until structureManager.size.x.toInt()) {
            for (y in 0 until structureManager.size.y.toInt()) {
                for (z in 0 until structureManager.size.z.toInt()) {
                    blocks.add(location.block.getRelative(x, y, z))
                }
            }
        }
        return blocks
    }

    override fun getBlackList(): List<Material> {
        return arrayListOf(
            Material.ACACIA_BUTTON,
            Material.BIRCH_BUTTON,
            Material.DARK_OAK_BUTTON,
            Material.JUNGLE_BUTTON,
            Material.OAK_BUTTON,
            Material.SPRUCE_BUTTON,
            Material.STONE_BUTTON,
            // Add all signs
            Material.ACACIA_SIGN,
            Material.BIRCH_SIGN,
            Material.DARK_OAK_SIGN,
            Material.JUNGLE_SIGN,
            Material.OAK_SIGN,
            Material.SPRUCE_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN
        )
    }
}