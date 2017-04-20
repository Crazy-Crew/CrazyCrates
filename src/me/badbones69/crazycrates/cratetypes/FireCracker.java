package me.badbones69.crazycrates.cratetypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.FireworkDamageAPI;
import me.badbones69.crazycrates.api.KeyType;

public class FireCracker {
	
	private static HashMap<Player, Integer> F = new HashMap<Player, Integer>();
	
	public static void startFireCracker(final Player player, final String crate, final Location C){
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.crates.get(player));
		}
		final ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.YELLOW);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.BLACK);
		colors.add(Color.AQUA);
		colors.add(Color.MAROON);
		colors.add(Color.PURPLE);
		F.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
			Random r = new Random();
			int color = r.nextInt(colors.size());
			int l = 0;
			Location L = C.clone().add(.5, 25, .5);
			@Override
			public void run() {
				L.subtract(0, 1, 0);
				fireWork(L, colors.get(color));
				l++;
				if(l==25){
					Bukkit.getScheduler().cancelTask(F.get(player));
					F.remove(player);
					QuickCrate.openCrate(player, C, false);
				}
			}
		}, 0, 2));
	}
	
	private static void fireWork(Location loc, Color color) {
		final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL)
				.withColor(color)
				.withColor(color)
				.trail(false)
				.flicker(false)
				.build());
		fm.setPower(0);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable() {
			public void run() {
				fw.detonate();
			}
		}, 1);
	}
	
}