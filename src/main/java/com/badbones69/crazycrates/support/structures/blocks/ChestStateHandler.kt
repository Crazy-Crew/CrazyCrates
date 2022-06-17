package com.badbones69.crazycrates.support.structures.blocks

import com.badbones69.crazycrates.support.structures.interfaces.ChestControl
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Chest
import org.bukkit.block.EnderChest
import org.bukkit.block.data.Directional

class ChestStateHandler : ChestControl {

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

    override fun rotateChest(block: Block, direction: Int) {
        val blockFace = when (direction) {
            0 ->  // South
                BlockFace.SOUTH
            1 ->  // East
                BlockFace.EAST
            2 ->  // West
                BlockFace.WEST
            3 ->  // North
                BlockFace.NORTH
            else -> BlockFace.DOWN
        }

        val blockData = block.blockData as Directional

        blockData.facing = blockFace

        block.blockData = blockData

        block.state.update(true)
    }
}