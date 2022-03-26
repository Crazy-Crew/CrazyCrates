package com.badbones69.crazycrates.v2.utils.quadcrates

import com.badbones69.crazycrates.v2.utils.interfaces.QuadCrate
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.EnderChest
import org.bukkit.block.data.Directional

class QuadCrateHandler : QuadCrate {

    override fun openChest(block: Block, forceUpdate: Boolean) {
        if (block.type != Material.CHEST || block.type != Material.TRAPPED_CHEST || block.type != Material.ENDER_CHEST) return

        val blockState = block.state

        when (block.type) {
            Material.ENDER_CHEST -> {
                val enderChest = blockState as EnderChest
                if (!enderChest.isOpen) enderChest.open()
                blockState.update(forceUpdate)
            }
            else -> {
                val chest = blockState as Chest
                if (!chest.isOpen) chest.open()
                blockState.update(forceUpdate)
            }
        }
    }

    override fun closeChest(block: Block, forceUpdate: Boolean) {
        if (block.type != Material.CHEST || block.type != Material.TRAPPED_CHEST || block.type != Material.ENDER_CHEST) return

        // val blockLocation = block.location
        val blockState = block.state

        when (block.type) {
            Material.ENDER_CHEST -> {
                val enderChest = blockState as EnderChest
                if (enderChest.isOpen) enderChest.close()
                blockState.update(forceUpdate)
            }
            else -> {
                val chest = blockState as Chest
                if (chest.isOpen) chest.close()
                blockState.update(forceUpdate)
            }
        }
    }

    override fun rotateChest(chest: Block, direction: Int) {
        val blockFace = when (direction) {
            0 ->  //East
                BlockFace.WEST
            1 ->  //South
                BlockFace.NORTH
            2 ->  //West
                BlockFace.EAST
            3 ->  //North
                BlockFace.SOUTH
            else -> BlockFace.DOWN
        }

        val state = chest.state

        val directional = state.data as Directional

        directional.facing = blockFace

        state.blockData = directional
        state.update()
    }
}