package me.badbones69.crazycrates.cratetypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Material;
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

public class Wonder implements Listener{
	
	private static HashMap<Player, HashMap<ItemStack, String>> Items = new HashMap<Player, HashMap<ItemStack, String>>();
	private static HashMap<Player, Integer> crate = new HashMap<Player, Integer>();
	
	public static void startWonder(final Player player){
		final Inventory inv = Bukkit.createInventory(null, 45, Methods.color(GUI.crates.get(player).getFile().getString("Crate.CrateName")));
		final HashMap<ItemStack, String> items = new HashMap<ItemStack, String>();
		final ArrayList<String> slots = new ArrayList<String>();
		for(int i=0;i<45;i++){
			Prize prize = Main.CC.pickPrize(player);
			slots.add(i+"");
			inv.setItem(i, prize.getDisplayItem());
		}
		Items.put(player, items);
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.crates.get(player));
		}
		player.openInventory(inv);
		crate.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
			int fulltime = 0;
			int timer = 0;
			int slot1 = 0;
			int slot2 = 44;
			Random r = new Random();
			ArrayList<Integer> Slots = new ArrayList<Integer>();
			Prize p = null;
			@Override
			public void run(){
				if(timer >= 2 && fulltime <= 65){
					slots.remove(slot1+"");slots.remove(slot2 + "");
					Slots.add(slot1);Slots.add(slot2);
					inv.setItem(slot1, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, 15, " "));
					inv.setItem(slot2, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, 15, " "));
					for(String slot : slots){
						p = Main.CC.pickPrize(player);
						inv.setItem(Integer.parseInt(slot), p.getDisplayItem());
					}
					slot1++;
					slot2--;
				}
				if(fulltime > 67){
					int color = r.nextInt(15);
					for(int slot : Slots){
						inv.setItem(slot, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
						inv.setItem(slot, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
					}
				}
				player.openInventory(inv);
				if(fulltime > 100){
					Bukkit.getScheduler().cancelTask(crate.get(player));
					crate.remove(player);
					player.closeInventory();
					Main.CC.getReward(player, p);
					if(p.toggleFirework()){
						Methods.fireWork(player.getLocation().add(0, 1, 0));
					}
					Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.WONDER, CrateControl.Crate.get(player).getName(), p));
					GUI.crates.remove(player);
					return;
				}
				fulltime++;
				timer++;
				if(timer > 2){
					timer = 0;
				}
			}
		}, 0, 2));
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(CrateControl.Crate.containsKey(player)){
			if(!CrateControl.Crate.get(e.getWhoClicked()).getFile().getString("Crate.CrateType").equalsIgnoreCase("Wonder"))return;
		}else{
			return;
		}
		if(inv != null){
			if(inv.getName().equals(Methods.color(CrateControl.Crate.get(player).getFile().getString("Crate.CrateName")))){
				e.setCancelled(true);
			}
		}
	}
	
}