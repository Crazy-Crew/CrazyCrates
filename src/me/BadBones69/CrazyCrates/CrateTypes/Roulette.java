package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.BadBones69.CrazyCrates.Api;
import me.BadBones69.CrazyCrates.CC;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;

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
import org.bukkit.plugin.Plugin;

public class Roulette implements Listener{
	public static HashMap<Player, Integer> roll = new HashMap<Player, Integer>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public Roulette(Plugin plugin){
		this.plugin = plugin;
	}
	private static void setGlass(Inventory inv){
		Random r = new Random();
		for(int i=0;i<27;i++){
			if(i!=13){
				inv.setItem(i, Api.makeItem(Material.STAINED_GLASS_PANE, 1, r.nextInt(15), " "));
			}
		}
	}
	public static void openRoulette(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Api.color("&8&lRoulette Crate"));
		setGlass(inv);
		inv.setItem(13, pickItem(player));
		player.openInventory(inv);
		startRoulette(player, inv);
	}
	private static void startRoulette(final Player player, final Inventory inv){
		if(Api.Key.get(player).equals("PhysicalKey")){
			Api.removeItem(CC.Key.get(player), player);
		}
		if(Api.Key.get(player).equals("VirtualKey")){
			Api.takeKeys(1, player, GUI.Crate.get(player));
		}
		roll.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			int time = 1;
			int even = 0;
			int full = 0;
			int open = 0;
			@Override
			public void run(){
				if(full<=35){
					inv.setItem(13, pickItem(player));
					setGlass(inv);
					if(Api.getVersion()==183){
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					if(Api.getVersion()==191){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}
					even++;
					if(even>=4){
						even=0;
						inv.setItem(13, pickItem(player));
					}
				}
				open++;
				if(open>=5){
					player.openInventory(inv);
					open=0;
				}
				full++;
				if(full>36){
					if(slowSpin().contains(time)){
						setGlass(inv);
						inv.setItem(13, pickItem(player));
						if(Api.getVersion()==183){
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
						if(Api.getVersion()==191){
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}
					}
					time++;
					if(time>=45){
						if(Api.getVersion()==183){
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}
						if(Api.getVersion()==191){
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}
						Bukkit.getScheduler().cancelTask(roll.get(player));
						roll.remove(player);
						String crate = GUI.Crate.get(player);
						String name = inv.getItem(13).getItemMeta().getDisplayName();
						for(String reward : Main.settings.getFile(crate).getConfigurationSection("Crate.Prizes").getKeys(false)){
							if(name.equals(Api.color(Main.settings.getFile(crate).getString("Crate.Prizes."+reward+".DisplayName")))){
								getReward(player, reward);
								GUI.Crate.remove(player);
								return;
							}
						}
					}
				}
			}
		}, 2, 2));
	}
	public static void getReward(Player player, String reward){
		reward = "Crate.Prizes."+reward;
		Api.path.put(player, reward);
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(Api.path.get(player) + ".Items")){
			for(ItemStack i : Api.getFinalItems(Api.path.get(player), player)){
				player.getInventory().addItem(i);
			}
		}
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(Api.path.get(player) + ".Commands")){
			for(String command : Main.settings.getFile(GUI.Crate.get(player)).getStringList(Api.path.get(player) + ".Commands")){
				command = Api.color(command);
				command = command.replace("%Player%", player.getName());
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
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
	private static ItemStack pickItem(Player player){
		HashMap<ItemStack, String> items = Api.getItems(player);
		int stop = 0;
		for(;items.size()==0;stop++){
			if(stop==100){
				break;
			}
			items=Api.getItems(player);
		}
		Random r = new Random();
		ArrayList<ItemStack> I = new ArrayList<ItemStack>();
		ArrayList<String> P = new ArrayList<String>();
		I.addAll(items.keySet());
		for(ItemStack it : I){
			P.add(items.get(it));
		}
		int pick = r.nextInt(I.size());
		String pa = P.get(pick);
		Api.path.put(player, pa);
		return I.get(pick);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		if(inv!=null){
			if(inv.getName().equals(Api.color("&8&lRoulette Crate"))){
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