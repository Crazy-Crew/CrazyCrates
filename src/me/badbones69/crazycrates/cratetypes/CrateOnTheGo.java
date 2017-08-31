package me.badbones69.crazycrates.cratetypes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.Crate;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;

public class CrateOnTheGo implements Listener {
	
	@EventHandler
	public void onCrateOpen(PlayerInteractEvent e) {
		Player player = e.getPlayer();
		if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			ItemStack item = Methods.getItemInHand(player);
			if(item != null) {
				for(Crate crate : Main.CC.getCrates()) {
					if(crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
						if(Methods.isSimilar(crate.getKey(), item)) {
							e.setCancelled(true);
							GUI.crates.put(player, crate);
							CrateControl.crates.put(player, crate);
							Methods.removeItem(item, player);
							Prize prize = Main.CC.pickPrize(player);
							Main.CC.getReward(player, prize);
							Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.CRATE_ON_THE_GO, CrateControl.crates.get(player).getName(), prize));
							if(prize.toggleFirework()) {
								Methods.fireWork(player.getLocation().add(0, 1, 0));
							}
							GUI.crates.remove(player);
							CrateControl.crates.remove(player, crate);
							return;
						}
					}
				}
			}
		}
	}
	
}