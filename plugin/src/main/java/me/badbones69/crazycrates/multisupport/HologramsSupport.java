package me.badbones69.crazycrates.multisupport;

import com.sainttx.holograms.HologramPlugin;
import com.sainttx.holograms.api.Hologram;
import com.sainttx.holograms.api.HologramManager;
import com.sainttx.holograms.api.line.TextLine;
import me.badbones69.crazycrates.api.interfaces.HologramController;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateHologram;
import org.bukkit.block.Block;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Random;

public class HologramsSupport implements HologramController {
	
	private static HashMap<Block, Hologram> holograms = new HashMap<>();
	private static HologramManager hologramManager = JavaPlugin.getPlugin(HologramPlugin.class).getHologramManager();
	
	public void createHologram(Block block, Crate crate) {
		CrateHologram crateHologram = crate.getHologram();
		if(crateHologram.isEnabled()) {
			double hight = crateHologram.getHeight() - .5;//Doing this as Holograms seems to add .5 height when adding lines or something..
			Hologram hologram = new Hologram(new Random().nextInt() + "", block.getLocation().add(.5, hight, .5));
			for(String line : crateHologram.getMessages()) {
				hologram.addLine(new TextLine(hologram, line));
			}
			hologramManager.addActiveHologram(hologram);
			holograms.put(block, hologram);
		}
	}
	
	public void removeHologram(Block block) {
		if(holograms.containsKey(block)) {
			Hologram hologram = holograms.get(block);
			hologramManager.deleteHologram(hologram);
			holograms.remove(block);
		}
	}
	
	public void removeAllHolograms() {
		for(Block location : holograms.keySet()) {
			Hologram hologram = holograms.get(location);
			hologramManager.deleteHologram(hologram);
		}
		holograms.clear();
	}
	
}