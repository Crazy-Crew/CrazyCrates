package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import me.BadBones69.CrazyCrates.Api;
import me.BadBones69.CrazyCrates.CC;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class Cosmic implements Listener{
	public static HashMap<Player, Integer> roll = new HashMap<Player, Integer>();
	private static HashMap<Player, ArrayList<Integer>> glass = new HashMap<Player, ArrayList<Integer>>();
	private static HashMap<Player, ArrayList<Integer>> picks = new HashMap<Player, ArrayList<Integer>>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public Cosmic(Plugin plugin){
		this.plugin = plugin;
	}
	private static void showRewards(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Api.color(getFile(player).getString("Crate.CrateName")+" - Prizes"));
		for(int i : picks.get(player))inv.setItem(i, pickTier(player));
		player.openInventory(inv);
	}
	private static void startRoll(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Api.color(getFile(player).getString("Crate.CrateName")+" - Shuffling"));
		for(int i=0;i<27;i++){
			inv.setItem(i, pickTier(player));
		}
		if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
		if(Api.getVersion()<=183)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
		player.openInventory(inv);
	}
	private static void setChests(Inventory inv){
		int amount=1;
		for(int i=0;i<27;i++){
			inv.setItem(i, Api.makeItem(Material.CHEST, amount, 0, "&f&l???", Arrays.asList("&7You may choose 4 crates.")));
			amount++;
		}
	}
	public static void openCosmic(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Api.color(getFile(player).getString("Crate.CrateName")+" - Choose"));
		setChests(inv);
		player.openInventory(inv);
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		final Inventory inv = e.getInventory();
		final Player player = (Player) e.getWhoClicked();
		if(inv!=null){
			if(GUI.Crate.containsKey(player)){
				if(!getFile(player).getString("Crate.CrateType").equalsIgnoreCase("Cosmic"))return;
			}else{
				return;
			}
			if(inv.getName().equals(Api.color(getFile(player).getString("Crate.CrateName")+" - Shuffling"))){
				e.setCancelled(true);
			}
			if(inv.getName().equals(Api.color(getFile(player).getString("Crate.CrateName")+" - Prizes"))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(inCosmic(slot)){
					for(int i : picks.get(player)){
						if(slot==i){
							ItemStack item = e.getCurrentItem();
							for(String tier : getTiers(player)){
								if(item.getItemMeta().getDisplayName().equals(Api.color(getFile(player).getString("Crate.Tiers."+tier+".Name")))){
									String reward = pickReward(player, tier);
									int stop=0;
									for(;reward.equals("");stop++){
										if(stop==500)break;
										reward = pickReward(player, tier);
									}
									getReward(player, reward);
									String id = getFile(player).getString("Crate.Prizes."+reward+".DisplayItem");
									String name = getFile(player).getString("Crate.Prizes."+reward+".DisplayName");
									List<String> lore = getFile(player).getStringList("Crate.Prizes."+reward+".Lore");
									e.setCurrentItem(Api.makeItem(id, 1, name, lore));
									if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
									if(Api.getVersion()<=183)player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
									return;
								}
							}
						}
					}
				}
			}
			if(inv.getName().equals(Api.color(getFile(player).getString("Crate.CrateName")+" - Choose"))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(inCosmic(slot)){
					ItemStack item = e.getCurrentItem();
					if(item.getType()==Material.CHEST){
						if(!glass.containsKey(player))glass.put(player, new ArrayList<Integer>());
						if(glass.get(player).size()<4){
							e.setCurrentItem(Api.makeItem(Material.THIN_GLASS, (slot+1), 0, "&f&l???", Arrays.asList("&7You have chosen #"+(slot+1)+".")));
							glass.get(player).add(slot);
						}
						if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						if(Api.getVersion()<=183)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					if(item.getType()==Material.THIN_GLASS){
						if(!glass.containsKey(player))glass.put(player, new ArrayList<Integer>());
						e.setCurrentItem(Api.makeItem(Material.CHEST, (slot+1), 0, "&f&l???", Arrays.asList("&7You may choose 4 crates.")));
						ArrayList<Integer> l = new ArrayList<Integer>();
						for(int i : glass.get(player))if(i!=slot)l.add(i);
						glass.put(player, l);
						if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						if(Api.getVersion()<=183)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					if(glass.get(player).size()>=4){
						if(Api.Key.get(player).equals("PhysicalKey")){
							ItemStack it = new ItemStack(Material.AIR);
							for(ItemStack i : player.getOpenInventory().getBottomInventory().getContents()){
								if(i!=null){
									if(i.hasItemMeta()){
										if(i.getItemMeta().hasDisplayName()){
											if(i.getItemMeta().getDisplayName().equals(CC.Key.get(player).getItemMeta().getDisplayName())){
												it=i;
												break;
											}
										}
									}
								}
							}
							if(it.getType()==Material.AIR){
								player.closeInventory();
								player.sendMessage(Api.getPrefix()+Api.color("&cNo key was found."));
								if(GUI.Crate.containsKey(player)){
									GUI.Crate.remove(player);
								}
								if(glass.containsKey(player)){
									glass.remove(player);
								}
								return;
							}
							Api.removeItem(it, player);
						}
						if(Api.Key.get(player).equals("VirtualKey")){
							Api.takeKeys(1, player, GUI.Crate.get(player));
						}
						roll.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
							int time = 0;
							@Override
							public void run() {
								startRoll(player);
								time++;
								if(time==40){
									Bukkit.getScheduler().cancelTask(roll.get(player));
									showRewards(player);
									if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1, 1);
									if(Api.getVersion()<=183)player.playSound(player.getLocation(), Sound.valueOf("ANVIL_LAND"), 1, 1);
								}
							}
						}, 0, 2));
					}
				}
			}
		}
	}
	@EventHandler
	public void onInvClose(InventoryCloseEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getPlayer();
		if(GUI.Crate.containsKey(player)){
			if(!getFile(player).getString("Crate.CrateType").equalsIgnoreCase("Cosmic"))return;
		}else{
			return;
		}
		if(inv.getName().equals(Api.color(getFile(player).getString("Crate.CrateName")+" - Prizes"))){
			boolean T=false;
			for(int i : picks.get(player)){
				if(inv.getItem(i)!=null){
					if(inv.getItem(i).getType()==Material.STAINED_GLASS_PANE){
						ItemStack item = inv.getItem(i);
						for(String tier : getTiers(player)){
							if(item.hasItemMeta()){
								if(item.getItemMeta().hasDisplayName()){
									if(item.getItemMeta().getDisplayName().equals(Api.color(getFile(player).getString("Crate.Tiers."+tier+".Name")))){
										String reward = pickReward(player, tier);
										int stop=0;
										for(;reward.equals("");stop++){
											if(stop==500)break;
											reward = pickReward(player, tier);
										}
										getReward(player, reward);
										T=true;
									}
								}
							}
						}
					}
				}
			}
			if(T){
				if(Api.getVersion()>=191)player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
				if(Api.getVersion()<=183)player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
			}
			if(GUI.Crate.containsKey(player)){
				GUI.Crate.remove(player);
			}
			if(glass.containsKey(player)){
				picks.put((Player) player, glass.get(player));
				glass.remove(player);
			}
		}
		if(inv.getName().equals(Api.color(getFile(player).getString("Crate.CrateName")+" - Choose"))){
			if(!glass.containsKey(player)||glass.get(player).size()<4){
				if(GUI.Crate.containsKey(player)){
					GUI.Crate.remove(player);
				}
			}
			if(glass.containsKey(player)){
				picks.put(player, glass.get(player));
				glass.remove(player);
			}
		}
	}
	public static void getReward(Player player, String reward){
		String path = "Crate.Prizes."+reward;
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(path + ".Items")){
			for(ItemStack i : Api.getFinalItems(path, player)){
				player.getInventory().addItem(i);
			}
		}
		if(Main.settings.getFile(GUI.Crate.get(player)).contains(path + ".Commands")){
			for(String command : Main.settings.getFile(GUI.Crate.get(player)).getStringList(path + ".Commands")){
				command = Api.color(command);
				command = command.replace("%Player%", player.getName());
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
	}
	public static String pickReward(Player player, String tier){
		ArrayList<String> items = new ArrayList<String>();
		Random r = new Random();
		for(String reward : getFile(player).getConfigurationSection("Crate.Prizes").getKeys(false)){
			for(String T : getFile(player).getStringList("Crate.Prizes."+reward+".Tiers")){
				if(T.equalsIgnoreCase(tier)){
					int chance = getFile(player).getInt("Crate.Prizes." + reward + ".Chance");
					int max = getFile(player).getInt("Crate.Prizes." + reward + ".MaxRange")-1;
					int num;
					for(int counter = 1; counter<=1; counter++){
						num = 1 + r.nextInt(max);
						if(num >= 1 && num <= chance)items.add(reward);
					}
				}
			}
		}
		if(items.size()==0)return "";
		return items.get(r.nextInt(items.size()));
	}
	private static ItemStack pickTier(Player player){
		FileConfiguration file = getFile(player);
		ItemStack item = new ItemStack(Material.AIR);
		Random r = new Random();
		int stop = 0;
		for(;item.getType()==Material.AIR;stop++){
			if(stop==100){
				break;
			}
			int num;
			for(int counter = 1; counter<=1; counter++){
				String tier = getTiers(player).get(r.nextInt(getTiers(player).size()));
				int chance = file.getInt("Crate.Tiers." + tier + ".Chance");
				int max = file.getInt("Crate.Tiers." + tier + ".MaxRange")-1;
				num = 1 + r.nextInt(max);
				if(num >= 1 && num <= chance)item=Api.makeItem(Material.STAINED_GLASS_PANE, 1, file.getInt("Crate.Tiers."+tier+".Color"), file.getString("Crate.Tiers."+tier+".Name"));
			}
		}
		return item;
	}
	private static ArrayList<String> getTiers(Player player){
		ArrayList<String> list = new ArrayList<String>();
		for(String tier : getFile(player).getConfigurationSection("Crate.Tiers").getKeys(false)){
			list.add(tier);
		}
		return list;
	}
	private static FileConfiguration getFile(Player player){
		return Main.settings.getFile(GUI.Crate.get(player));
	}
	private boolean inCosmic(int slot){
		//The last slot in comsic crate is 27
		if(slot<27)return true;
		return false;
	}
}