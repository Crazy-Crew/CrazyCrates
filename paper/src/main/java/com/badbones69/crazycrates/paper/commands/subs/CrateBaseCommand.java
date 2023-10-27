package com.badbones69.crazycrates.paper.commands.subs;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.api.enums.Permissions;
import com.badbones69.crazycrates.paper.listeners.CrateControlListener;
import com.badbones69.crazycrates.paper.listeners.MenuListener;
import com.badbones69.crazycrates.paper.listeners.PreviewListener;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import org.bukkit.*;
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
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
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
    private final CrazyManager crazyManager = this.plugin.getStarter().getCrazyManager();

    @NotNull
    private final FileManager fileManager = this.plugin.getFileManager();

    @NotNull
    private final EventLogger eventLogger = this.plugin.getStarter().getEventLogger();

    @NotNull
    private final FileConfiguration config = Files.CONFIG.getFile();

    @NotNull
    private final FileConfiguration locations = Files.LOCATIONS.getFile();

    @Default
    @Permission(value = "crazycrates.command.player.menu", def = PermissionDefault.TRUE)
    public void onDefaultMenu(Player player) {
        boolean openMenu = this.config.getBoolean("Settings.Enable-Crate-Menu");

        if (openMenu) MenuListener.openGUI(player);
        else player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.command.player.help", def = PermissionDefault.TRUE)
    public void onHelp(CommandSender sender) {
        sender.sendMessage(Messages.HELP.getMessage());
    }

    @SubCommand("transfer")
    @Permission(value = "crazycrates.command.player.transfer", def = PermissionDefault.OP)
    public void onPlayerTransferKeys(Player sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player, @Suggestion("numbers") int amount) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        // If the crate is menu or null. we return
        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%crate%", crateName));
            return;
        }

        // If it's the same player, we return.
        if (player.getName().equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            return;
        }

        // If they don't have enough keys, we return.
        if (this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(sender.getUniqueId(), crate.getName()) <= amount) {
            sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("%Crate%", crate.getName()));
            return;
        }

        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
        this.plugin.getServer().getPluginManager().callEvent(event);

        // If the event is cancelled, We return.
        if (event.isCancelled()) return;

        this.plugin.getCrazyHandler().getUserManager().takeKeys(amount, sender.getUniqueId(), crate.getName(), KeyType.virtual_key, false);
        this.plugin.getCrazyHandler().getUserManager().addKeys(amount, player.getUniqueId(), crate.getName(), KeyType.virtual_key);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Crate%", crate.getName());
        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", player.getName());

        sender.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));

        placeholders.put("%Player%", sender.getName());

        player.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));

        boolean logFile = this.config.getBoolean("Settings.Crate-Actions.Log-File");
        boolean logConsole = this.config.getBoolean("Settings.Crate-Actions.Log-Console");

        this.eventLogger.logKeyEvent(player, sender, crate, KeyType.virtual_key, EventLogger.KeyEventType.KEY_EVENT_RECEIVED, logFile, logConsole);
    }

    @SubCommand("admin-help")
    @Permission(value = "crazycrates.command.admin.help", def = PermissionDefault.OP)
    public void onAdminHelp(CommandSender sender) {
        sender.sendMessage(Messages.ADMIN_HELP.getMessage());
    }

    @SubCommand("reload")
    @Permission(value = "crazycrates.command.admin.reload", def = PermissionDefault.OP)
    public void onReload(CommandSender sender) {
        boolean isEnabled = this.plugin.getCrazyHandler().getConfigManager().getPluginConfig().getProperty(PluginConfig.toggle_metrics);

        if (!isEnabled) {
            this.plugin.getCrazyHandler().getMetrics().stop();
        } else {
            this.plugin.getCrazyHandler().getMetrics().start();
        }

        this.fileManager.reloadAllFiles();
        this.fileManager.setup();

        this.plugin.cleanFiles();
        this.crazyManager.loadCrates();

        sender.sendMessage(Messages.RELOAD.getMessage());
    }

    @SubCommand("debug")
    @Permission(value = "crazycrates.command.admin.debug", def = PermissionDefault.OP)
    public void onDebug(CommandSender sender, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        crate.getPrizes().forEach(prize -> this.crazyManager.givePrize((Player) sender, prize, crate));
    }

    @SubCommand("schem-save")
    @Permission(value = "crazycrates.command.admin.schematic.save", def = PermissionDefault.OP)
    public void onAdminSave(Player player) {
        player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("schem-set")
    @Permission(value = "crazycrates.command.admin.schematic.set", def = PermissionDefault.OP)
    public void onAdminSet(Player player) {
        player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("admin")
    @Permission(value = "crazycrates.command.admin.access", def = PermissionDefault.OP)
    public void onAdminMenu(Player player) {
        int size = this.crazyManager.getCrates().size();
        int slots = 9;

        for (; size > 9; size -= 9) slots += 9;

        Inventory inv = this.plugin.getServer().createInventory(null, slots, Methods.color("&4&lAdmin Keys"));

        for (Crate crate : this.crazyManager.getCrates()) {
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

        this.crazyManager.getCrates().forEach(crate -> crates.append("&a").append(crate.getName()).append("&8, "));

        StringBuilder brokecratesBuilder = new StringBuilder();

        this.crazyManager.getBrokeCrates().forEach(crate -> brokecratesBuilder.append("&c").append(crate).append(".yml&8,"));

        brokecrates = brokecratesBuilder.toString();

        sender.sendMessage(Methods.color("&e&lCrates:&f " + crates));

        if (!brokecrates.isEmpty())
            sender.sendMessage(Methods.color("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2)));

        sender.sendMessage(Methods.color("&e&lAll Crate Locations:"));
        sender.sendMessage(Methods.color("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]"));
        int line = 1;

        for (CrateLocation loc : this.crazyManager.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();

            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();

            sender.sendMessage(Methods.color("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z));
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
                player.sendMessage(Methods.color(Methods.getPrefix() + "&7You have been teleported to &6" + name + "&7."));

                return;
            }
        }

        player.sendMessage(Methods.color(Methods.getPrefix() + "&cThere is no location called &6" + id + "&c."));
    }

    @SubCommand("additem")
    @Permission(value = "crazycrates.command.admin.additem", def = PermissionDefault.OP)
    public void onAdminCrateAddItem(Player player, @Suggestion("crates") String crateName, @Suggestion("prizes") String prize) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        try {
            crate.addEditorItem(prize, item);
        } catch (Exception exception) {
            this.plugin.getServer().getLogger().log(Level.WARNING, "Failed to add a new prize to the " + crate.getName() + " crate.", exception);
        }

        // Reload the individual file instead of all of them.
        this.plugin.getServer().getLogger().warning("Crate: " + crate.getName());

        this.fileManager.getFile(crate.getName() + ".yml").reloadFile();

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Crate%", crate.getName());
        placeholders.put("%Prize%", prize);

        player.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
    }

    @SubCommand("preview")
    @Permission(value = "crazycrates.command.admin.preview", def = PermissionDefault.OP)
    public void onAdminCratePreview(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        if (!crate.isPreviewEnabled()) {
            sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
            return;
        }

        PreviewListener.setPlayerInMenu(player, false);
        PreviewListener.openNewPreview(player, crate);
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
            player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        if (this.crazyManager.isInOpeningList(player)) {
            sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
            return;
        }

        CrateType type = crate.getCrateType();

        if (type == null) {
            player.sendMessage(Messages.INTERNAL_ERROR.getMessage());
            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());
            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.virtual_key;

        if (this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
            hasKey = true;
        } else {
            if (this.config.getBoolean("Settings.Virtual-Accepts-Physical-Keys")) {
                if (this.crazyManager.hasPhysicalKey(player, crate, false)) {
                    hasKey = true;
                    keyType = KeyType.physical_key;
                }
            }
        }

        if (!hasKey) {
            if (this.config.contains("Settings.Need-Key-Sound")) {
                Sound sound = Sound.valueOf(this.config.getString("Settings.Need-Key-Sound"));

                player.playSound(player.getLocation(), sound, 1f, 1f);
            }

            player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
            return;
        }

        if (Methods.isInventoryFull(player)) {
            player.sendMessage(Messages.INVENTORY_FULL.getMessage());
            return;
        }

        if (type != CrateType.crate_on_the_go && type != CrateType.quick_crate && type != CrateType.fire_cracker && type != CrateType.quad_crate) {
            sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
            return;
        }

        this.crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Crate%", crate.getName());
        placeholders.put("%Player%", player.getName());

        sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));

        boolean logFile = this.config.getBoolean("Settings.Crate-Actions.Log-File");
        boolean logConsole = this.config.getBoolean("Settings.Crate-Actions.Log-Console");

        this.eventLogger.logKeyEvent(player, sender, crate, keyType, EventLogger.KeyEventType.KEY_EVENT_REMOVED, logFile, logConsole);
    }

    @SubCommand("mass-open")
    @Permission(value = "crazycrates.command.admin.massopen", def = PermissionDefault.OP)
    public void onAdminCrateMassOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount) {
        if (!(sender instanceof Player player)) return;

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        this.crazyManager.addPlayerToOpeningList(player, crate);

        int keys = this.plugin.getCrazyHandler().getUserManager().getVirtualKeys(player.getUniqueId(), crate.getName());
        int keysUsed = 0;

        if (keys == 0) {
            player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
            return;
        }

        for (; keys > 0; keys--) {
            if (Methods.isInventoryFull(player)) break;
            if (keysUsed > amount) break;
            if (keysUsed >= crate.getMaxMassOpen()) break;

            Prize prize = crate.pickPrize(player);
            this.crazyManager.givePrize(player, prize, crate);
            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

            if (prize.useFireworks()) Methods.firework(((Player) sender).getLocation().clone().add(.5, 1, .5));

            keysUsed++;
        }

        if (!this.plugin.getCrazyHandler().getUserManager().takeKeys(keysUsed, player.getUniqueId(), crate.getName(), KeyType.virtual_key, false)) {
            Methods.failedToTakeKey(player, crate);
            CrateControlListener.inUse.remove(player);
            this.crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        this.crazyManager.removePlayerFromOpeningList(player);
    }

    @SubCommand("forceopen")
    @Permission(value = "crazycrates.command.admin.forceopen", def = PermissionDefault.OP)
    public void onAdminForceOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        if (this.crazyManager.isInOpeningList(player)) {
            sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
            return;
        }

        CrateType type = crate.getCrateType();

        if (type == null) {
            player.sendMessage(Messages.INTERNAL_ERROR.getMessage());
            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());
            return;
        }

        if (type != CrateType.crate_on_the_go && type != CrateType.quick_crate && type != CrateType.fire_cracker) {
            sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
            return;
        }

        this.crazyManager.openCrate(player, crate, KeyType.free_key, player.getLocation(), true, false);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Crate%", crate.getName());
        placeholders.put("%Player%", player.getName());

        sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));

        boolean logFile = this.config.getBoolean("Settings.Crate-Actions.Log-File");
        boolean logConsole = this.config.getBoolean("Settings.Crate-Actions.Log-Console");

        this.eventLogger.logKeyEvent(player, sender, crate, KeyType.free_key, EventLogger.KeyEventType.KEY_EVENT_REMOVED, logFile, logConsole);
    }

    @SubCommand("set")
    @Permission(value = "crazycrates.command.admin.set", def = PermissionDefault.OP)
    public void onAdminCrateSet(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        Block block = player.getTargetBlock(null, 5);

        if (block.isEmpty()) {
            player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
            return;
        }

        this.crazyManager.addCrateLocation(block.getLocation(), crate);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Crate%", crate.getName());
        placeholders.put("%Prefix%", Methods.getPrefix());

        player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));
    }

    @SubCommand("give-random")
    @Permission(value = "crazycrates.command.admin.giverandomkey", def = PermissionDefault.OP)
    public void onAdminCrateGiveRandom(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        Crate crate = this.crazyManager.getCrates().get((int) this.crazyManager.pickNumber(0, (this.crazyManager.getCrates().size() - 2)));

        onAdminCrateGive(sender, keyType, crate.getName(), amount, target);
    }

    public record CustomPlayer(String name) {
        private static final CrazyCrates plugin = CrazyCrates.getPlugin();

        public @NotNull OfflinePlayer getOfflinePlayer() {
            CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> Bukkit.getServer().getOfflinePlayer(name)).thenApply(OfflinePlayer::getUniqueId);

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
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
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

        boolean logFile = this.config.getBoolean("Settings.Crate-Actions.Log-File");
        boolean logConsole = this.config.getBoolean("Settings.Crate-Actions.Log-Console");

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

            boolean fullMessage = this.config.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");
            boolean inventoryCheck = this.config.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");

            sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
            if (!inventoryCheck || !fullMessage && !Methods.isInventoryFull(player) && player.isOnline()) player.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

            this.eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, logFile, logConsole);

            return;
        }

        if (!this.plugin.getCrazyHandler().getUserManager().addOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type)) {
            sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
        } else {
            HashMap<String, String> placeholders = new HashMap<>();

            placeholders.put("%Amount%", String.valueOf(amount));
            placeholders.put("%Player%", offlinePlayer.getName());

            sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));

            this.eventLogger.logKeyEvent(offlinePlayer, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, logFile, logConsole);
        }
    }

    @SubCommand("take")
    @Permission(value = "crazycrates.command.admin.takekey", def = PermissionDefault.OP)
    public void onAdminCrateTake(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        KeyType type = KeyType.getFromName(keyType);

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.free_key) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }
        
        if (target.getPlayer() != null) {
            Player player = target.getPlayer();
            
            takeKey(sender, player, null, crate, type, amount);
        }
        
        takeKey(sender, null, target.getOfflinePlayer(), crate, type, amount);
    }

    private void takeKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, Crate crate, KeyType type, int amount) {
        boolean logFile = this.config.getBoolean("Settings.Crate-Actions.Log-File");
        boolean logConsole = this.config.getBoolean("Settings.Crate-Actions.Log-Console");

        if (player != null) {
            this.plugin.getCrazyHandler().getUserManager().takeKeys(amount, player.getUniqueId(), crate.getName(), type, false);

            HashMap<String, String> placeholders = new HashMap<>();

            placeholders.put("%Amount%", String.valueOf(amount));
            placeholders.put("%Player%", player.getName());

            sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

            this.eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_REMOVED, logFile, logConsole);

            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Player%", offlinePlayer.getName());
        
        sender.sendMessage(Messages.TAKE_OFFLINE_PLAYER_KEYS.getMessage(placeholders));
        
        this.plugin.getCrazyHandler().getUserManager().takeOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type);
    }

    @SubCommand("giveall")
    @Permission(value = "crazycrates.command.admin.giveall", def = PermissionDefault.OP)
    public void onAdminCrateGiveAllKeys(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);

        if (type == null || type == KeyType.free_key) {
            sender.sendMessage(Methods.color(Methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Messages.NOT_A_CRATE.getMessage("%Crate%", crateName));
            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("%Amount%", String.valueOf(amount));
        placeholders.put("%Key%", crate.getKey().getItemMeta().getDisplayName());

        sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));

        for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (Methods.permCheck(onlinePlayer, Permissions.CRAZY_CRATES_PLAYER_EXCLUDE_GIVE_ALL, true)) continue;

            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(onlinePlayer, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
            onlinePlayer.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            onlinePlayer.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                onlinePlayer.getInventory().addItem(crate.getKey(amount));
                return;
            }

            this.plugin.getCrazyHandler().getUserManager().addKeys(amount, onlinePlayer.getUniqueId(), crate.getName(), type);

            boolean logFile = this.config.getBoolean("Settings.Crate-Actions.Log-File");
            boolean logConsole = this.config.getBoolean("Settings.Crate-Actions.Log-Console");

            this.eventLogger.logKeyEvent(onlinePlayer, sender, crate, type, EventLogger.KeyEventType.KEY_EVENT_GIVEN, logFile, logConsole);
        }
    }
}