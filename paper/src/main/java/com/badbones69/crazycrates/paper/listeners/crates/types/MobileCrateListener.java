package com.badbones69.crazycrates.paper.listeners.crates.types;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.api.KeyManager;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.paper.utils.ItemUtil;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class MobileCrateListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final KeyManager keyManager = this.platform.getKeyManager();

    private final CrateManager crateManager = this.platform.getCrateManager();

    @EventHandler
    public void onCrateUse(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (item.isEmpty()) return;

        final String key = this.keyManager.getKey(item);

        if (key.isBlank()) return;

        final Crate crate = this.crateManager.getCrateFromName(key);

        if (crate == null || crate.getCrateType() != CrateType.crate_on_the_go) return;

        if (!ItemUtil.isSimilar(item, crate)) return;

        this.crateManager.openCrate(player, crate, KeyType.physical_key, player.getLocation(), false, false, EventType.event_crate_opened);

        event.setCancelled(true);
    }
}