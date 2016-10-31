package me.BadBones69.CrazyCrates.CrateTypes;

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
import org.bukkit.plugin.Plugin;

import me.BadBones69.CrazyCrates.Api;
import me.BadBones69.CrazyCrates.CC;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import me.BadBones69.CrazyCrates.API.CrateType;
import me.BadBones69.CrazyCrates.API.PlayerPrizeEvent;

public class Wonder implements Listener{
	private static HashMap<Player, HashMap<ItemStack, String>> Items = new HashMap<Player, HashMap<ItemStack, String>>();
	private static HashMap<Player, Integer> crate = new HashMap<Player, Integer>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public Wonder(Plugin plugin){
		this.plugin = plugin;
	}
	public static void startWonder(final Player player){
		final Inventory inv = Bukkit.createInventory(null, 45, Api.color(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.CrateName")));
		final HashMap<ItemStack, String> items = new HashMap<ItemStack, String>();
		final ArrayList<String> slots = new ArrayList<String>();
		for(int i=0;i<45;i++){
			ItemStack item = CC.pickItem(player);
			slots.add(i+"");
			inv.setItem(i, item);
		}
		Items.put(player, items);
		if(Api.Key.get(player).equals("PhysicalKey")){
			Api.removeItem(CC.Key.get(player), player);
		}
		if(Api.Key.get(player).equals("VirtualKey")){
			Api.takeKeys(1, player, GUI.Crate.get(player));
		}
		player.openInventory(inv);
		crate.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
			int fulltime = 0;
			int timer = 0;
			int slot1 = 0;
			int slot2 = 44;
			Random r = new Random();
			ArrayList<Integer> Slots = new ArrayList<Integer>();
			ItemStack It = new ItemStack(Material.STONE);
			@Override
			public void run(){
				if(timer>=2&&fulltime<=65){
					slots.remove(slot1+"");slots.remove(slot2+"");
					Slots.add(slot1);Slots.add(slot2);
					inv.setItem(slot1, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 15, " "));
					inv.setItem(slot2, Api.makeItem(Material.STAINED_GLASS_PANE, 1, 15, " "));
					for(String slot : slots){
						It = CC.pickItem(player);
						inv.setItem(Integer.parseInt(slot), It);
					}
					slot1++;
					slot2--;
				}
				if(fulltime>67){
					int color = r.nextInt(15);
					for(int slot : Slots){
						inv.setItem(slot, Api.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
						inv.setItem(slot, Api.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
					}
				}
				player.openInventory(inv);
				if(fulltime>100){
					Bukkit.getScheduler().cancelTask(crate.get(player));
					crate.remove(player);
					player.closeInventory();
					String path = CC.Rewards.get(player).get(It);
					CC.getReward(player, path);
					Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.WONDER, CC.Crate.get(player), path.replace("Crate.Prizes.", "")));
					GUI.Crate.remove(player);
					CC.Rewards.remove(player);
					return;
				}
				fulltime++;
				timer++;
				if(timer>2)timer=0;
			}
		}, 0, 2));
	}
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(CC.Crate.containsKey(player)){
			if(!Main.settings.getFile(CC.Crate.get(e.getWhoClicked())).getString("Crate.CrateType").equalsIgnoreCase("Wonder"))return;
		}else{
			return;
		}
		if(inv!=null){
			if(inv.getName().equals(Api.color(Main.settings.getFile(CC.Crate.get(player)).getString("Crate.CrateName")))){
				e.setCancelled(true);
			}
		}
	}
}