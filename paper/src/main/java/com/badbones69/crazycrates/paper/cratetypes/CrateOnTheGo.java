package com.badbones69.crazycrates.paper.cratetypes;

import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.support.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
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
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;

public class CrateOnTheGo implements Listener {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    @NotNull
    private final CrazyManager crazyManager = this.plugin.getCrazyManager();

    @NotNull
    private final Methods methods = this.plugin.getCrazyHandler().getMethods();
    
    @EventHandler
    public void onCrateOpen(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            ItemStack item = player.getInventory().getItemInMainHand();
            
            if (item.getType() == Material.AIR) return;
            
            for (Crate crate : this.crazyManager.getCrates()) {
                if (crate.getCrateType() == CrateType.crate_on_the_go && this.methods.isSimilar(item, crate)) {
                    e.setCancelled(true);
                    this.crazyManager.addPlayerToOpeningList(player, crate);

                    this.methods.removeItem(item, player);

                    Prize prize = crate.pickPrize(player);

                    this.plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
                    this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, this.crazyManager.getOpeningCrate(player).getName(), prize));

                    if (prize.useFireworks()) this.methods.firework(player.getLocation().add(0, 1, 0));

                    this.crazyManager.removePlayerFromOpeningList(player);
                }
            }
        }
    }
}