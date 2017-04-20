package me.badbones69.crazycrates.cratetypes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

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

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;
import me.badbones69.crazycrates.multisupport.Version;

public class Cosmic implements Listener{
	
	private static HashMap<Player, HashMap<ItemStack, String>> tiers = new HashMap<Player, HashMap<ItemStack, String>>();
	public static HashMap<Player, Integer> roll = new HashMap<Player, Integer>();
	private static HashMap<Player, ArrayList<Integer>> glass = new HashMap<Player, ArrayList<Integer>>();
	private static HashMap<Player, ArrayList<Integer>> picks = new HashMap<Player, ArrayList<Integer>>();
	
	private static void showRewards(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(getFile(player).getString("Crate.CrateName")+" - Prizes"));
		for(int i : picks.get(player))inv.setItem(i, pickTier(player));
		player.openInventory(inv);
	}
	
	private static void startRoll(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(getFile(player).getString("Crate.CrateName")+" - Shuffling"));
		for(int i=0;i<27;i++){
			inv.setItem(i, pickTier(player));
		}
		if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
			player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
		}else{
			player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
		}
		player.openInventory(inv);
	}
	
	private static void setChests(Inventory inv){
		int amount=1;
		for(int i=0;i<27;i++){
			inv.setItem(i, Methods.makeItem(Material.CHEST, amount, 0, "&f&l???", Arrays.asList("&7You may choose 4 crates.")));
			amount++;
		}
	}
	
	public static void openCosmic(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(getFile(player).getString("Crate.CrateName")+" - Choose"));
		setChests(inv);
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		final Inventory inv = e.getInventory();
		final Player player = (Player) e.getWhoClicked();
		if(inv != null){
			if(CrateControl.Crate.containsKey(player)){
				if(!getFile(player).getString("Crate.CrateType").equalsIgnoreCase("Cosmic"))return;
			}else{
				return;
			}
			final FileConfiguration file = getFile(player);
			if(inv.getName().equals(Methods.color(file.getString("Crate.CrateName")+" - Shuffling"))){
				e.setCancelled(true);
			}
			if(inv.getName().equals(Methods.color(file.getString("Crate.CrateName")+" - Prizes"))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(inCosmic(slot)){
					for(int i : picks.get(player)){
						if(slot==i){
							ItemStack item = e.getCurrentItem();
							String tier = tiers.get(player).get(item);
							if(item != null && tier != null){
								if(item.getType() == Material.STAINED_GLASS_PANE && tier != ""){
									if(item.hasItemMeta()){
										if(item.getItemMeta().hasDisplayName()){
											if(item.getItemMeta().getDisplayName().equals(Methods.color(file.getString("Crate.Tiers."+tier+".Name")))){
												Prize prize = pickReward(player, tier);
												for(int stop = 0; prize == null && stop <= 2000; stop++){
													prize = pickReward(player, tier);
												}
												Main.CC.getReward(player, prize);
												Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.COSMIC, CrateControl.Crate.get(player).getName(), prize));
												e.setCurrentItem(prize.getDisplayItem());
												if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
													player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
												}else{
													player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
												}
												if(prize.toggleFirework()){
													Methods.fireWork(player.getLocation().add(0, 1, 0));
												}
												return;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			if(inv.getName().equals(Methods.color(file.getString("Crate.CrateName")+" - Choose"))){
				e.setCancelled(true);
				int slot = e.getRawSlot();
				if(inCosmic(slot)){
					ItemStack item = e.getCurrentItem();
					if(item!=null){
						if(item.getType()==Material.CHEST){
							if(!glass.containsKey(player))glass.put(player, new ArrayList<Integer>());
							if(glass.get(player).size()<4){
								e.setCurrentItem(Methods.makeItem(Material.THIN_GLASS, (slot+1), 0, "&f&l???", Arrays.asList("&7You have chosen #"+(slot+1)+".")));
								glass.get(player).add(slot);
							}
							if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
								player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
							}else{
								player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
							}
						}
						if(item.getType()==Material.THIN_GLASS){
							if(!glass.containsKey(player))glass.put(player, new ArrayList<Integer>());
							e.setCurrentItem(Methods.makeItem(Material.CHEST, (slot+1), 0, "&f&l???", Arrays.asList("&7You may choose 4 crates.")));
							ArrayList<Integer> l = new ArrayList<Integer>();
							for(int i : glass.get(player))if(i!=slot)l.add(i);
							glass.put(player, l);
							if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
								player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
							}else{
								player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
							}
						}
						if(glass.get(player).size()>=4){
							if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
								ItemStack it = new ItemStack(Material.AIR);
								for(ItemStack i : player.getOpenInventory().getBottomInventory().getContents()){
									if(i!=null){
										if(i.hasItemMeta()){
											if(i.getItemMeta().hasDisplayName()){
												if(i.getItemMeta().getDisplayName().equals(CrateControl.Key.get(player).getItemMeta().getDisplayName())){
													it=i;
													break;
												}
											}
										}
									}
								}
								if(it.getType()==Material.AIR){
									player.closeInventory();
									player.sendMessage(Methods.getPrefix()+Methods.color("&cNo key was found."));
									if(GUI.crates.containsKey(player)){
										GUI.crates.remove(player);
									}
									if(glass.containsKey(player)){
										glass.remove(player);
									}
									return;
								}
								Methods.removeItem(it, player);
							}
							if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
								Methods.takeKeys(1, player, GUI.crates.get(player));
							}
							roll.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
								int time = 0;
								@Override
								public void run() {
									startRoll(player);
									time++;
									if(time==40){
										Bukkit.getScheduler().cancelTask(roll.get(player));
										showRewards(player);
										if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
											player.playSound(player.getLocation(), Sound.valueOf("BLOCK_ANVIL_LAND"), 1, 1);
										}else{
											player.playSound(player.getLocation(), Sound.valueOf("ANVIL_LAND"), 1, 1);
										}
									}
								}
							}, 0, 2));
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onInvClose(InventoryCloseEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getPlayer();
		if(CrateControl.Crate.containsKey(player)){
			if(getFile(player) == null){
				return;
			}else{
				if(!getFile(player).getString("Crate.CrateType").equalsIgnoreCase("Cosmic")){
					return;
				}
			}
		}else{
			return;
		}
		if(inv.getName().equals(Methods.color(getFile(player).getString("Crate.CrateName") + " - Prizes"))){
			boolean T = false;
			for(int i : picks.get(player)){
				if(inv.getItem(i) != null){
					if(inv.getItem(i).getType() == Material.STAINED_GLASS_PANE){
						ItemStack item = inv.getItem(i);
						if(item.hasItemMeta()){
							if(item.getItemMeta().hasDisplayName()){
								if(tiers.containsKey(player)){
									if(tiers.get(player).containsKey(item)){
										String tier = tiers.get(player).get(item);
										if(item.getItemMeta().getDisplayName().equals(Methods.color(getFile(player).getString("Crate.Tiers." + tier + ".Name")))){
											Prize prize = pickReward(player, tier);
											for(int stop = 0; prize == null && stop <= 2000; stop++){
												prize = pickReward(player, tier);
											}
											Main.CC.getReward(player, prize);
											T = true;
										}
									}
								}
							}
						}
					}
				}
			}
			if(T){
				if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
					player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
				}else{
					player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
				}
			}
			if(GUI.crates.containsKey(player)){
				GUI.crates.remove(player);
			}
			if(glass.containsKey(player)){
				picks.put((Player) player, glass.get(player));
				glass.remove(player);
			}
		}
		if(inv.getName().equals(Methods.color(getFile(player).getString("Crate.CrateName") + " - Choose"))){
			if(!glass.containsKey(player) || glass.get(player).size() < 4){
				if(GUI.crates.containsKey(player)){
					GUI.crates.remove(player);
				}
			}
			if(glass.containsKey(player)){
				picks.put(player, glass.get(player));
				glass.remove(player);
			}
		}
	}
	
	public static Prize pickReward(Player player, String tier){
		ArrayList<Prize> prizes = new ArrayList<Prize>();
		Random r = new Random();
		if(GUI.crates.containsKey(player)){
			FileConfiguration file = GUI.crates.get(player).getFile();
			for(Prize prize : GUI.crates.get(player).getPrizes()){
				for(String tierCheck : file.getStringList("Crate.Prizes." + prize.getName() + ".Tiers")){
					if(tierCheck.equalsIgnoreCase(tier)){
						int chance = prize.getChance();
						int max = prize.getMaxRange() - 1;
						int num;
						for(int counter = 1; counter<=1; counter++){
							num = 1 + r.nextInt(max);
							if(num >= 1 && num <= chance){
								prizes.add(prize);
							}
						}
					}
				}
			}
		}
		if(prizes.size() == 0){
			return null;
		}
		return prizes.get(r.nextInt(prizes.size()));
	}
	
	private static ItemStack pickTier(Player player){
		FileConfiguration file = getFile(player);
		if(!tiers.containsKey(player)){
			HashMap<ItemStack, String> list = new HashMap<ItemStack, String>();
			for(String tier : getFile(player).getConfigurationSection("Crate.Tiers").getKeys(false)){
				list.put(Methods.makeItem(Material.STAINED_GLASS_PANE, 1, file.getInt("Crate.Tiers."+tier+".Color"), file.getString("Crate.Tiers."+tier+".Name")), tier);
			}
			tiers.put(player, list);
		}
		ItemStack item = new ItemStack(Material.AIR);
		Random r = new Random();
		int stop = 0;
		for(;item.getType()==Material.AIR;stop++){
			if(stop==100){
				break;
			}
			for(String tier : tiers.get(player).values()){
				int chance = file.getInt("Crate.Tiers." + tier + ".Chance");
				int max = file.getInt("Crate.Tiers." + tier + ".MaxRange")-1;
				int num = 1 + r.nextInt(max);
				if(num >= 1 && num <= chance){
					item = Methods.makeItem(Material.STAINED_GLASS_PANE, 1, file.getInt("Crate.Tiers." + tier + ".Color"), file.getString("Crate.Tiers." + tier + ".Name"));
				}
			}
		}
		return item;
	}
	
	private static FileConfiguration getFile(Player player){
		return CrateControl.Crate.get(player).getFile();
	}
	
	private boolean inCosmic(int slot){
		//The last slot in comsic crate is 27
		if(slot<27)return true;
		return false;
	}
	
}