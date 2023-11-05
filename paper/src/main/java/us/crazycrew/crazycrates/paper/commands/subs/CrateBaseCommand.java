package us.crazycrew.crazycrates.paper.commands.subs;

import ch.jalu.configme.SettingsManager;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.enums.Translation;
import us.crazycrew.crazycrates.paper.utils.MiscUtils;
import us.crazycrew.crazycrates.paper.utils.MsgUtils;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Command(value = "crates", alias = {"crazycrates", "cc", "crate", "crazycrate"})
public class CrateBaseCommand extends BaseCommand {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    
    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final FileManager fileManager = this.plugin.getFileManager();

    @NotNull
    private final EventLogger eventLogger = this.plugin.getEventLogger();

    @NotNull
    private final SettingsManager config = this.plugin.getConfigManager().getConfig();

    @NotNull
    private final FileConfiguration locations = Files.LOCATIONS.getFile();

    @Default
    @Permission(value = "crazycrates.command.player.menu", def = PermissionDefault.TRUE)
    public void onDefaultMenu(Player player) {
        if (this.config.getProperty(Config.enable_crate_menu)) this.plugin.getCrazyHandler().getInventoryManager().openGUI(player); else player.sendMessage(Translation.feature_disabled.getString());
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.command.player.help", def = PermissionDefault.TRUE)
    public void onHelp(CommandSender sender) {
        sender.sendMessage(Translation.help.getString());
    }

    @SubCommand("transfer")
    @Permission(value = "crazycrates.command.player.transfer", def = PermissionDefault.OP)
    public void onPlayerTransferKeys(Player sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player, @Suggestion("numbers") int amount) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        // If the crate is menu or null. we return
        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        // If it's the same player, we return.
        if (player.getName().equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Translation.same_player.getString());
            return;
        }

        // If they don't have enough keys, we return.
        if (this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(sender.getUniqueId(), crate.getName()) <= amount) {
            sender.sendMessage(Translation.transfer_not_enough_keys.getMessage("%crate%", crate.getName()).toString());
            return;
        }

        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
        this.plugin.getServer().getPluginManager().callEvent(event);

        // If the event is cancelled, We return.
        if (event.isCancelled()) return;

        this.plugin.getCrazyHandler().getUserManager().takeKeys(amount, sender.getUniqueId(), crate.getName(), KeyType.virtual_key, false);
        this.plugin.getCrazyHandler().getUserManager().addKeys(amount, player.getUniqueId(), crate.getName(), KeyType.virtual_key);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%crate%", crate.getName());
        placeholders.put("%amount%", String.valueOf(amount));
        placeholders.put("%player%", player.getName());

        sender.sendMessage(Translation.transfer_sent_keys.getMessage(placeholders).toString());

        placeholders.put("%player%", sender.getName());

        player.sendMessage(Translation.transfer_received_keys.getMessage("%player%", sender.getName()).toString());

        this.eventLogger.logKeyEvent(player, sender, crate, KeyType.virtual_key, EventLogger.KeyEventType.KEY_EVENT_RECEIVED, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));
    }

    @SubCommand("admin-help")
    @Permission(value = "crazycrates.command.admin.help", def = PermissionDefault.OP)
    public void onAdminHelp(CommandSender sender) {
        sender.sendMessage(Translation.admin_help.getString());
    }

    @SubCommand("reload")
    @Permission(value = "crazycrates.command.admin.reload", def = PermissionDefault.OP)
    public void onReload(CommandSender sender) {
        this.plugin.getConfigManager().reload();

        this.fileManager.reloadAllFiles();
        this.fileManager.setup();

        boolean isEnabled = this.plugin.getCrazyHandler().getConfigManager().getPluginConfig().getProperty(PluginConfig.toggle_metrics);

        if (!isEnabled) {
            this.plugin.getCrazyHandler().getMetrics().stop();
        } else {
            this.plugin.getCrazyHandler().getMetrics().start();
        }

        this.plugin.getCrazyHandler().cleanFiles();

        // Close previews
        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            this.plugin.getCrazyHandler().getInventoryManager().closeCratePreview(player);
            player.sendMessage(Translation.reloaded_forced_out_of_preview.getString());
        });

        this.plugin.getCrateManager().loadCrates();

        sender.sendMessage(Translation.reloaded_plugin.getString());
    }

    @SubCommand("debug")
    @Permission(value = "crazycrates.command.admin.debug", def = PermissionDefault.OP)
    public void onDebug(CommandSender sender, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            sender.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        crate.getPrizes().forEach(prize -> this.plugin.getCrazyHandler().getPrizeManager().givePrize((Player) sender, prize, crate));
    }

    @SubCommand("schem-save")
    @Permission(value = "crazycrates.command.admin.schematic.save", def = PermissionDefault.OP)
    public void onAdminSave(Player player) {
        player.sendMessage(Translation.feature_disabled.getString());
    }

    @SubCommand("schem-set")
    @Permission(value = "crazycrates.command.admin.schematic.set", def = PermissionDefault.OP)
    public void onAdminSet(Player player) {
        player.sendMessage(Translation.feature_disabled.getString());
    }

    @SubCommand("admin")
    @Permission(value = "crazycrates.command.admin.access", def = PermissionDefault.OP)
    public void onAdminMenu(Player player) {
        int size = this.crateManager.getCrates().size();
        int slots = 9;

        for (; size > 9; size -= 9) slots += 9;

        Inventory inv = this.plugin.getServer().createInventory(null, slots, MsgUtils.color("&4&lAdmin Keys"));

        for (Crate crate : this.crateManager.getCrates()) {
            if (crate.getCrateType() != CrateType.menu) {
                if (inv.firstEmpty() >= 0) inv.setItem(inv.firstEmpty(), crate.getAdminKey());
            }
        }

        player.openInventory(inv);
    }

    @SubCommand("list")
    @Permission(value = "crazycrates.command.admin.list", def = PermissionDefault.OP)
    public void onAdminList(CommandSender sender) {
        StringBuilder crates = new StringBuilder();
        String brokecrates;

        this.crateManager.getCrates().forEach(crate -> crates.append("&a").append(crate.getName()).append("&8, "));

        StringBuilder brokecratesBuilder = new StringBuilder();

        this.crateManager.getBrokeCrates().forEach(crate -> brokecratesBuilder.append("&c").append(crate).append(".yml&8,"));

        brokecrates = brokecratesBuilder.toString();

        sender.sendMessage(MsgUtils.color("&e&lCrates:&f " + crates));

        if (!brokecrates.isEmpty())
            sender.sendMessage(MsgUtils.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));

        sender.sendMessage(MsgUtils.color("&e&lAll Crate Locations:"));
        sender.sendMessage(MsgUtils.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
        int line = 1;

        for (CrateLocation loc : this.crateManager.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();

            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();

            sender.sendMessage(MsgUtils.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
            line++;
        }
    }

    @SubCommand("tp")
    @Permission(value = "crazycrates.command.admin.teleport", def = PermissionDefault.OP)
    public void onAdminTeleport(Player player, @Suggestion("locations") String id) {
        if (!this.locations.contains("Locations")) {
            this.locations.set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        for (String name : this.locations.getConfigurationSection("Locations").getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                World world = plugin.getServer().getWorld(Objects.requireNonNull(this.locations.getString("Locations." + name + ".World")));

                int x = this.locations.getInt("Locations." + name + ".X");
                int y = this.locations.getInt("Locations." + name + ".Y");
                int z = this.locations.getInt("Locations." + name + ".Z");

                Location loc = new Location(world, x, y, z);

                player.teleport(loc.add(.5, 0, .5));

                player.sendMessage(MsgUtils.getPrefix("&7You have been teleported to &6" + name + "&7."));

                return;
            }
        }

        player.sendMessage(MsgUtils.getPrefix("&cThere is no location called &6" + id + "&c."));
    }

    @SubCommand("additem")
    @Permission(value = "crazycrates.command.admin.additem", def = PermissionDefault.OP)
    public void onAdminCrateAddItem(Player player, @Suggestion("crates") String crateName, @Suggestion("prizes") String prize) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(Translation.no_item_in_hand.getString());
            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        if (crate.getCrateType() == CrateType.cosmic) {
            player.sendMessage(Translation.failed_to_add_item.getString());
            return;
        }

        try {
            crate.addEditorItem(prize, item);
        } catch (Exception exception) {
            this.plugin.getServer().getLogger().log(Level.WARNING, "Failed to add a new prize to the " + crate.getName() + " crate.", exception);

            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%crate%", crate.getName());
        placeholders.put("%prize%", prize);

        player.sendMessage(Translation.added_item_with_editor.getMessage(placeholders).toString());
    }

    @SubCommand("preview")
    @Permission(value = "crazycrates.command.admin.preview", def = PermissionDefault.OP)
    public void onAdminCratePreview(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        if (!crate.isPreviewEnabled()) {
            sender.sendMessage(Translation.preview_disabled.getString());
            return;
        }

        this.plugin.getCrazyHandler().getInventoryManager().addViewer(player);
        this.plugin.getCrazyHandler().getInventoryManager().openNewCratePreview(player, crate);
    }

    @SubCommand("open-others")
    @Permission(value = "crazycrates.command.admin.open.others", def = PermissionDefault.OP)
    public void onAdminCrateOpenOthers(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        openCrate(sender, player, crateName);
    }

    @SubCommand("open")
    @Permission(value = "crazycrates.command.admin.open", def = PermissionDefault.OP)
    public void onAdminCrateOpen(Player player, @Suggestion("crates") String crateName) {
        openCrate(player, player, crateName);
    }

    private void openCrate(CommandSender sender, Player player, String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        if (this.crateManager.isInOpeningList(player)) {
            player.sendMessage(Translation.already_opening_crate.getString());
            return;
        }

        CrateType type = crate.getCrateType();

        if (type == null) {
            player.sendMessage(Translation.internal_error.getString());
            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());
            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.virtual_key;

        if (this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
            hasKey = true;
        } else {
            if (this.config.getProperty(Config.virtual_accepts_physical_keys)) {
                if (this.plugin.getCrazyHandler().getUserManager().hasPhysicalKey(player.getUniqueId(), crate.getName(), false)) {
                    hasKey = true;
                    keyType = KeyType.physical_key;
                }
            }
        }

        if (!hasKey) {
            if (this.config.getProperty(Config.need_key_sound_toggle)) {
                player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(Config.need_key_sound)), 1f, 1f);
            }

            player.sendMessage(Translation.no_virtual_key.getString());
            return;
        }

        if (MiscUtils.isInventoryFull(player)) {
            player.sendMessage(Translation.inventory_not_empty.getString());
            return;
        }

        if (type != CrateType.crate_on_the_go && type != CrateType.quick_crate && type != CrateType.fire_cracker && type != CrateType.quad_crate) {
            sender.sendMessage(Translation.cant_be_a_virtual_crate.getString());
            return;
        }

        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Crate%", crate.getName());
        placeholders.put("%Player%", player.getName());

        sender.sendMessage(Translation.opened_a_crate.getMessage(placeholders).toString());

        this.eventLogger.logKeyEvent(player, sender, crate, keyType, EventLogger.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));
    }

    @SubCommand("mass-open")
    @Permission(value = "crazycrates.command.admin.massopen", def = PermissionDefault.OP)
    public void onAdminCrateMassOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount) {
        if (!(sender instanceof Player player)) return;

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        int keys = this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName());
        int keysUsed = 0;

        if (keys == 0) {
            player.sendMessage(Translation.no_virtual_key.getString());
            return;
        }

        for (; keys > 0; keys--) {
            if (MiscUtils.isInventoryFull(player)) break;
            if (keysUsed > amount) break;
            if (keysUsed >= crate.getMaxMassOpen()) break;

            Prize prize = crate.pickPrize(player);
            this.plugin.getCrazyHandler().getPrizeManager().givePrize(player, prize, crate);
            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

            if (prize.useFireworks()) MiscUtils.spawnFirework(((Player) sender).getLocation().clone().add(.5, 1, .5), null);

            keysUsed++;
        }

        if (!this.plugin.getCrazyHandler().getUserManager().takeKeys(keysUsed, player.getUniqueId(), crate.getName(), KeyType.virtual_key, false)) {
            MiscUtils.failedToTakeKey(player, crate);
            CrateControlListener.inUse.remove(player);
            this.crateManager.removePlayerFromOpeningList(player);
            return;
        }

        this.crateManager.removePlayerFromOpeningList(player);
    }

    @SubCommand("forceopen")
    @Permission(value = "crazycrates.command.admin.forceopen", def = PermissionDefault.OP)
    public void onAdminForceOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        if (this.crateManager.isInOpeningList(player)) {
            sender.sendMessage(Translation.already_opening_crate.getString());
            return;
        }

        CrateType type = crate.getCrateType();

        if (type == null) {
            player.sendMessage(Translation.internal_error.getString());
            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());
            return;
        }

        if (type != CrateType.crate_on_the_go && type != CrateType.quick_crate && type != CrateType.fire_cracker) {
            sender.sendMessage(Translation.cant_be_a_virtual_crate.getString());
            return;
        }

        this.crateManager.openCrate(player, crate, KeyType.free_key, player.getLocation(), true, false);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%crate%", crate.getName());
        placeholders.put("%player%", player.getName());

        sender.sendMessage(Translation.opened_a_crate.getMessage(placeholders).toString());

        this.eventLogger.logKeyEvent(player, sender, crate, KeyType.free_key, EventLogger.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));
    }

    @SubCommand("set")
    @Permission(value = "crazycrates.command.admin.set", def = PermissionDefault.OP)
    public void onAdminCrateSet(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        Block block = player.getTargetBlock(null, 5);

        if (block.isEmpty()) {
            player.sendMessage(Translation.must_be_looking_at_block.getString());
            return;
        }

        this.crateManager.addCrateLocation(block.getLocation(), crate);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%crate%", crate.getName());
        placeholders.put("%prefix%", MsgUtils.getPrefix());

        player.sendMessage(Translation.created_physical_crate.getMessage(placeholders).toString());
    }

    @SubCommand("give-random")
    @Permission(value = "crazycrates.command.admin.giverandomkey", def = PermissionDefault.OP)
    public void onAdminCrateGiveRandom(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        Crate crate = this.plugin.getCrazyHandler().getCrateManager().getCrates().get((int) MiscUtils.pickNumber(0, (this.plugin.getCrazyHandler().getCrateManager().getCrates().size() - 2)));

        onAdminCrateGive(sender, keyType, crate.getName(), amount, target);
    }

    public record CustomPlayer(String name) {
        private static final CrazyCrates plugin = CrazyCrates.getPlugin(CrazyCrates.class);

        public @NotNull OfflinePlayer getOfflinePlayer() {
            CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> plugin.getServer().getOfflinePlayer(name)).thenApply(OfflinePlayer::getUniqueId);

            return plugin.getServer().getOfflinePlayer(future.join());
        }

        public Player getPlayer() {
            return plugin.getServer().getPlayer(name);
        }
    }

    @SubCommand("give")
    @Permission(value = "crazycrates.command.admin.givekey", def = PermissionDefault.OP)
    public void onAdminCrateGive(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        KeyType type = KeyType.getFromName(keyType);
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.free_key) {
            sender.sendMessage(MsgUtils.color(MsgUtils.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        if (target.getPlayer() != null) {
            Player player = target.getPlayer();

            addKey(sender, player, null, crate, type, amount);

            return;
        }

        OfflinePlayer offlinePlayer = target.getOfflinePlayer();

        addKey(sender, null, offlinePlayer, crate, type, amount);
    }

    private void addKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, Crate crate, KeyType type, int amount) {
        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        if (player != null) {
            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                player.getInventory().addItem(crate.getKey(amount));
            } else {
                this.plugin.getCrazyHandler().getUserManager().addKeys(amount, player.getUniqueId(), crate.getName(), type);
            }

            HashMap<String, String> placeholders = new HashMap<>();

            placeholders.put("%amount%", String.valueOf(amount));
            placeholders.put("%player%", player.getName());
            placeholders.put("%key%", crate.getKey().getItemMeta().getDisplayName());

            boolean fullMessage = this.config.getProperty(Config.notify_player_when_inventory_full);
            boolean inventoryCheck = this.config.getProperty(Config.give_virtual_keys_when_inventory_full);

            sender.sendMessage(Translation.gave_a_player_keys.getMessage(placeholders).toString());
            if (!inventoryCheck || !fullMessage && !MiscUtils.isInventoryFull(player) && player.isOnline()) player.sendMessage(Translation.obtaining_keys.getMessage(placeholders).toString());

            this.eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));

            return;
        }

        if (!this.plugin.getCrazyHandler().getUserManager().addOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type)) {
            sender.sendMessage(Translation.internal_error.getString());
        } else {
            HashMap<String, String> placeholders = new HashMap<>();

            placeholders.put("%amount%", String.valueOf(amount));
            placeholders.put("%player%", offlinePlayer.getName());

            sender.sendMessage(Translation.given_offline_player_keys.getMessage(placeholders).toString());

            this.eventLogger.logKeyEvent(offlinePlayer, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));
        }
    }

    @SubCommand("take")
    @Permission(value = "crazycrates.command.admin.takekey", def = PermissionDefault.OP)
    public void onAdminCrateTake(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        KeyType type = KeyType.getFromName(keyType);

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.free_key) {
            sender.sendMessage(MsgUtils.color(MsgUtils.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }
        
        if (target.getPlayer() != null) {
            Player player = target.getPlayer();
            
            takeKey(sender, player, null, crate, type, amount);
        }
        
        takeKey(sender, null, target.getOfflinePlayer(), crate, type, amount);
    }

    private void takeKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, Crate crate, KeyType type, int amount) {
        if (player != null) {
            this.plugin.getCrazyHandler().getUserManager().takeKeys(amount, player.getUniqueId(), crate.getName(), type, false);

            HashMap<String, String> placeholders = new HashMap<>();

            placeholders.put("%amount%", String.valueOf(amount));
            placeholders.put("%player%", player.getName());

            sender.sendMessage(Translation.take_player_keys.getMessage(placeholders).toString());

            this.eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));

            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%amount%", String.valueOf(amount));
        placeholders.put("%player%", offlinePlayer.getName());

        sender.sendMessage(Translation.take_offline_player_keys.getMessage(placeholders).toString());
        
        this.plugin.getCrazyHandler().getUserManager().takeOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type);
    }

    @SubCommand("giveall")
    @Permission(value = "crazycrates.command.admin.giveall", def = PermissionDefault.OP)
    public void onAdminCrateGiveAllKeys(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);

        if (type == null || type == KeyType.free_key) {
            sender.sendMessage(MsgUtils.color(MsgUtils.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Translation.not_a_crate.getMessage("%crate%", crateName).toString());
            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%amount%", String.valueOf(amount));
        placeholders.put("%key%", crate.getKey().getItemMeta().getDisplayName());

        sender.sendMessage(Translation.given_everyone_keys.getMessage(placeholders).toString());

        for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (MiscUtils.permCheck(onlinePlayer, Permissions.CRAZY_CRATES_PLAYER_EXCLUDE_GIVE_ALL, true)) continue;

            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(onlinePlayer, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
            onlinePlayer.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            onlinePlayer.sendMessage(Translation.obtaining_keys.getMessage(placeholders).toString());

            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                onlinePlayer.getInventory().addItem(crate.getKey(amount));
                return;
            }

            this.plugin.getCrazyHandler().getUserManager().addKeys(amount, onlinePlayer.getUniqueId(), crate.getName(), type);

            this.eventLogger.logKeyEvent(onlinePlayer, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, this.config.getProperty(Config.log_to_file), this.config.getProperty(Config.log_to_console));
        }
    }
}