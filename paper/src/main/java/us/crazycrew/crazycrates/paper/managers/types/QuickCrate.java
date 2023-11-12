package us.crazycrew.crazycrates.paper.managers.types;

import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Location;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.api.builders.CrateBuilder;
import us.crazycrew.crazycrates.paper.api.enums.PersistentKeys;
import us.crazycrew.crazycrates.paper.other.MiscUtils;
import java.util.logging.Level;

@SuppressWarnings("ALL")
public class QuickCrate extends CrateBuilder {

    public QuickCrate(Crate crate, Player player, Location location) {
        super(crate, player, location);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(type, checkHand)) {
            return;
        }

        this.plugin.getCrateManager().addCrateInUse(getPlayer(), getLocation());

        int keys = switch (type) {
            case virtual_key -> this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(getPlayer().getUniqueId(), getCrate().getName());
            case physical_key -> this.plugin.getCrazyHandler().getUserManager().getPhysicalKeys(getPlayer().getUniqueId(), getCrate().getName());
            default -> 1;
        };

        if (getPlayer().isSneaking() && keys > 1) {
            int used = 0;

            for (;keys > 0; keys--) {
                if (MiscUtils.isInventoryFull(getPlayer())) break;
                if (used >= getCrate().getMaxMassOpen()) break;

                Prize prize = getCrate().pickPrize(getPlayer());
                this.plugin.getCrazyHandler().getPrizeManager().givePrize(getPlayer(), prize, getCrate());
                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(getPlayer(), getCrate(), getCrate().getName(), prize));

                if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(.5, 1, .5), null);

                used++;
            }

            boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(used, getPlayer().getUniqueId(), getCrate().getName(), type, false);

            if (!keyCheck) {
                // Send the message about failing to take the key.
                MiscUtils.failedToTakeKey(getPlayer(), getCrate());

                // Remove from opening list.
                this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

                return;
            }

            plugin.getCrateManager().endQuickCrate(getPlayer(), getLocation(), getCrate(), true);

            return;
        }

        boolean keyCheck = this.plugin.getCrazyHandler().getUserManager().takeKeys(1, getPlayer().getUniqueId(), getCrate().getName(), type, true);

        if (!keyCheck) {
            // Send the message about failing to take the key.
            MiscUtils.failedToTakeKey(getPlayer(), getCrate());

            // Remove from opening list.
            this.plugin.getCrateManager().removePlayerFromOpeningList(getPlayer());

            return;
        }

        Prize prize = getCrate().pickPrize(getPlayer(), getLocation().clone().add(.5, 1.3, .5));
        this.plugin.getCrazyHandler().getPrizeManager().givePrize(getPlayer(), prize, getCrate());
        this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(getPlayer(), getCrate(), getCrate().getName(), prize));

        // Get the display item.
        ItemStack display = prize.getDisplayItem();

        // Get the item meta.
        ItemMeta itemMeta = display.getItemMeta();

        // Access the pdc and set "crazycrates-item"
        PersistentKeys key = PersistentKeys.crate_prize;

        //noinspection unchecked
        itemMeta.getPersistentDataContainer().set(key.getNamespacedKey(this.plugin), key.getType(), 1);

        // Set the item meta.
        display.setItemMeta(itemMeta);

        boolean hideQuickCrateItem = this.plugin.getConfigManager().getConfig().getProperty(Config.hide_quickcrate_item);

        if (hideQuickCrateItem) {
            Item reward;

            try {
                reward = getPlayer().getWorld().dropItem(getLocation().clone().add(.5, 1, .5), display);
            } catch (IllegalArgumentException exception) {
                this.plugin.getLogger().warning("A prize could not be given due to an invalid display item for this prize.");
                this.plugin.getLogger().log(Level.WARNING, "Crate: " + prize.getCrate() + " Prize: " + prize.getName(), exception);

                return;
            }

            reward.setMetadata("betterdrops_ignore", new FixedMetadataValue(this.plugin, true));
            reward.setVelocity(new Vector(0, .2, 0));
            reward.setCustomName(display.getItemMeta().getDisplayName());
            reward.setCustomNameVisible(true);
            reward.setPickupDelay(-1);

            this.plugin.getCrateManager().addReward(getPlayer(), reward);

            this.plugin.getCrazyHandler().getChestManager().openChest(getLocation().getBlock(), true);

            if (prize.useFireworks()) MiscUtils.spawnFirework(getLocation().clone().add(0.5, 1, .5), null);

            addCrateTask(new BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getCrateManager().endQuickCrate(getPlayer(), getLocation(), getCrate(), false);
                }
            }.runTaskLater(this.plugin, 5 * 20));

            return;
        }

        plugin.getCrazyHandler().getPrizeManager().checkPrize(prize, getPlayer(), getCrate());
    }
}