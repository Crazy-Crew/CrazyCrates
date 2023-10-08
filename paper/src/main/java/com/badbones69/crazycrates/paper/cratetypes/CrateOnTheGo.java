package com.badbones69.crazycrates.paper.cratetypes;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;

public class CrateOnTheGo implements Listener {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();

    @EventHandler
    public void onCrateOpen(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) return;

        for (Crate crate : this.crazyManager.getCrates()) {
            if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO && this.methods.isSimilar(item, crate)) {
                event.setCancelled(true);
                this.crazyManager.addPlayerToOpeningList(player, crate);

                this.methods.removeItem(item, player);

                Prize prize = crate.pickPrize(player);

                this.crazyManager.givePrize(player, prize, crate);
                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player.getUniqueId(), crate, this.crazyManager.getOpeningCrate(player).getName(), prize));

                if (prize.useFireworks()) this.methods.firework(player.getLocation().add(0, 1, 0));

                this.crazyManager.removePlayerFromOpeningList(player);
            }
        }
    }
}