package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import me.BadBones69.CrazyCrates.Methods;
import me.BadBones69.CrazyCrates.API.FireworkDamageAPI;
import me.BadBones69.CrazyCrates.CC;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;

public class FireCracker {
	static HashMap<Player, Integer> F = new HashMap<Player, Integer>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public FireCracker(Plugin plugin){
		this.plugin = plugin;
	}
	public static void startFireCracker(final Player player, final String crate, final Location C){
		Methods.removeItem(CC.Key.get(player), player);
		final ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.RED);
		colors.add(Color.YELLOW);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.BLACK);
		colors.add(Color.AQUA);
		colors.add(Color.MAROON);
		colors.add(Color.PURPLE);
		F.put(player, Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
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
		Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
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
		detonate(fw);
	}
	private static void detonate(final Firework f) {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				f.detonate();
			}
		}, 1);
	}
}