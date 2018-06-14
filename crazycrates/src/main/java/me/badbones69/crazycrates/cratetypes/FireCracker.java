package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.controllers.FireworkDamageAPI;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class FireCracker {
	
	private static CrazyCrates cc = CrazyCrates.getInstance();
	
	public static void startFireCracker(final Player player, final Crate crate, KeyType key, final Location loc) {
		cc.takeKeys(1, player, crate, key);
		final ArrayList<Color> colors = new ArrayList<>();
		colors.add(Color.RED);
		colors.add(Color.YELLOW);
		colors.add(Color.GREEN);
		colors.add(Color.BLUE);
		colors.add(Color.BLACK);
		colors.add(Color.AQUA);
		colors.add(Color.MAROON);
		colors.add(Color.PURPLE);
		cc.addCrateTask(player, new BukkitRunnable() {
			Random r = new Random();
			int color = r.nextInt(colors.size());
			int l = 0;
			Location L = loc.clone().add(.5, 25, .5);
			
			@Override
			public void run() {
				L.subtract(0, 1, 0);
				fireWork(L, colors.get(color));
				l++;
				if(l == 25) {
					cc.endCrate(player);
					// The key type is set to free because the key has already been taken above.
					QuickCrate.openCrate(player, loc, crate, KeyType.FREE_KEY, false);
				}
			}
		}.runTaskTimer(Main.getPlugin(), 0, 2));
	}
	
	private static void fireWork(Location loc, Color color) {
		final Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK);
		FireworkMeta fm = fw.getFireworkMeta();
		fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL).withColor(color).withColor(color).trail(false).flicker(false).build());
		fm.setPower(0);
		fw.setFireworkMeta(fm);
		FireworkDamageAPI.addFirework(fw);
		new BukkitRunnable() {
			public void run() {
				fw.detonate();
			}
		}.runTaskLaterAsynchronously(Main.getPlugin(), 1);
	}
	
}