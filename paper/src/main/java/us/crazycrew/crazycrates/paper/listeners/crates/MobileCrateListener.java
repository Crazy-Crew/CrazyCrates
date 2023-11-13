package us.crazycrew.crazycrates.paper.listeners.crates;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.managers.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.paper.CrazyHandler;
import us.crazycrew.crazycrates.paper.other.ItemUtils;
import us.crazycrew.crazycrates.paper.other.MiscUtils;

public class MobileCrateListener implements Listener {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final CrazyHandler crazyHandler = this.plugin.getCrazyHandler();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();
    
    @EventHandler
    public void onCrateOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            
            if (item.getType() == Material.AIR) return;
            
            for (Crate crate : this.crateManager.getCrates()) {
                if (crate.getCrateType() == CrateType.crate_on_the_go && ItemUtils.isSimilar(item, crate)) {
                    event.setCancelled(true);
                    this.crateManager.addPlayerToOpeningList(player, crate);

                    ItemUtils.removeItem(item, player);

                    Prize prize = crate.pickPrize(player);

                    this.crazyHandler.getPrizeManager().givePrize(player, prize, crate);
                    this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, this.crateManager.getOpeningCrate(player).getName(), prize));

                    if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                    this.crateManager.removePlayerFromOpeningList(player);
                }
            }
        }
    }
}