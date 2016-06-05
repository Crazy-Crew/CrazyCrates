package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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

public class Wonder implements Listener{
	private static HashMap<Player, HashMap<ItemStack, String>> Rewards = new HashMap<Player, HashMap<ItemStack, String>>();
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
			ItemStack item = pickItem(player);
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
						It = pickItem(player);
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
					getReward(player, Rewards.get(player).get(It));
					GUI.Crate.remove(player);
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
		if(GUI.Crate.containsKey(player)){
			if(!Main.settings.getFile(GUI.Crate.get(e.getWhoClicked())).getString("Crate.CrateType").equalsIgnoreCase("Wonder"))return;
		}else{
			return;
		}
		if(inv!=null){
			if(inv.getName().equals(Api.color(Main.settings.getFile(GUI.Crate.get(player)).getString("Crate.CrateName")))){
				e.setCancelled(true);
			}
		}
	}
	private static ItemStack pickItem(Player player){
		FileConfiguration file = Main.settings.getFile(GUI.Crate.get(player));
		if(!Rewards.containsKey(player)){
			getItems(player);
		}
		Set<ItemStack> items = Rewards.get(player).keySet();
		ArrayList<ItemStack> Items = new ArrayList<ItemStack>();
		Random r = new Random();
		for(int stop=0;Items.size()==0;stop++){
			if(stop==100){
				break;
			}
			for(ItemStack i : items){
				String path = Rewards.get(player).get(i);
				ItemStack item = Api.makeItem(file.getString(path+".DisplayItem"), 1, file.getString(path+".DisplayName"));
				int max = file.getInt(path+".MaxRange");
				int chance = file.getInt(path+".Chance");
				int num;
				for(int counter = 1; counter<=1; counter++){
					num = 1 + r.nextInt(max);
					if(num >= 1 && num <= chance)Items.add(item);
				}
			}
		}
		return Items.get(r.nextInt(Items.size()));
	}
	private static void getItems(Player player){
		FileConfiguration file = Main.settings.getFile(GUI.Crate.get(player));
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		HashMap<ItemStack, String> path = new HashMap<ItemStack, String>();
		for(String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false)){
			String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
			String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
			try{
				items.add(Api.makeItem(id, 1, name));
				path.put(Api.makeItem(id, 1, name), "Crate.Prizes."+reward);
			}catch(Exception e){
				continue;
			}
		}
		Rewards.put(player, path);
	}
	private static void getReward(Player player, String path){
		FileConfiguration file = Main.settings.getFile(GUI.Crate.get(player));
		if(file.contains(path + ".Items")){
			for(ItemStack i : Api.getFinalItems(path, player)){
				player.getInventory().addItem(i);
			}
		}
		if(file.contains(path + ".Commands")){
			for(String command : file.getStringList(path + ".Commands")){
				command = Api.color(command);
				command = command.replace("%Player%", player.getName());
				command = command.replace("%player%", player.getName());
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}
	}
}