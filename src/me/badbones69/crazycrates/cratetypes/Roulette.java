package me.badbones69.crazycrates.cratetypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;
import me.badbones69.crazycrates.multisupport.Version;

public class Roulette implements Listener{
	
	public static HashMap<Player, Integer> roll = new HashMap<Player, Integer>();
	
	private static void setGlass(Inventory inv){
		Random r = new Random();
		for(int i=0;i<27;i++){
			if(i!=13){
				int color =  r.nextInt(15);
				if(color==8)color=1;
				inv.setItem(i, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
			}
		}
	}
	
	public static void openRoulette(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(GUI.crates.get(player).getFile().getString("Crate.CrateName")));
		setGlass(inv);
		inv.setItem(13, Main.CC.pickPrize(player).getDisplayItem());
		player.openInventory(inv);
		startRoulette(player, inv);
	}
	
	private static void startRoulette(final Player player, final Inventory inv){
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.crates.get(player));
		}
		roll.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
			int time = 1;
			int even = 0;
			int full = 0;
			int open = 0;
			@Override
			public void run(){
				if(full <= 15){
					inv.setItem(13, Main.CC.pickPrize(player).getDisplayItem());
					setGlass(inv);
					if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					even++;
					if(even >= 4){
						even = 0;
						inv.setItem(13, Main.CC.pickPrize(player).getDisplayItem());
					}
				}
				open++;
				if(open >= 5){
					player.openInventory(inv);
					open = 0;
				}
				full++;
				if(full > 16){
					if(slowSpin().contains(time)){
						setGlass(inv);
						inv.setItem(13, Main.CC.pickPrize(player).getDisplayItem());
						if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
					}
					time++;
					if(time >= 23){
						if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}
						Bukkit.getScheduler().cancelTask(roll.get(player));
						roll.remove(player);
						Prize prize = null;
						for(Prize p : GUI.crates.get(player).getPrizes()){
							if(inv.getItem(13).isSimilar(p.getDisplayItem())){
								prize = p;
							}
						}
						Main.CC.getReward(player, prize);
						if(prize.toggleFirework()){
							Methods.fireWork(player.getLocation().add(0, 1, 0));
						}
						Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.ROULETTE, CrateControl.Crate.get(player).getName(), prize));
						GUI.crates.remove(player);
						return;
					}
				}
			}
		}, 2, 2));
	}
	
	private static ArrayList<Integer> slowSpin(){
		ArrayList<Integer> slow = new ArrayList<Integer>();
		int full = 46;
		int cut = 9;
		for(int i=46;cut>0;full--){
			if(full<=i-cut||full>=i-cut){
				slow.add(i);
				i=i-cut;
				cut--;
			}
		}
		return slow;
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Player player = (Player) e.getWhoClicked();
		if(CrateControl.Crate.containsKey(player)){
			if(!CrateControl.Crate.get(player).getFile().getString("Crate.CrateType").equalsIgnoreCase("Roulette"))return;
		}else{
			return;
		}
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Methods.color(CrateControl.Crate.get(player).getFile().getString("Crate.CrateName")))){
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		if(roll.containsKey(player)){
			Bukkit.getScheduler().cancelTask(roll.get(player));
			roll.remove(player);
		}
		if(GUI.crates.containsKey(player)){
			GUI.crates.remove(player);
		}
	}
	
}