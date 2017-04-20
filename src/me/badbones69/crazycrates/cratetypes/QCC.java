package me.badbones69.crazycrates.cratetypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import me.badbones69.crazycrates.CrateControl;
import me.badbones69.crazycrates.GUI;
import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.ParticleEffect;
import me.badbones69.crazycrates.ParticleEffect.BlockData;
import me.badbones69.crazycrates.api.CrateType;
import me.badbones69.crazycrates.api.KeyType;
import me.badbones69.crazycrates.api.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.Prize;
import me.badbones69.crazycrates.multisupport.Version;

public class QCC implements Listener{ // Quad Crate Control.
	
	public static HashMap<Player, HashMap<Location, BlockState>> crates = new HashMap<Player, HashMap<Location, BlockState>>();
	public static HashMap<Player, HashMap<Location, BlockState>> All = new HashMap<Player, HashMap<Location, BlockState>>();
	public static HashMap<Player, HashMap<Location, Boolean>> opened = new HashMap<Player, HashMap<Location, Boolean>>();
	public static HashMap<Player, ArrayList<Location>> chests = new HashMap<Player, ArrayList<Location>>();
	public static HashMap<Player, ArrayList<Location>> Rest = new HashMap<Player, ArrayList<Location>>();
	public static HashMap<Player, ArrayList<Entity>> Rewards = new HashMap<Player, ArrayList<Entity>>();
	public static HashMap<Player, Integer> P = new HashMap<Player, Integer>();
	public static HashMap<Player, Integer> timer = new HashMap<Player, Integer>();
	
	/**
	 * Starts building the Crate Setup.
	 * @param player Player that is opening the chest.
	 * @param loc Location it opens at.
	 * @param Middle Middle Material.
	 * @param Inner Inner line Material.
	 * @param Outer Outer line Material.
	 * @param Frame Frame Material.
	 * @param sound Sound the Frame Makes.
	 * @param Chest Chest Type.
	 */
	public static void startBuild(final Player player, final Location loc, Material Chest){
		Location Lo = loc.clone();
		final ArrayList<Location> Ch = getChests(loc);
		String schem = Methods.pickRandomSchem();
		List<Location> Check = Methods.getLocations(schem, Lo.clone());
		ArrayList<Location> rest = new ArrayList<Location>();
		rest.add(loc.clone().add(0, -1, 0));
		HashMap<Location, Boolean> checks = new HashMap<Location, Boolean>();
		for(Location l : Ch){
			checks.put(l, false);
		}
		opened.put(player, checks);
		ArrayList<Material> BlockList = new ArrayList<Material>();
		BlockList.add(Material.SIGN_POST);
		BlockList.add(Material.WALL_SIGN);
		BlockList.add(Material.STONE_BUTTON);
		BlockList.add(Material.WOOD_BUTTON);
		for(Location l : Check){
			if(BlockList.contains(l.getBlock().getType())){
				String msg = Main.settings.getConfig().getString("Settings.QuadCrate.NeedsMoreRoom");
				player.sendMessage(Methods.color(Methods.getPrefix()+msg));
				GUI.crates.remove(player);
				return;
			}
			if(l.getBlockY()!=Lo.clone().getBlockY()-1&&l.getBlock().getType()!=Material.AIR){
				if(!l.equals(Lo.clone())){
					String msg = Main.settings.getConfig().getString("Settings.QuadCrate.NeedsMoreRoom");
					player.sendMessage(Methods.color(Methods.getPrefix()+msg));
					GUI.crates.remove(player);
					return;
				}
			}
		}
		if(Lo.clone().subtract(0, 1, 0).getBlock().getType()==Material.AIR){
			player.sendMessage(Methods.color(Methods.getPrefix()+"&cYou must be standing on a block."));
			GUI.crates.remove(player);
			return;
		}
		for(Entity en : player.getNearbyEntities(3, 3, 3)){
			if(en instanceof Player){
				Player p = (Player) en;
				if(crates.containsKey(p)){
					String msg = Main.settings.getConfig().getString("Settings.QuadCrate.ToCloseToAnotherPlayer");
					msg = msg.replaceAll("%Player%", p.getName());
					msg = msg.replaceAll("%player%", p.getName());
					player.sendMessage(Methods.color(Methods.getPrefix()+msg));
					GUI.crates.remove(player);
					return;
				}
			}
		}
		player.teleport(loc.clone().add(.5,0,.5));
		for(Entity en : player.getNearbyEntities(2, 2, 2)){
			if(en instanceof Player){
				Player p = (Player) en;
				Vector v = p.getLocation().toVector().subtract(player.getLocation().toVector()).normalize().setY(1);
				p.setVelocity(v);
			}
		}
		if(GUI.crates.get(player).getFile().getBoolean("Crate.OpeningBroadCast")){
			String msg = Methods.color(GUI.crates.get(player).getFile().getString("Crate.BroadCast"));
			msg = msg.replaceAll("%Prefix%", Methods.getPrefix());
			msg = msg.replaceAll("%prefix%", Methods.getPrefix());
			msg = msg.replaceAll("%Player%", player.getName());
			msg = msg.replaceAll("%player%", player.getName());
			Bukkit.broadcastMessage(msg);
		}
		chests.put(player, Ch);
		if(Methods.Key.get(player) == KeyType.PHYSICAL_KEY){
			Methods.removeItem(CrateControl.Key.get(player), player);
		}
		if(Methods.Key.get(player) == KeyType.VIRTUAL_KEY){
			Methods.takeKeys(1, player, GUI.crates.get(player));
		}
		rest.clear();
		HashMap<Location, BlockState> locs = new HashMap<Location, BlockState>();
		HashMap<Location, BlockState> A = new HashMap<Location, BlockState>();
		for(Location l : Methods.getLocations(schem, Lo.clone())){
			boolean found = false;
			for(Location L : chests.get(player)){
				if(l.getBlockX()==L.getBlockX()&&l.getBlockY()==L.getBlockY()&&l.getBlockZ()==L.getBlockZ()){
					found=true;
					break;
				}
			}
			if(!found){
				locs.put(l, l.getBlock().getState());
				rest.add(l);
			}
			A.put(l, l.getBlock().getState());
		}
		Methods.pasteSchem(schem, Lo.clone());
		All.put(player, A);
		crates.put(player, locs);
		Rest.put(player, rest);
		Location L = new Location(loc.getWorld(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		player.teleport(L.add(.5, 0, .5));
		spawnChest(Ch, player, Chest);
	}
	
	private static void spawnChest(final ArrayList<Location> locs, final Player player, final Material Chest){
		ArrayList<ParticleEffect> particles = new ArrayList<ParticleEffect>();
		particles.add(ParticleEffect.FLAME);
		particles.add(ParticleEffect.VILLAGER_HAPPY);
		particles.add(ParticleEffect.SPELL_WITCH);
		particles.add(ParticleEffect.REDSTONE);
		Random r = new Random();
		final ParticleEffect particle = particles.get(r.nextInt(particles.size()));
		P.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
			double r = 0;
			int i = 0;
			int e = 0;
			int f = 0;
			Location l = new Location(locs.get(0).getWorld(), locs.get(0).getBlockX(), locs.get(0).getBlockY(), locs.get(0).getBlockZ()).add(.5, 3, .5);
			Location loc = locs.get(0).clone();
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				ArrayList<Location> L = getCircle(l, r, 10);
				ArrayList<Location> L2 = getCircleReverse(l, r, 10);
				particle.display(0, 0, 0, 0, 1, L.get(i), 100);
				particle.display(0, 0, 0, 0, 1, L2.get(i), 100);
				i++;f++;e++;
				l.add(0, -.05, 0);
				if(i==10)i=0;
				if(e==6){
					e=0;
					r=r+.08;
				}
				if(f==60){
					ParticleEffect.BLOCK_DUST.display(new BlockData(Chest, (byte)0), .5F, .5F, .5F, 0, 10, loc.clone().add(.5, .3, .5), 100);
					if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
						player.playSound(player.getLocation(), Sound.valueOf("BLOCK_STONE_STEP"), 1, 1);
					}else{
						player.playSound(player.getLocation(), Sound.valueOf("STEP_STONE"), 1, 1);
					}
					loc.getBlock().setType(Chest);
					loc.getBlock().setData((byte)4);
					Bukkit.getScheduler().cancelTask(P.get(player));
					P.remove(player);
				}
			}
		}, 0, 1));
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
			@Override
			public void run() {
				P.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
					double r = 0;
					int i = 0;
					int e = 0;
					int f = 0;
					Location l = new Location(locs.get(1).getWorld(), locs.get(1).getBlockX(), locs.get(1).getBlockY(), locs.get(1).getBlockZ()).add(.5, 3, .5);
					Location loc = locs.get(1).clone();
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						ArrayList<Location> L = getCircle(l, r, 10);
						ArrayList<Location> L2 = getCircleReverse(l, r, 10);
						particle.display(0, 0, 0, 0, 1, L.get(i), 100);
						particle.display(0, 0, 0, 0, 1, L2.get(i), 100);
						i++;f++;e++;
						l.add(0, -.05, 0);
						if(i==10)i=0;
						if(e==6){
							e=0;
							r=r+.08;
						}
						if(f==60){
							ParticleEffect.BLOCK_DUST.display(new BlockData(Chest, (byte)0), .5F, .5F, .5F, 0, 10, loc.clone().add(.5, .3, .5), 100);
							if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
								player.playSound(player.getLocation(), Sound.valueOf("BLOCK_STONE_STEP"), 1, 1);
							}else{
								player.playSound(player.getLocation(), Sound.valueOf("STEP_STONE"), 1, 1);
							}
							loc.getBlock().setType(Chest);
							loc.getBlock().setData((byte)2);
							Bukkit.getScheduler().cancelTask(P.get(player));
							P.remove(player);
						}
					}
				}, 0, 1));
			}
		}, 61);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
			@Override
			public void run() {
				P.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
					double r = 0;
					int i = 0;
					int e = 0;
					int f = 0;
					Location l = new Location(locs.get(2).getWorld(), locs.get(2).getBlockX(), locs.get(2).getBlockY(), locs.get(2).getBlockZ()).add(.5, 3, .5);
					Location loc = locs.get(2).clone();
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						ArrayList<Location> L = getCircle(l, r, 10);
						ArrayList<Location> L2 = getCircleReverse(l, r, 10);
						particle.display(0, 0, 0, 0, 1, L.get(i), 100);
						particle.display(0, 0, 0, 0, 1, L2.get(i), 100);
						i++;f++;e++;
						l.add(0, -.05, 0);
						if(i==10)i=0;
						if(e==6){
							e=0;
							r=r+.08;
						}
						if(f==60){
							ParticleEffect.BLOCK_DUST.display(new BlockData(Chest, (byte)0), .5F, .5F, .5F, 0, 10, loc.clone().add(.5, .3, .5), 100);;
							if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
								player.playSound(player.getLocation(), Sound.valueOf("BLOCK_STONE_STEP"), 1, 1);
							}else{
								player.playSound(player.getLocation(), Sound.valueOf("STEP_STONE"), 1, 1);
							}
							loc.getBlock().setType(Chest);
							loc.getBlock().setData((byte)5);
							Bukkit.getScheduler().cancelTask(P.get(player));
							P.remove(player);
						}
					}
				}, 0, 1));
			}
		}, 121);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
			@Override
			public void run() {
				P.put(player, Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.getPlugin(), new Runnable(){
					double r = 0;
					int i = 0;
					int e = 0;
					int f = 0;
					Location l = new Location(locs.get(3).getWorld(), locs.get(3).getBlockX(), locs.get(3).getBlockY(), locs.get(3).getBlockZ()).add(.5, 3, .5);
					Location loc = locs.get(3).clone();
					@SuppressWarnings("deprecation")
					@Override
					public void run() {
						ArrayList<Location> L = getCircle(l, r, 10);
						ArrayList<Location> L2 = getCircleReverse(l, r, 10);
						particle.display(0, 0, 0, 0, 1, L.get(i), 100);
						particle.display(0, 0, 0, 0, 1, L2.get(i), 100);
						i++;f++;e++;
						l.add(0, -.05, 0);
						if(i==10)i=0;
						if(e==6){
							e=0;
							r=r+.08;
						}
						if(f==60){
							ParticleEffect.BLOCK_DUST.display(new BlockData(Chest, (byte)0), .5F, .5F, .5F, 0, 10, loc.clone().add(.5, .3, .5), 100);
							if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
								player.playSound(player.getLocation(), Sound.valueOf("BLOCK_STONE_STEP"), 1, 1);
							}else{
								player.playSound(player.getLocation(), Sound.valueOf("STEP_STONE"), 1, 1);
							}
							loc.getBlock().setType(Chest);
							loc.getBlock().setData((byte)3);
							Bukkit.getScheduler().cancelTask(P.get(player));
							P.remove(player);
						}
					}
				}, 0, 1));
			}
		}, 181);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
			@Override
			public void run() {
				timer.put(player, Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
					@Override
					public void run() {
						undoBuild(player);
						player.sendMessage(Methods.getPrefix()+Methods.color(Main.settings.getConfig().getString("Settings.QuadCrate.Out-Of-Time")));
					}
				}, Main.settings.getConfig().getInt("Settings.QuadCrate.Timer")*20));
			}
		}, 241);
	}
	
	/**
	 * Undoes the Crate Build.
	 * @param player The player that opened the Crate.
	 */
	@SuppressWarnings("deprecation")
	public static void undoBuild(Player player){
		HashMap<Location, BlockState> locs = All.get(player);
		for(Location loc : locs.keySet()){
			if(locs.get(loc)!=null){
				loc.getBlock().setTypeIdAndData(locs.get(loc).getTypeId(), locs.get(loc).getRawData(), true);
			}
		}
		crates.remove(player);
		chests.remove(player);
		GUI.crates.remove(player);
		if(P.containsKey(player)){
			Bukkit.getScheduler().cancelTask(P.get(player));
			P.remove(player);
		}
		if(timer.containsKey(player)){
			Bukkit.getScheduler().cancelTask(timer.get(player));
			timer.remove(player);
		}
		if(Rewards.containsKey(player)){
			for(Entity h : Rewards.get(player)){
				h.remove();
			}
			Rewards.remove(player);
		}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e){
		Location loc = e.getBlock().getLocation();
		for(Player player : crates.keySet()){
			for(Location l : crates.get(player).keySet()){
				if(l.getBlockX()==loc.getBlockX()&&l.getBlockY()==loc.getBlockY()&&l.getBlockZ()==loc.getBlockZ()){
					e.setCancelled(true);
					return;
				}
			}
		}
		if(crates.containsKey(e.getPlayer())){
			e.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onChestClick(PlayerInteractEvent e){
		if(e.getAction()==Action.RIGHT_CLICK_BLOCK||e.getAction()==Action.LEFT_CLICK_BLOCK){
			if(e.getClickedBlock().getType()==Material.CHEST){
				for(final Player player : chests.keySet()){
					for(final Location l : chests.get(player)){
						Location B = e.getClickedBlock().getLocation();
						if(l.getBlockX() == B.getBlockX() && l.getBlockY() == B.getBlockY() && l.getBlockZ() == B.getBlockZ()){
							e.setCancelled(true);
							if(player == e.getPlayer()){
								Methods.playChestAction(e.getClickedBlock(), true);
								if(!opened.get(player).get(l)){
									ArrayList<Entity> rewards = new ArrayList<Entity>();
									Prize prize = Main.CC.pickPrize(player, B.clone().add(.5, 1.3, .5));
									Main.CC.getReward(player, prize);
									Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.QUAD_CRATE, CrateControl.Crate.get(player).getName(), prize));
									final Entity reward = player.getWorld().dropItem(B.clone().add(.5, 1, .5), Methods.addLore(prize.getDisplayItem().clone(), new Random().nextInt(Integer.MAX_VALUE) + ""));
									reward.setVelocity(new Vector(0,.2,0));
									reward.setCustomName(prize.getDisplayItem().getItemMeta().getDisplayName());
									reward.setCustomNameVisible(true);
									if(Rewards.containsKey(player)){
										for(Entity h : Rewards.get(player)){
											rewards.add(h);
										}
									}
									rewards.add(reward);
									Rewards.put(player, rewards);
									if(prize.toggleFirework()){
										Methods.fireWork(B.clone().add(.5, 1, .5));
									}
								}
								boolean trigger = true;
								for(Location loc : opened.get(player).keySet()){
									if(loc.getBlockX()==l.getBlockX()&&loc.getBlockY()==l.getBlockY()&&loc.getBlockZ()==l.getBlockZ()){
										opened.get(player).put(loc, true);
									}
									if(opened.get(player).get(loc)==false){
										trigger = false;
									}
								}
								if(trigger){
									Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
										@SuppressWarnings("deprecation")
										@Override
										public void run() {
											if(Rest.get(player)!=null){
												for(Location loc : Rest.get(player)){ // Rest is all except Chests.
													HashMap<Location, BlockState> locs = crates.get(player); // Crates holds the Data.
													if(locs!=null){
														for(Location loc2 : locs.keySet()){ // Looping though the locations.
															if(locs.get(loc)!=null){ // Checking to make sure a location isn't null.
																if(loc.equals(loc2)){
																	loc2.getBlock().setTypeIdAndData(locs.get(loc2).getTypeId(), locs.get(loc2).getRawData(), true);
																}
															}
														}
													}
												}
											}
											if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
												player.playSound(player.getLocation(), Sound.valueOf("BLOCK_STONE_STEP"), 1, 1);
											}
											else{
												player.playSound(player.getLocation(), Sound.valueOf("STEP_STONE"), 1, 1);
											}
										}
									}, 3*20);
									Bukkit.getScheduler().scheduleSyncDelayedTask(Main.getPlugin(), new Runnable(){
										@Override
										public void run() {
											if(Rest.get(player)!=null){
												for(Location loc : Rest.get(player)){
													HashMap<Location, BlockState> locs = crates.get(player);
													if(chests.get(player)!=null){
														for(Location loc2 : chests.get(player)){
															if(locs.get(loc)!=null){
																loc2.getBlock().setType(Material.AIR);
															}
														}
													}
												}
											}
											if(Rewards.get(player)!=null){
												for(Entity h : Rewards.get(player)){
													h.remove();
												}
											}
											Rewards.remove(player);
											if(Version.getVersion().getVersionInteger()>=Version.v1_9_R1.getVersionInteger()){
												player.playSound(player.getLocation(), Sound.valueOf("BLOCK_STONE_STEP"), 1, 1);
											}else{
												player.playSound(player.getLocation(), Sound.valueOf("STEP_STONE"), 1, 1);
											}
											crates.remove(player);
											chests.remove(player);
											Rest.remove(player);
											GUI.crates.remove(player);
											if(timer.containsKey(player)){
												Bukkit.getScheduler().cancelTask(timer.get(player));
												timer.remove(player);
											}
											if(CrateControl.LastLoc.containsKey(player)){
												player.teleport(CrateControl.LastLoc.get(player));
												CrateControl.LastLoc.remove(player);
											}
										}
									}, 6*20);
								}
							}
							return;
						}
					}
				}
			}
		}
	}
	
	@EventHandler	
	public void onItemPickup(PlayerPickupItemEvent e){
		Entity item = e.getItem();
		for(Player p : Rewards.keySet()){
			if(Rewards.get(p).contains(item)){
				e.setCancelled(true);
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e){
		Player player = e.getPlayer();
		Location from = e.getFrom();
		Location to = e.getTo();
		if(crates.containsKey(player)){
			if(from.getBlockX()!=to.getBlockX()||from.getBlockZ()!=to.getBlockZ()){
				e.setCancelled(true);
				player.teleport(from);
				return;
			}
		}
		for(Entity en : player.getNearbyEntities(2, 2, 2)){
			if(en instanceof Player){
				Player p = (Player) en;
				if(crates.containsKey(p)){
					Vector v = player.getLocation().toVector().subtract(p.getLocation().toVector()).normalize().setY(1);
					if(player.isInsideVehicle()){
						player.getVehicle().setVelocity(v);
						return;
					}
					player.setVelocity(v);
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e){
		if(crates.containsKey(e.getPlayer())){
			e.setCancelled(true);
		}
		for(Player p : Rest.keySet()){
			for(Location l : Rest.get(p)){
				Location P = e.getBlockPlaced().getLocation();
				if(P.getBlockX()==l.getBlockX()&&P.getBlockY()==l.getBlockY()&&P.getBlockZ()==l.getBlockZ()){
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e){
		new BukkitRunnable(){
			@Override
			public void run() {
				Player player = e.getPlayer();
				String uuid = player.getUniqueId().toString();
				if(!Main.settings.getData().contains("Players."+uuid)){
					Main.settings.getData().set("Players."+uuid+".Name", player.getName());
					for(String crate : Main.settings.getAllCratesNames()){
						int amount = Main.settings.getFile(crate).getInt("Crate.StartingKeys");
						Main.settings.getData().set("Players."+uuid+"."+crate, amount);
					}
					Main.settings.saveData();
				}
			}
		}.runTaskAsynchronously(Methods.plugin);
	}
	
	@EventHandler
	public void onCMD(PlayerCommandPreprocessEvent e){
		Player player = e.getPlayer();
		if(crates.containsKey(player)){
			if(!e.getMessage().toLowerCase().contains("plugman")){
				e.setCancelled(true);
				String msg = Main.settings.getConfig().getString("Settings.NoCMDsWhileCrateOpened");
				msg = msg.replaceAll("%Player%", player.getName());
				msg = msg.replaceAll("%player%", player.getName());
				player.sendMessage(Methods.color(Methods.getPrefix()+msg));
				return;
			}
		}
	}
	
	@EventHandler
	public void onTeleport(PlayerTeleportEvent e){
		Player player = e.getPlayer();
		if(crates.containsKey(player)){
			if(e.getCause()==TeleportCause.ENDER_PEARL){
				e.setCancelled(true);
				String msg = Main.settings.getConfig().getString("Settings.No-Teleporting-Msg");
				msg = msg.replaceAll("%Player%", player.getName());
				msg = msg.replaceAll("%player%", player.getName());
				player.sendMessage(Methods.color(Methods.getPrefix()+msg));
				return;
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e){
		Player player = e.getPlayer();
		if(crates.containsKey(player)){
			undoBuild(player);
		}
	}
	
	private static ArrayList<Location> getChests(Location loc){
		ArrayList<Location> U = new ArrayList<Location>();
		U.add(loc.clone().add(2, 0, 0));
		U.add(loc.clone().add(0, 0, 2));
		U.add(loc.clone().add(-2, 0, 0));
		U.add(loc.clone().add(0, 0, -2));
		return U;
	}
	
	private static ArrayList<Location> getCircle(Location center, double radius, int amount){
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++){
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
	
	public static ArrayList<Location> getCircleReverse(Location center, double radius, int amount){
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++){
            double angle = i * increment;
            double x = center.getX() - (radius * Math.cos(angle));
            double z = center.getZ() - (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
	
}