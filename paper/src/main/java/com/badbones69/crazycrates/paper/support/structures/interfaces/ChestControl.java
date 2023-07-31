package com.badbones69.crazycrates.paper.support.structures.interfaces;

import org.bukkit.block.Block;

public interface ChestControl {

    void openChest(Block block, boolean forceUpdate);

    void closeChest(Block block, boolean forceUpdate);

    void rotateChest(Block block, int direction);

}