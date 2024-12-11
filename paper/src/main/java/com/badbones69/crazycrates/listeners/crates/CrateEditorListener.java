package com.badbones69.crazycrates.listeners.crates;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.common.config.ConfigManager;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import com.badbones69.crazycrates.common.config.impl.EditorKeys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import java.util.HashMap;

public class CrateEditorListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final SettingsManager editor = ConfigManager.getEditor();

    private final SettingsManager config = ConfigManager.getConfig();

    @EventHandler
    public void onRightClick(final PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Player player = event.getPlayer();

        if (!this.crateManager.hasEditorCrate(player)) return;

        final Location location = event.getInteractionPoint();

        if (location == null) return;

        final Crate crate = this.crateManager.getEditorCrate(player);

        if (crate == null) {
            this.crateManager.removeEditorCrate(player);

            Messages.force_editor_exit.sendMessage(player);

            return;
        }

        if (crate.getCrateType() == CrateType.menu && !this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            Messages.cannot_set_type.sendMessage(player);

            return;
        }

        if (this.crateManager.isCrateLocation(location)) {
            if (this.editor.getProperty(EditorKeys.overwrite_old_crate_locations)) {
                final CrateLocation crateLocation = this.crateManager.getCrateLocation(location);

                if (crateLocation == null) return;

                this.crateManager.removeLocation(crateLocation); // remove old location

                this.crateManager.addCrateLocation(crateLocation.getLocation(), crate); // add new location

                Messages.physical_crate_overridden.sendMessage(player, new HashMap<>() {{
                    put("{id}", crateLocation.getID());
                    put("{crate}", crate.getCrateName());
                }});

                return;
            }

            Messages.physical_crate_already_exists.sendMessage(player, new HashMap<>() {{
                final CrateLocation crateLocation = crateManager.getCrateLocation(location);

                put("{id}", crateLocation != null ? crateLocation.getID() : "N/A");
                put("{crate}", crateLocation != null ? crateLocation.getCrate().getCrateName() : "N/A");
            }});

            return;
        }

        this.crateManager.addCrateLocation(location, crate);

        Messages.created_physical_crate.sendMessage(player, "{crate}", crate.getCrateName());
    }
}