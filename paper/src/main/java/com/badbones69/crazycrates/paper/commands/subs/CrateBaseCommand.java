package com.badbones69.crazycrates.paper.commands.subs;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.EventLogger;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.FileManager.Files;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.users.BukkitUserManager;
import com.ryderbelserion.cluster.bukkit.utils.LegacyLogger;
import com.ryderbelserion.cluster.bukkit.utils.LegacyUtils;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.api.enums.Permissions;
import us.crazycrew.crazycrates.common.config.ConfigManager;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.paper.api.plugin.CrazyHandler;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Command(value = "crates", alias = {"crazycrates", "cc", "crate", "crazycrate"})
public class CrateBaseCommand extends BaseCommand {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final @NotNull CrazyHandler crazyHandler = this.plugin.getCrazyHandler();
    private final @NotNull FileManager fileManager = this.crazyHandler.getFileManager();
    private final @NotNull BukkitUserManager userManager = this.crazyHandler.getUserManager();
    private final @NotNull CrazyManager crazyManager = this.crazyHandler.getCrazyManager();
    private final @NotNull EventLogger eventLogger = this.crazyHandler.getEventLogger();
    private final @NotNull Methods methods = this.crazyHandler.getMethods();

    private final @NotNull ConfigManager configManager = this.crazyHandler.getConfigManager();
    private final @NotNull SettingsManager config = this.configManager.getConfig();
    private final @NotNull SettingsManager menuConfig = this.configManager.getMainMenuConfig();

    @Default
    @Permission(value = "crazycrates.crate-menu", def = PermissionDefault.TRUE)
    public void onDefaultMenu(Player player) {
        //TODO() Update message enum.
        //if (this.menuConfig.getProperty(CrateMainMenu.crate_menu_toggle)) this.crazyHandler.getMenuManager().openMainMenu(player); else player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.help", def = PermissionDefault.TRUE)
    public void onHelp(CommandSender sender) {
        if (sender.hasPermission(new org.bukkit.permissions.Permission("crazycrates.admin-access", PermissionDefault.NOT_OP))) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.ADMIN_HELP.getMessage());
            return;
        }

        //TODO() Update message enum.
        //sender.sendMessage(Messages.HELP.getMessage());
    }

    @SubCommand("transfer")
    @Permission(value = "crazycrates.transfer", def = PermissionDefault.TRUE)
    public void onPlayerTransferKeys(Player sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player, @Suggestion("numbers") int amount) {
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (player.getName().equalsIgnoreCase(sender.getName())) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.SAME_PLAYER.getMessage());
            return;
        }

        if (this.userManager.getVirtualKeys(sender.getUniqueId(), crate.getName()) <= amount) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_ENOUGH_KEYS.getMessage("{crate}", crate.getName()));
            return;
        }

        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player.getUniqueId(), crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) {
            //TODO() Add logging when an event is called.
            return;
        }

        this.userManager.takeKeys(amount, sender.getUniqueId(), crate.getName(), KeyType.VIRTUAL_KEY, false);
        this.userManager.addVirtualKeys(amount, player.getUniqueId(), crate.getName());

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{player}", player.getName());

        //TODO() Update message enum.
        //sender.sendMessage(Messages.TRANSFERRED_KEYS.getMessage(placeholders));

        placeholders.put("{player}", sender.getName());

        //TODO() Update message enum.
        //player.sendMessage(Messages.RECEIVED_TRANSFERRED_KEYS.getMessage(placeholders));

        boolean logFile = this.config.getProperty(Config.log_to_file);
        boolean logConsole = this.config.getProperty(Config.log_to_console);

        this.eventLogger.logKeyEvent(player, sender, crate, KeyType.VIRTUAL_KEY, EventLogger.KeyEventType.key_received_event, logFile, logConsole);
    }

    @SubCommand("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    public void onCrateReload(CommandSender sender) {
        this.fileManager.reloadAllFiles();
        this.fileManager.setup();

        CrazyHandler.janitor();

        this.crazyManager.reload(false);

        //this.crazyHandler.getMenuManager().loadButtons();

        //TODO() Update message enum.
        //sender.sendMessage(Messages.RELOAD.getMessage());
    }

    @SubCommand("debug")
    @Permission(value = "crazycrates.debug", def = PermissionDefault.TRUE)
    public void onCrateDebug(CommandSender sender, @Suggestion("crates") String crateName) {
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        crate.getPrizes().forEach(prize -> this.crazyManager.givePrize((Player) sender, prize, crate));
    }

    @SubCommand("schem-save")
    @Permission(value = "crazycrates.schematic-save", def = PermissionDefault.TRUE)
    public void onSchematicSave(Player player) {
        //TODO() Update message enum.
        //player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("schem-set")
    @Permission(value = "crazycrates.schematic-set", def = PermissionDefault.TRUE)
    public void onSchematicSet(Player player) {
        //TODO() Update message enum.
        //player.sendMessage(Messages.FEATURE_DISABLED.getMessage());
    }

    @SubCommand("admin")
    @Permission(value = "crazycrates.admin-access", def = PermissionDefault.TRUE)
    public void onCrateMenu(Player player) {
        int size = this.crazyManager.getCrates().size();
        int slots = 9;

        for (; size > 9; size -= 9) slots += 9;

        Inventory inv = this.plugin.getServer().createInventory(null, slots, LegacyUtils.color(("&4&lAdmin Keys")));

        for (Crate crate : this.crazyManager.getCrates()) {
            if (crate.getCrateType() != CrateType.MENU) {
                if (inv.firstEmpty() >= 0) inv.setItem(inv.firstEmpty(), crate.getAdminKey());
            }
        }

        player.openInventory(inv);
    }

    @SubCommand("list")
    @Permission(value = "crazycrates.list", def = PermissionDefault.TRUE)
    public void onCrateList(CommandSender sender) {
        StringBuilder crates = new StringBuilder();
        String brokecrates;

        this.crazyManager.getCrates().forEach(crate -> crates.append("&a").append(crate.getName()).append("&8, "));

        StringBuilder brokeCratesBuilder = new StringBuilder();

        this.crazyManager.getBrokeCrates().forEach(crate -> brokeCratesBuilder.append("&c").append(crate).append(".yml&8,"));

        brokecrates = brokeCratesBuilder.toString();

        sender.sendMessage(LegacyUtils.color(("&e&lCrates:&f " + crates)));

        if (!brokecrates.isEmpty()) sender.sendMessage(LegacyUtils.color(("&6&lBroken Crates:&f " + brokecrates.substring(0, brokecrates.length() - 2))));

        sender.sendMessage(LegacyUtils.color(("&e&lAll Crate Locations:")));
        sender.sendMessage(LegacyUtils.color(("&c[ID]&8, &c[Crate]&8, &c[World]&8, &c[X]&8, &c[Y]&8, &c[Z]")));
        int line = 1;

        for (CrateLocation loc : this.crazyManager.getCrateLocations()) {
            Crate crate = loc.getCrate();
            String world = loc.getLocation().getWorld().getName();

            int x = loc.getLocation().getBlockX();
            int y = loc.getLocation().getBlockY();
            int z = loc.getLocation().getBlockZ();

            sender.sendMessage(LegacyUtils.color(("&8[&b" + line + "&8]: " + "&c" + loc.getID() + "&8, &c" + crate.getName() + "&8, &c" + world + "&8, &c" + x + "&8, &c" + y + "&8, &c" + z)));
            line++;
        }
    }

    @SubCommand("tp")
    @Permission(value = "crazycrates.teleport", def = PermissionDefault.TRUE)
    public void onPlayerTeleport(Player player, @Suggestion("locations") String id) {
        FileConfiguration locations = FileManager.Files.LOCATIONS.getFile();

        if (!locations.contains("Locations")) {
            locations.set("Locations.Clear", null);
            Files.LOCATIONS.saveFile();
        }

        for (String name : locations.getConfigurationSection("Locations").getKeys(false)) {
            if (name.equalsIgnoreCase(id)) {
                World world = this.plugin.getServer().getWorld(locations.getString("Locations." + name + ".World"));

                int x = locations.getInt("Locations." + name + ".X");
                int y = locations.getInt("Locations." + name + ".Y");
                int z = locations.getInt("Locations." + name + ".Z");

                Location location = new Location(world, x, y, z);

                player.teleport(location.add(.5, 0, .5));
                player.sendMessage(LegacyUtils.color((this.methods.getPrefix() + "&7You have been teleported to &6" + name + "&7.")));

                return;
            }
        }

        player.sendMessage(LegacyUtils.color((this.methods.getPrefix() + "&cThere is no location called &6" + id + "&c.")));
    }

    @SubCommand("additem")
    @Permission(value = "crazycrates.additem", def = PermissionDefault.TRUE)
    public void onCrateAddItem(Player player, @Suggestion("crates") String crateName, @Suggestion("prizes") String prize) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.NO_ITEM_IN_HAND.getMessage());
            return;
        }

        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.NOT_A_CRATE.getMessage("{prize}", crateName));
            return;
        }

        try {
            crate.addEditorItem(prize, item);
        } catch (Exception exception) {
            LegacyLogger.debug("Failed to add a new prize to the " + crate.getName() + " crate", exception);
        }

        this.crazyManager.loadCrates();

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{prize}", prize);

        //TODO() Update message enum.
        //player.sendMessage(Messages.ADDED_ITEM_WITH_EDITOR.getMessage(placeholders));
    }

    @SubCommand("preview")
    @Permission(value = "crazycrates.preview", def = PermissionDefault.TRUE)
    public void onCratePreview(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (!crate.isPreviewEnabled()) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.PREVIEW_DISABLED.getMessage());
            return;
        }

        this.crazyHandler.getMenuManager().setPlayerInMenu(player, false);
        this.crazyHandler.getMenuManager().openNewPreview(player, crate);
    }

    @SubCommand("open-others")
    @Permission(value = "crazycrates.open-others", def = PermissionDefault.TRUE)
    public void onCrateOpenOthers(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        openCrate(sender, player, crateName);
    }

    @SubCommand("open")
    @Permission(value = "crazycrates.open", def = PermissionDefault.TRUE)
    public void onCrateOpen(Player player, @Suggestion("crates") String crateName) {
        openCrate(player, player, crateName);
    }

    private void openCrate(CommandSender sender, Player player, String crateName) {
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (this.crazyManager.isInOpeningList(player)) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
            return;
        }

        CrateType type = crate.getCrateType();

        if (type == CrateType.CRATE_ON_THE_GO || type == CrateType.QUICK_CRATE || type == CrateType.FIRE_CRACKER || type == CrateType.QUAD_CRATE) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage("{crate}", crateName));
            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.VIRTUAL_KEY;

        if (this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
            hasKey = true;
        } else {
            if (this.config.getProperty(Config.virtual_accepts_physical_keys)) {
                if (this.userManager.hasPhysicalKey(player.getUniqueId(), crate.getName(), false)) {
                    hasKey = true;
                    keyType = KeyType.PHYSICAL_KEY;
                }
            }
        }

        if (!hasKey) {
            //TODO() make volume/pitch configurable
            //TODO() Adopt the new sound system including custom sounds.
            if (this.config.getProperty(Config.key_sound_toggle)) {
                Sound sound = Sound.valueOf(this.config.getProperty(Config.key_sound_name));

                player.playSound(player.getLocation(), sound, SoundCategory.PLAYERS, 1f, 1f);
            }

            //TODO() Update message enum.
            //player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
            return;
        }

        if (this.methods.isInventoryFull(player)) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.INVENTORY_FULL.getMessage());
            return;
        }

        this.crazyManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{player}", player.getName());

        //TODO() Update message enum.
        //sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));

        boolean logFile = this.config.getProperty(Config.log_to_file);
        boolean logConsole = this.config.getProperty(Config.log_to_console);

        this.eventLogger.logKeyEvent(player, sender, crate, keyType, EventLogger.KeyEventType.key_removed_event, logFile, logConsole);
    }

    @SubCommand("mass-open")
    @Permission(value = "crazycrates.mass-open", def = PermissionDefault.TRUE)
    public void onCrateMassOpen(Player player, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount) {
        UUID uuid = player.getUniqueId();

        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        this.crazyManager.addPlayerToOpeningList(player, crate);

        int keys = this.userManager.getVirtualKeys(uuid, crate.getName());
        int keysUsed = 0;

        if (keys == 0) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.NO_VIRTUAL_KEY.getMessage());
            return;
        }

        for (; keys > 0; keys--) {
            if (this.methods.isInventoryFull(player)) break;
            if (keysUsed > amount) break;
            if (keysUsed >= crate.getMaxMassOpen()) break;

            Prize prize = crate.pickPrize(player);
            this.crazyManager.givePrize(player, prize, crate);

            PlayerPrizeEvent event = new PlayerPrizeEvent(player.getUniqueId(), crate, crate.getName(), prize);

            this.plugin.getServer().getPluginManager().callEvent(event);

            if (prize.useFireworks()) this.methods.firework(player.getLocation().clone().add(.5, 1, .5));

            keysUsed++;
        }

        if (!this.userManager.takeKeys(keysUsed, player.getUniqueId(), crate.getName(), KeyType.VIRTUAL_KEY, false)) {
            this.methods.failedToTakeKey(player.getName(), crate);
            //TODO() Remove static
            //CrateControlListener.inUse.remove(player);
            this.crazyManager.removePlayerFromOpeningList(player);
            return;
        }

        this.crazyManager.removePlayerFromOpeningList(player);
    }

    @SubCommand("forceopen")
    @Permission(value = "crazycrates.force-open", def = PermissionDefault.TRUE)
    public void onCrateForceOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (this.crazyManager.isInOpeningList(player)) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
            return;
        }

        CrateType type = crate.getCrateType();

        if (type == CrateType.CRATE_ON_THE_GO || type == CrateType.QUICK_CRATE || type == CrateType.FIRE_CRACKER) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.CANT_BE_A_VIRTUAL_CRATE.getMessage());
            return;
        }

        this.crazyManager.openCrate(player, crate, KeyType.FREE_KEY, player.getLocation(), true, false);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{player}", player.getName());

        //TODO() Update message enum.
        //sender.sendMessage(Messages.OPENED_A_CRATE.getMessage(placeholders));

        boolean logFile = this.config.getProperty(Config.log_to_file);
        boolean logConsole = this.config.getProperty(Config.log_to_console);

        this.eventLogger.logKeyEvent(player, sender, crate, KeyType.FREE_KEY, EventLogger.KeyEventType.key_removed_event, logFile, logConsole);
    }

    @SubCommand("set")
    @Permission(value = "crazycrates.set-crate", def = PermissionDefault.TRUE)
    public void onCrateSet(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (this.crazyManager.isInOpeningList(player)) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.CRATE_ALREADY_OPENED.getMessage());
            return;
        }

        Block block = player.getTargetBlock(null, 5);

        if (block.isEmpty()) {
            //TODO() Update message enum.
            //player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
            return;
        }

        if (this.crazyManager.isCrateLocation(block.getLocation())) {
            //TODO() Add new message.
            return;
        }

        this.crazyManager.addCrateLocation(block.getLocation(), crate);

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());

        //TODO() Update message enum.
        //player.sendMessage(Messages.CREATED_PHYSICAL_CRATE.getMessage(placeholders));
    }

    @SubCommand("give-random")
    @Permission(value = "crazycrates.give-random-key", def = PermissionDefault.TRUE)
    public void onAdminCrateGiveRandom(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        Crate crate = this.crazyManager.getCrates().get((int) this.crazyManager.pickNumber(0, (this.crazyManager.getCrates().size() - 2)));

        onKeyGive(sender, keyType, crate.getName(), amount, target);
    }

    public record CustomPlayer(String name, CrazyCrates plugin) {
        public OfflinePlayer getOfflinePlayer() {
            CompletableFuture<UUID> future = CompletableFuture.supplyAsync(() -> this.plugin.getServer().getOfflinePlayer(this.name)).thenApply(OfflinePlayer::getUniqueId);

            return this.plugin.getServer().getOfflinePlayer(future.join());
        }

        public Player getPlayer() {
            return this.plugin.getServer().getPlayer(name);
        }
    }

    private void addKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, UUID uuid, Crate crate, KeyType type, int amount) {
        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(uuid, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_COMMAND, amount);

        boolean logFile = this.config.getProperty(Config.log_to_file);
        boolean logConsole = this.config.getProperty(Config.log_to_console);

        this.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled()) return;

        if (player != null) {
            if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                player.getInventory().addItem(crate.getKey(amount));
            } else {
                this.userManager.addKeys(amount, uuid, crate.getName(), type);
            }
        } else {
            if (!this.userManager.addOfflineKeys(uuid, crate.getName(), amount, type)) {
                //TODO() Update message enum.
                //sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
            } else {
                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("{amount}", String.valueOf(amount));
                placeholders.put("{player}", offlinePlayer.getName());

                //TODO() Update message enum.
                //sender.sendMessage(Messages.GIVEN_OFFLINE_PLAYER_KEYS.getMessage(placeholders));

                this.eventLogger.logKeyEvent(offlinePlayer, sender, crate, type, EventLogger.KeyEventType.key_removed_event, logFile, logConsole);

                return;
            }
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{player}", player.getName());
        placeholders.put("{key}", crate.getKey().getItemMeta().getDisplayName());

        boolean fullMessage = this.config.getProperty(Config.give_virtual_keys_message);
        boolean inventoryCheck = this.config.getProperty(Config.give_virtual_keys);

        //TODO() Update message enum.
        //sender.sendMessage(Messages.GIVEN_A_PLAYER_KEYS.getMessage(placeholders));
        //if (!inventoryCheck || !fullMessage && !methods.isInventoryFull(player) && player.isOnline()) sender.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

        this.eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.key_give_event, logFile, logConsole);
    }

    @SubCommand("give")
    @Permission(value = "crazycrates.give-key", def = PermissionDefault.TRUE)
    public void onKeyGive(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        KeyType type = KeyType.getFromName(keyType);
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(LegacyUtils.color((this.methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type.")));
            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (target.getPlayer() != null) {
            Player player = target.getPlayer();

            addKey(sender, player, null, player.getUniqueId(), crate, type, amount);

            return;
        }

        OfflinePlayer offlinePlayer = target.getOfflinePlayer();

        addKey(sender, null, offlinePlayer, offlinePlayer.getUniqueId(), crate, type, amount);
    }

    private void takeKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, UUID uuid, Crate crate, KeyType type, int amount) {
        boolean logFile = this.config.getProperty(Config.log_to_file);
        boolean logConsole = this.config.getProperty(Config.log_to_console);

        if (player != null) {
            this.userManager.takeKeys(amount, uuid, crate.getName(), type, false);
        } else {
            if (!this.userManager.takeKeys(amount, uuid, crate.getName(), type, false)) {
                //TODO() Update message enum.
                //sender.sendMessage(Messages.INTERNAL_ERROR.getMessage());
            } else {
                this.userManager.takeKeys(amount, uuid, crate.getName(), type, false);

                HashMap<String, String> placeholders = new HashMap<>();

                placeholders.put("{amount}", String.valueOf(amount));
                placeholders.put("{player}", offlinePlayer.getName());

                //TODO() Update message enum.
                //sender.sendMessage(Messages.TAKE_OFFLINE_PLAYER_KEYS.getMessage(placeholders));

                this.eventLogger.logKeyEvent(offlinePlayer, sender, crate, type, EventLogger.KeyEventType.key_removed_event, logFile, logConsole);

                return;
            }
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{player}", player.getName());

        //TODO() Update message enum.
        //sender.sendMessage(Messages.TAKE_A_PLAYER_KEYS.getMessage(placeholders));

        this.eventLogger.logKeyEvent(player, sender, crate, type, EventLogger.KeyEventType.key_removed_event, logFile, logConsole);
    }

    @SubCommand("take")
    @Permission(value = "crazycrates.take-key", def = PermissionDefault.TRUE)
    public void onKeyTake(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("crates") String crateName, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        KeyType type = KeyType.getFromName(keyType);
        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(LegacyUtils.color((this.methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type.")));
            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        if (target.getPlayer() != null) {
            Player player = target.getPlayer();

            takeKey(sender, player, null, player.getUniqueId(), crate, type, amount);

            return;
        }

        OfflinePlayer offlinePlayer = target.getOfflinePlayer();

        takeKey(sender, null, offlinePlayer, offlinePlayer.getUniqueId(), crate, type, amount);
    }

    @SubCommand("giveall")
    @Permission(value = "crazycrates.give-all", def = PermissionDefault.TRUE)
    public void onCrateGiveAllKeys(CommandSender sender, @Suggestion("key-types") @ArgName("key-type") String keyType, @Suggestion("crates") @ArgName("crate-name") String crateName, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);

        if (type == null || type == KeyType.FREE_KEY) {
            sender.sendMessage(LegacyUtils.color(this.methods.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));
            return;
        }

        Crate crate = this.crazyManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.MENU) {
            //TODO() Update message enum.
            //sender.sendMessage(Messages.NOT_A_CRATE.getMessage("{crate}", crateName));
            return;
        }

        HashMap<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", amount + "");
        placeholders.put("{key}", crate.getKey().getItemMeta().getDisplayName());

        //TODO() Update message enum.
        //sender.sendMessage(Messages.GIVEN_EVERYONE_KEYS.getMessage(placeholders));

        for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (this.methods.permCheck(onlinePlayer, Permissions.crazy_crates_player_exclude_give_all, true)) continue;

            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(onlinePlayer.getUniqueId(), crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
            onlinePlayer.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) {
                //TODO() Add logging.
                return;
            }

            //TODO() Update message enum.
            //onlinePlayer.sendMessage(Messages.OBTAINING_KEYS.getMessage(placeholders));

            if (crate.getCrateType() == CrateType.CRATE_ON_THE_GO) {
                onlinePlayer.getInventory().addItem(crate.getKey(amount));
                return;
            }

            this.userManager.addKeys(amount, onlinePlayer.getUniqueId(), crate.getName(), type);

            boolean logFile = this.config.getProperty(Config.log_to_file);
            boolean logConsole = this.config.getProperty(Config.log_to_console);

            this.eventLogger.logKeyEvent(onlinePlayer, sender, crate, type, EventLogger.KeyEventType.key_give_event, logFile, logConsole);
        }
    }
}