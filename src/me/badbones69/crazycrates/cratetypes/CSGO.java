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

public class CSGO implements Listener{
	
	public static HashMap<Player, Integer> roll = new HashMap<Player, Integer>();
	
	private static void setGlass(Inventory inv){
		Random r = new Random();
		HashMap<Integer, ItemStack> Glass = new HashMap<Integer, ItemStack>();
		for(int i=0;i<10;i++){
			if(i<9&&i!=3){
				Glass.put(i, inv.getItem(i));
			}
		}
		for(int i : Glass.keySet()){
			if(inv.getItem(i) == null){
				int color = r.nextInt(15);
				inv.setItem(i, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
				inv.setItem(i+18, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
			}
		}
		for(int i=1;i<10;i++){
			if(i<9&&i!=4){
				Glass.put(i, inv.getItem(i));
			}
		}
		int color = r.nextInt(15);
		if(color==8)color=1;
		inv.setItem(0, Glass.get(1));
		inv.setItem(0+18, Glass.get(1));
		inv.setItem(1, Glass.get(2));
		inv.setItem(1+18, Glass.get(2));
		inv.setItem(2, Glass.get(3));
		inv.setItem(2+18, Glass.get(3));
		inv.setItem(3, Glass.get(5));
		inv.setItem(3+18, Glass.get(5));
		inv.setItem(4, Methods.makeItem(Material.STAINED_GLASS, 1, 15, " "));
		inv.setItem(4+18, Methods.makeItem(Material.STAINED_GLASS, 1, 15, " "));
		inv.setItem(5, Glass.get(6));
		inv.setItem(5+18, Glass.get(6));
		inv.setItem(6, Glass.get(7));
		inv.setItem(6+18, Glass.get(7));
		inv.setItem(7, Glass.get(8));
		inv.setItem(7+18, Glass.get(8));
		inv.setItem(8, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
		inv.setItem(8+18, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
	}
	
	public static void openCSGO(Player player){
		Inventory inv = Bukkit.createInventory(null, 27, Methods.color(GUI.crates.get(player).getFile().getString("Crate.CrateName")));
		setGlass(inv);
		for(int i = 9; i > 8 && i < 18; i++){
			inv.setItem(i, Main.CC.pickPrize(player).getDisplayItem());
		}
		player.openInventory(inv);
		startCSGO(player, inv);
	}
	
	private static void startCSGO(final Player player, final Inventory inv){
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.crates.get(player));
		}
		roll.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
			int time = 1;
			int full = 0;
			int open = 0;
			@Override
			public void run(){
				if(full <= 50){//When Spinning
					moveItems(inv, player);
					setGlass(inv);
					if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
						player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
					}
				}
				open++;
				if(open >= 5){
					player.openInventory(inv);
					open = 0;
				}
				full++;
				if(full > 51){
					if(slowSpin().contains(time)){//When Slowing Down
						moveItems(inv, player);
						setGlass(inv);
						if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
							player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
						}else{
							player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
						}
					}
					time++;
					if(time >= 60){// When done
						if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
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
						Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.CSGO, CrateControl.Crate.get(player).getName(), prize));
						GUI.crates.remove(player);
						return;
					}
				}
			}
		}, 1, 1));
	}
	
	private static ArrayList<Integer> slowSpin(){
		ArrayList<Integer> slow = new ArrayList<Integer>();
		int full = 120;
		int cut = 15;
		for(int i = 120; cut > 0; full--){
			if(full <= i - cut || full >= i - cut){
				slow.add(i);
				i=i-cut;
				cut--;
			}
		}
		return slow;
	}
	
	private static void moveItems(Inventory inv, Player player){
		ArrayList<ItemStack> items = new ArrayList<ItemStack>();
		for(int i = 9; i > 8 && i < 17; i++){
			items.add(inv.getItem(i));
		}
		inv.setItem(9, Main.CC.pickPrize(player).getDisplayItem());
		for(int i = 0; i < 8; i++){
			inv.setItem(i+10, items.get(i));
		}
	}
	
	@EventHandler
	public void onInvClick(InventoryClickEvent e){
		Inventory inv = e.getInventory();
		Player player = (Player) e.getWhoClicked();
		if(CrateControl.Crate.containsKey(player)){
			if(!CrateControl.Crate.get(e.getWhoClicked()).getFile().getString("Crate.CrateType").equalsIgnoreCase("CSGO"))return;
		}else{
			return;
		}
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