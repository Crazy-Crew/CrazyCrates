package com.badbones69.crazycrates.v2.utils.interfaces

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface QuadCrate {
    // Open a chest by altering the block state
    fun openChest(block: Block, forceUpdate: Boolean)

    // Close a chest by altering the block state
    fun closeChest(block: Block, forceUpdate: Boolean)

    // Get the item in a player's hand
    fun getItemInHand(player: Player): ItemStack
}