package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyManager;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class CrateOnTheGo implements Listener {
    
    private static final CrazyManager crazyManager = CrazyManager.getInstance();
    
    @EventHandler
    public void onCrateOpen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = crazyManager.getNMSSupport().getItemInMainHand(player);

            if (item == null || item.getType() == Material.AIR) return;

            for (Crate crate : crazyManager.getCrates()) {
                if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO && Methods.isSimilar(item, crate)) {
                    e.setCancelled(true);
                    crazyManager.addPlayerToOpeningList(player, crate);
                    Methods.removeItem(item, player);
                    Prize prize = crate.pickPrize(player);
                    crazyManager.givePrize(player, prize);

                    crazyManager.getPlugin().getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crazyManager.getOpeningCrate(player).getName(), prize));

                    if (prize.useFireworks()) {
                        Methods.fireWork(player.getLocation().add(0, 1, 0));
                    }

                    crazyManager.removePlayerFromOpeningList(player);
                }
            }
        }
    }

}