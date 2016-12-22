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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyCrates.Methods;
import me.BadBones69.CrazyCrates.CC;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.API.CrateType;
import me.BadBones69.CrazyCrates.API.PlayerPrizeEvent;
import me.BadBones69.CrazyCrates.MultiSupport.Version;

public class Wheel implements Listener{
	public static HashMap<Player, Integer> crate = new HashMap<Player, Integer>();
	public static HashMap<Player, HashMap<Integer, ItemStack>> Rewards = new HashMap<Player, HashMap<Integer, ItemStack>>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public Wheel(Plugin plugin){
		this.plugin = plugin;
	}
	public static void startWheel(final Player player){
		final Inventory inv = Bukkit.createInventory(null, 54, Methods.color(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.CrateName")));
		for(int i=0;i<54;i++){
			inv.setItem(i, Methods.makeItem("160:15", 1, " "));
		} 
		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		for(int i : getBorder()){
			ItemStack item = CC.pickItem(player);
			inv.setItem(i, item);
			items.put(i, item);
		}
		Rewards.put(player, items);
		if(Methods.Key.get(player).equals("PhysicalKey")){
			Methods.removeItem(CC.Key.get(player), player);
		}
		if(Methods.Key.get(player).equals("VirtualKey")){
			Methods.takeKeys(1, player, GUI.Crate.get(player));
		}
		player.openInventory(inv);
		crate.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			ArrayList<Integer> slots = getBorder();
			int i = 0;
			int f = 17;
			int full = 0;
			int timer = Methods.randomNumber(42, 68);
			int slower = 0;
			int open = 0;
			int slow = 0;
			@Override
			public void run() {
				if(i>=18){
					i=0;
				}
				if(f>=18){
					f=0;
				}
				if(full<timer){
					inv.setItem(slots.get(i), Methods.makeItem("160:5", 1, Rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()));
					inv.setItem(slots.get(f), Rewards.get(player).get(slots.get(f)));
					if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					i++;
					f++;
				}
				if(full>=timer){
					if(slowSpin().contains(slower)){
						inv.setItem(slots.get(i), Methods.makeItem("160:5", 1, Rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()));
						inv.setItem(slots.get(f), Rewards.get(player).get(slots.get(f)));
						if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
						i++;
						f++;
					}
					if(full==timer+47){
						if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}
					}
					if(full>=timer+47){
						slow++;
						if(slow>=2){
							Random r = new Random();
							int color = r.nextInt(15);
							if(color==8)color=0;
							for(int slot=0;slot<54;slot++){
								if(!getBorder().contains(slot)){
									inv.setItem(slot, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
								}
							}
							slow=0;
						}
					}
					if(full>=(timer+55+47)){
						String path = CC.Rewards.get(player).get(Rewards.get(player).get(slots.get(f)));
						CC.getReward(player, path);
						Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.WHEEL, CC.Crate.get(player), path.replace("Crate.Prizes.", "")));
						player.closeInventory();
						GUI.Crate.remove(player);
						CC.Rewards.remove(player);
						Bukkit.getScheduler().cancelTask(crate.get(player));
					}
					slower++;
				}
				full++;
				open++;
				if(open>5){
					player.openInventory(inv);
					open=0;
				}
			}
		}, 1, 1)); 
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(CC.Crate.containsKey(player)){
			if(!Main.settings.getFile(CC.Crate.get(e.getWhoClicked())).getString("Crate.CrateType").equalsIgnoreCase("Wheel"))return;
		}else{
			return;
		}
		if(inv!=null){
			if(inv.getName().equals(Methods.color(Main.settings.getFile(CC.Crate.get(player)).getString("Crate.CrateName")))){
				e.setCancelled(true);
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
	private static ArrayList<Integer> getBorder(){
		ArrayList<Integer> slots = new ArrayList<Integer>();
		slots.add(13);
		slots.add(14);
		slots.add(15);
		slots.add(16);
		slots.add(25);
		slots.add(34);
		slots.add(43);
		slots.add(42);
		slots.add(41);
		slots.add(40);
		slots.add(39);
		slots.add(38);
		slots.add(37);
		slots.add(28);
		slots.add(19);
		slots.add(10);
		slots.add(11);
		slots.add(12);
		return slots;
	}
}