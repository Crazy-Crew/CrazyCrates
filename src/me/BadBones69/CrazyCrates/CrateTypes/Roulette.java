package me.BadBones69.CrazyCrates.CrateTypes;

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
import org.bukkit.inventory.ItemStack;

import me.BadBones69.CrazyCrates.CrateControl;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.Methods;
import me.BadBones69.CrazyCrates.API.CrateType;
import me.BadBones69.CrazyCrates.API.CrazyCrates;
import me.BadBones69.CrazyCrates.API.KeyType;
import me.BadBones69.CrazyCrates.API.PlayerPrizeEvent;
import me.BadBones69.CrazyCrates.MultiSupport.Version;

public class Roulette implements Listener{
	
	public static HashMap<Player, Integer> roll = new HashMap<Player, Integer>();
	private static CrazyCrates CC = CrazyCrates.getInstance();
	
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
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.CrateName")));
		setGlass(inv);
		inv.setItem(13, CC.pickItem(player));
		player.openInventory(inv);
		startRoulette(player, inv);
	}
	
	private static void startRoulette(final Player player, final Inventory inv){
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.Crate.get(player));
		}
		roll.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
			int time = 1;
			int even = 0;
			int full = 0;
			int open = 0;
			@Override
			public void run(){
				if(full<=15){
					inv.setItem(13, CC.pickItem(player));
					setGlass(inv);
					if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					even++;
					if(even>=4){
						even=0;
						inv.setItem(13, CC.pickItem(player));
					}
				}
				open++;
				if(open>=5){
					player.openInventory(inv);
					open=0;
				}
				full++;
				if(full>16){
					if(slowSpin().contains(time)){
						setGlass(inv);
						inv.setItem(13, CC.pickItem(player));
						if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
					}
					time++;
					if(time>=23){
						if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}
						Bukkit.getScheduler().cancelTask(roll.get(player));
						roll.remove(player);
						ItemStack item = inv.getItem(13);
						String path = CrateControl.Rewards.get(player).get(item);
						CC.getReward(player, path);
						if(Main.settings.getFile(GUI.Crate.get(player)).getBoolean("Crate.Prizes."+path.replace("Crate.Prizes.", "")+".Firework")){
							Methods.fireWork(player.getLocation().add(0, 1, 0));
						}
						Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.ROULETTE, CrateControl.Crate.get(player), path.replace("Crate.Prizes.", "")));
						CrateControl.Rewards.remove(player);
						GUI.Crate.remove(player);
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
			if(!Main.settings.getFile(CrateControl.Crate.get(player)).getString("Crate.CrateType").equalsIgnoreCase("Roulette"))return;
		}else{
			return;
		}
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Methods.color(Main.settings.getFile(CrateControl.Crate.get(player)).getString("Crate.CrateName")))){
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
		if(GUI.Crate.containsKey(player)){
			GUI.Crate.remove(player);
		}
	}
	
}