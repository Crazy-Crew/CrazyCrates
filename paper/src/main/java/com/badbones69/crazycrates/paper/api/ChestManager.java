package com.badbones69.crazycrates.paper.api;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.EnderChest;
import org.bukkit.block.data.Directional;
import org.jetbrains.annotations.NotNull;

public class ChestManager {

    public static void openChest(@NotNull final Block block, final boolean forceUpdate) {
        if (block.getType() != Material.CHEST || block.getType() != Material.TRAPPED_CHEST || block.getType() != Material.ENDER_CHEST) return;

        final BlockState blockState = block.getState();

        switch (block.getType()) {
            case ENDER_CHEST -> {
                final EnderChest enderChest = (EnderChest) blockState;

                if (!enderChest.isOpen()) enderChest.open();
                blockState.update(forceUpdate);
            }

            case CHEST, TRAPPED_CHEST -> {
                final Chest chest = (Chest) blockState;

                if (!chest.isOpen()) chest.open();
                blockState.update(forceUpdate);
            }
        }
    }

    public static void closeChest(@NotNull final Block block, final boolean forceUpdate) {
        if (block.getType() != Material.CHEST || block.getType() != Material.TRAPPED_CHEST || block.getType() != Material.ENDER_CHEST) return;

        final BlockState blockState = block.getState();

        switch (block.getType()) {
            case ENDER_CHEST -> {
                final EnderChest enderChest = (EnderChest) blockState;

                if (enderChest.isOpen()) {
                    enderChest.close();

                    blockState.update(forceUpdate);
                }
            }

            case CHEST, TRAPPED_CHEST -> {
                final Chest chest = (Chest) blockState;

                if (chest.isOpen()) {
                    chest.close();

                    blockState.update(forceUpdate);
                }
            }
        }
    }

    public static void rotateChest(@NotNull final Block block, final int direction) {
        final BlockFace blockFace = switch (direction) {
            case 0 -> // West
                    BlockFace.WEST;
            case 1 -> // North
                    BlockFace.NORTH;
            case 2 -> // East
                    BlockFace.EAST;
            case 3 -> // South
                    BlockFace.SOUTH;
            default -> BlockFace.DOWN;
        };

        final Directional blockData = (Directional) block.getBlockData();

        blockData.setFacing(blockFace);

        block.setBlockData(blockData);

        block.getState().update(true);
    }

    public static boolean isChestOpen(@NotNull final Block block) {
        if (block.getType() != Material.CHEST || block.getType() != Material.TRAPPED_CHEST || block.getType() != Material.ENDER_CHEST) return false;

        final BlockState blockState = block.getState();

        boolean isOpen = false;

        switch (block.getType()) {
            case ENDER_CHEST -> {
                final EnderChest enderChest = (EnderChest) blockState;

                isOpen = enderChest.isOpen();
            }

            case CHEST, TRAPPED_CHEST -> {
                final Chest chest = (Chest) blockState;

                isOpen = chest.isOpen();
            }
        }

        return isOpen;
    }
}