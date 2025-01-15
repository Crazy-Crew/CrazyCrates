package com.badbones69.crazycrates.paper.listeners.crates.types;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import io.papermc.paper.persistence.PersistentDataContainerView;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.paper.utils.ItemUtils;

public class MobileCrateListener implements Listener {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onCrateUse(PlayerInteractEvent event) {
        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        final Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) return;

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        final NamespacedKey key = ItemKeys.crate_key.getNamespacedKey();

        if (!container.has(key)) return;

        final Crate crate = this.crateManager.getCrateFromName(container.get(key, PersistentDataType.STRING));

        if (crate == null) return;

        if (crate.getCrateType() != CrateType.crate_on_the_go) return;

        if (!ItemUtils.isSimilar(item, crate)) return;

        event.setCancelled(true);

        this.crateManager.addPlayerToOpeningList(player, crate);

        ItemUtils.removeItem(item, player);

        final Prize prize = crate.pickPrize(player);

        PrizeManager.givePrize(player, crate, prize);

        this.crateManager.removePlayerFromOpeningList(player);
    }
}