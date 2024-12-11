package com.badbones69.crazycrates.listeners.crates;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class CrateEditorListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onRightClick(final PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Block block = event.getClickedBlock();

        if (block == null) return;

        final Player player = event.getPlayer();

        if (this.crateManager.hasEditorCrate(player)) {
            if (!player.hasPermission("crazycrates.editor")) {
                this.crateManager.removeEditorCrate(player);

                Messages.force_editor_exit.sendMessage(player, "{reason}", "Lacking permission crazycrates.editor");

                return;
            }

            this.crateManager.addEditorCrateLocation(player, block.getLocation());
        }
    }
}