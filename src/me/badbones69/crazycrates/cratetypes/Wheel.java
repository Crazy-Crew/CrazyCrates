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

public class Wheel implements Listener{
	
	public static HashMap<Player, Integer> crate = new HashMap<Player, Integer>();
	public static HashMap<Player, HashMap<Integer, ItemStack>> Rewards = new HashMap<Player, HashMap<Integer, ItemStack>>();
	
	public static void startWheel(final Player player){
		final Inventory inv = Bukkit.createInventory(null, 54, Methods.color(GUI.crates.get(player).getFile().getString("Crate.CrateName")));
		for(int i=0;i<54;i++){
			inv.setItem(i, Methods.makeItem("160:15", 1, " "));
		} 
		HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
		for(int i : getBorder()){
			Prize prize = Main.CC.pickPrize(player);
			inv.setItem(i, prize.getDisplayItem());
			items.put(i, prize.getDisplayItem());
		}
		Rewards.put(player, items);
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.crates.get(player));
		}
		player.openInventory(inv);
		crate.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
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
				if(i >= 18){
					i = 0;
				}
				if(f >= 18){
					f = 0;
				}
				if(full < timer){
					inv.setItem(slots.get(i), Methods.makeItem("160:5", 1, Rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()));
					inv.setItem(slots.get(f), Rewards.get(player).get(slots.get(f)));
					if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
					i++;
					f++;
				}
				if(full >= timer){
					if(slowSpin().contains(slower)){
						inv.setItem(slots.get(i), Methods.makeItem("160:5", 1, Rewards.get(player).get(slots.get(i)).getItemMeta().getDisplayName()));
						inv.setItem(slots.get(f), Rewards.get(player).get(slots.get(f)));
						if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
						i++;
						f++;
					}
					if(full == timer + 47){
						if(Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
						}
					}
					if(full >= timer + 47){
						slow++;
						if(slow >= 2){
							Random r = new Random();
							int color = r.nextInt(15);
							if(color == 8)color = 0;
							for(int slot=0; slot < 54; slot++){
								if(!getBorder().contains(slot)){
									inv.setItem(slot, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
								}
							}
							slow = 0;
						}
					}
					if(full >= (timer + 55 + 47)){
						Prize prize = null;
						for(Prize p : GUI.crates.get(player).getPrizes()){
							if(Rewards.get(player).get(slots.get(f)).isSimilar(p.getDisplayItem())){
								prize = p;
							}
						}
						Main.CC.getReward(player, prize);
						if(prize.toggleFirework()){
							Methods.fireWork(player.getLocation().add(0, 1, 0));
						}
						Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.WHEEL, CrateControl.Crate.get(player).getName(), prize));
						player.closeInventory();
						GUI.crates.remove(player);
						Bukkit.getScheduler().cancelTask(crate.get(player));
					}
					slower++;
				}
				full++;
				open++;
				if(open > 5){
					player.openInventory(inv);
					open = 0;
				}
			}
		}, 1, 1)); 
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(CrateControl.Crate.containsKey(player)){
			if(!CrateControl.Crate.get(e.getWhoClicked()).getFile().getString("Crate.CrateType").equalsIgnoreCase("Wheel"))return;
		}else{
			return;
		}
		if(inv != null){
			if(inv.getName().equals(Methods.color(CrateControl.Crate.get(player).getFile().getString("Crate.CrateName")))){
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