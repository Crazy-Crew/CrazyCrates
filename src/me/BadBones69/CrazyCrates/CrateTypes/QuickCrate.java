package me.BadBones69.CrazyCrates.CrateTypes;

import java.util.HashMap;

import me.BadBones69.CrazyCrates.Api;
import me.BadBones69.CrazyCrates.CC;
import me.BadBones69.CrazyCrates.GUI;
import me.BadBones69.CrazyCrates.Main;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.TileEntityChest;
import net.minecraft.server.v1_8_R3.TileEntityEnderChest;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

public class QuickCrate implements Listener{
	public static HashMap<Player, Entity> Reward = new HashMap<Player, Entity>();
	public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
	@SuppressWarnings("static-access")
	public QuickCrate(Plugin plugin){
		this.plugin = plugin;
	}
	public static void openCrate(final Player player, final Location loc, String Crate){
		Api.removeItem(CC.Key.get(player), player);
		ItemStack it = Api.displayItem(player, loc.clone().add(.5, 1.3, .5));
		String name = Api.color(Main.settings.getFile(GUI.Crate.get(player)).getString(Api.path.get(player)+".DisplayName"));
		final Entity reward = player.getWorld().dropItem(loc.clone().add(.5, 1, .5), it);
		reward.setVelocity(new Vector(0,.2,0));
		reward.setCustomName(name);
		reward.setCustomNameVisible(true);
		Reward.put(player, reward);
		playChestAction(loc.getBlock(), true);
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			@Override
			public void run() {
				if(Reward.get(player)!=null){
					Reward.get(player).remove();
					Reward.remove(player);
					playChestAction(loc.getBlock(), false);
					GUI.Crate.remove(player);
					CC.InUse.remove(player);
				}
			}
		}, 5*20);
	}
	@EventHandler
	public void onItemPickup(PlayerPickupItemEvent e){
		Entity item = e.getItem();
		for(Player p : Reward.keySet()){
			if(Reward.get(p).equals(item)){
				e.setCancelled(true);
				return;
			}
		}
	}
	private static void playChestAction(Block b, boolean open) {
        Location location = b.getLocation();
        if(b.getType()==Material.CHEST||b.getType()==Material.matchMaterial("146")||b.getType()==Material.ENDER_CHEST){
        	net.minecraft.server.v1_8_R3.World world = ((CraftWorld) location.getWorld()).getHandle();
        	BlockPosition position = new BlockPosition(location.getX(), location.getY(), location.getZ());
	        if (b.getType() == Material.ENDER_CHEST) {
	            TileEntityEnderChest tileChest = (TileEntityEnderChest) world.getTileEntity(position);
	            world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
	        } else {
	            TileEntityChest tileChest = (TileEntityChest) world.getTileEntity(position);
	            world.playBlockAction(position, tileChest.w(), 1, open ? 1 : 0);
	        }
        }
    }
}