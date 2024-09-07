package com.badbones69.crazycrates.listeners.crates.types;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.misc.Keys;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
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
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;

public class MobileCrateListener implements Listener {

    private @NotNull final CrazyCrates plugin = CrazyCrates.getPlugin();

    private @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    @EventHandler
    public void onCrateUse(PlayerInteractEvent event) {
        final Player player = event.getPlayer();

        if (event.getHand() == EquipmentSlot.OFF_HAND) return;

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        final ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) return;

        final PersistentDataContainerView container = item.getPersistentDataContainer();

        final NamespacedKey key = Keys.crate_key.getNamespacedKey();

        if (!container.has(key)) return;

        final Crate crate = this.crateManager.getCrateFromName(container.get(key, PersistentDataType.STRING));

        if (crate == null) return;

        if (crate.getCrateType() != CrateType.crate_on_the_go) return;

        if (!ItemUtils.isSimilar(item, crate)) return;

        event.setCancelled(true);

        this.crateManager.addPlayerToOpeningList(player, crate);

        ItemUtils.removeItem(item, player);

        final Prize prize = crate.pickPrize(player);

        PrizeManager.givePrize(player, prize, crate);

        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));

        if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

        this.crateManager.removePlayerFromOpeningList(player);
    }
}