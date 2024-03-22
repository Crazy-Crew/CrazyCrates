package com.badbones69.crazycrates.commands.subs;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import dev.triumphteam.cmd.core.annotation.ArgName;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.Description;
import dev.triumphteam.cmd.core.annotation.Optional;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;

import us.crazycrew.crazycrates.platform.config.ConfigManager;
import us.crazycrew.crazycrates.platform.config.impl.ConfigKeys;
import com.badbones69.crazycrates.CrazyCratesPaper;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.EventManager;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.FileManager.Files;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.events.PlayerReceiveKeyEvent;
import com.badbones69.crazycrates.api.enums.Permissions;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.BaseCommand;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.api.builders.types.CrateAdminMenu;
import com.badbones69.crazycrates.api.builders.types.CrateMainMenu;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.utils.FileUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Command(value = "crates", alias = {"crazycrates", "crazycrate", "crate", "cc"})
@Description("The base command for CrazyCrates")
public class CrateBaseCommand extends BaseCommand {

    @NotNull
    private final CrazyCratesPaper plugin = CrazyCratesPaper.get();

    @NotNull
    private final UserManager userManager = this.plugin.getUserManager();

    @NotNull
    private final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    @NotNull
    private final CrateManager crateManager = this.plugin.getCrateManager();

    @NotNull
    private final FileManager fileManager = this.plugin.getFileManager();

    @NotNull
    private final SettingsManager config = ConfigManager.getConfig();

    @NotNull
    private final FileConfiguration locations = Files.LOCATIONS.getFile();

    @Default
    @Permission(value = "crazycrates.command.player.menu", def = PermissionDefault.TRUE)
    public void onDefaultMenu(Player player) {
        if (this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            CrateMainMenu crateMainMenu = new CrateMainMenu(player, this.config.getProperty(ConfigKeys.inventory_size), this.config.getProperty(ConfigKeys.inventory_name));

            player.openInventory(crateMainMenu.build().getInventory());

            return;
        }

        onHelp(player);
    }

    @SubCommand("help")
    @Permission(value = "crazycrates.help", def = PermissionDefault.TRUE)
    public void onHelp(CommandSender sender) {
        if (sender instanceof Player player) {
            if (player.hasPermission("crazycrates.admin-access")) {
                player.sendMessage(Messages.admin_help.getMessage(player));

                return;
            }

            player.sendMessage(Messages.help.getMessage(player));

            return;
        }

        sender.sendMessage(Messages.admin_help.getMessage());
    }

    @SubCommand("transfer")
    @Permission(value = "crazycrates.command.player.transfer", def = PermissionDefault.OP)
    public void onPlayerTransferKeys(Player sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player, @Suggestion("numbers") int amount) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        // If the crate is menu or null. we return
        if (crate == null || crate.getCrateType() == CrateType.menu) {
            sender.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, sender));

            return;
        }

        // If it's the same player, we return.
        if (player.getUniqueId().toString().equalsIgnoreCase(sender.getUniqueId().toString())) {
            sender.sendMessage(Messages.same_player.getMessage(sender));

            return;
        }

        // If they don't have enough keys, we return.
        if (this.userManager.getVirtualKeys(sender.getUniqueId(), crate.getName()) <= amount) {
            sender.sendMessage(Messages.transfer_not_enough_keys.getMessage("{crate}", crate.getName(), sender));

            return;
        }

        PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(player, crate, PlayerReceiveKeyEvent.KeyReceiveReason.TRANSFER, amount);
        this.plugin.getServer().getPluginManager().callEvent(event);

        // If the event is cancelled, We return.
        if (event.isCancelled()) return;

        this.userManager.takeKeys(amount, sender.getUniqueId(), crate.getName(), KeyType.virtual_key, false);
        this.userManager.addKeys(amount, player.getUniqueId(), crate.getName(), KeyType.virtual_key);

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", KeyType.virtual_key.getFriendlyName());
        placeholders.put("{player}", player.getName());

        sender.sendMessage(Messages.transfer_sent_keys.getMessage(placeholders, sender));

        player.sendMessage(Messages.transfer_received_keys.getMessage("{player}", sender.getName(), player));

        EventManager.logKeyEvent(player, sender, crate, KeyType.virtual_key, EventManager.KeyEventType.KEY_EVENT_RECEIVED, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));
    }

    @SubCommand("reload")
    @Permission(value = "crazycrates.command.admin.reload", def = PermissionDefault.OP)
    public void onReload(CommandSender sender) {
        ConfigManager.reload();

        this.fileManager.reloadAllFiles();
        this.fileManager.setup();

        FileUtils.loadFiles();

        boolean isEnabled = this.config.getProperty(ConfigKeys.toggle_metrics);

        if (!isEnabled) {
            this.plugin.getMetrics().stop();
        } else {
            this.plugin.getMetrics().start();
        }

        FileUtils.cleanFiles();

        // Close previews
        if (this.config.getProperty(ConfigKeys.take_out_of_preview)) {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (inventoryManager.inCratePreview(player)) {
                    inventoryManager.closeCratePreview(player);

                    if (this.config.getProperty(ConfigKeys.send_preview_taken_out_message)) {
                        player.sendMessage(Messages.reloaded_forced_out_of_preview.getMessage(player));
                    }
                }
            });
        }

        this.crateManager.loadCrates();

        if (sender instanceof Player player) {
            player.sendMessage(Messages.reloaded_plugin.getMessage(player));

            return;
        }

        sender.sendMessage(Messages.reloaded_plugin.getMessage());
    }

    @SubCommand("debug")
    @Permission(value = "crazycrates.command.admin.debug", def = PermissionDefault.OP)
    public void onDebug(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, player));

            return;
        }

        crate.getPrizes().forEach(prize -> PrizeManager.givePrize(player, prize, crate));
    }

    @SubCommand("save")
    @Permission(value = "crazycrates.save", def = PermissionDefault.OP)
    public void onSchematicSave(Player player) {
        player.sendMessage(MsgUtils.color("&cThis feature is not yet developed internally by &eRyder Belserion."));
    }

    @SubCommand("admin")
    @Permission(value = "crazycrates.command.admin.access", def = PermissionDefault.OP)
    public void onAdminMenu(Player player) {
        CrateAdminMenu inventory = new CrateAdminMenu(player, 54, "&c&lAdmin Keys");

        player.openInventory(inventory.build().getInventory());
    }

    @SubCommand("list")
    @Permission(value = "crazycrates.command.admin.list", def = PermissionDefault.OP)
    public void onAdminList(CommandSender sender) {
        StringBuilder crates = new StringBuilder();
        String brokeCrates;

        this.crateManager.getUsableCrates().forEach(crate -> crates.append("&a").append(crate.getName()).append("&8, "));

        StringBuilder brokeCratesBuilder = new StringBuilder();

        this.crateManager.getBrokeCrates().forEach(crate -> brokeCratesBuilder.append("&c").append(crate).append(".yml&8,"));

        brokeCrates = brokeCratesBuilder.toString();

        sender.sendMessage(MsgUtils.color("&e&lCrates:&f " + crates));

        if (!brokeCrates.isEmpty()) sender.sendMessage(MsgUtils.color("&6&lBroken Crates:&f " + brokeCrates.substring(0, brokeCrates.length() - 2)));

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
                World world = this.plugin.getServer().getWorld(Objects.requireNonNull(this.locations.getString("Locations." + name + ".World")));

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
    public void onAdminCrateAddItem(Player player, @Suggestion("crates") String crateName, @Suggestion("prizes") String prize, @Suggestion("numbers") int chance, @Optional @Suggestion("tiers") String tier) {
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.getType() == Material.AIR) {
            player.sendMessage(Messages.no_item_in_hand.getMessage("{crate}", crateName, player));

            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, player));

            return;
        }

        try {
            if (tier == null) {
                crate.addEditorItem(prize, item, chance);
            } else {
                crate.addEditorItem(prize, item, crate.getTier(tier), chance);
            }
        } catch (Exception exception) {
            this.plugin.getServer().getLogger().log(Level.WARNING, "Failed to add a new prize to the " + crate.getName() + " crate.", exception);

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{prize}", prize);

        player.sendMessage(Messages.added_item_with_editor.getMessage(placeholders, player));
    }

    @SubCommand("preview")
    @Permission(value = "crazycrates.command.admin.preview", def = PermissionDefault.OP)
    public void onAdminCratePreview(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            if (sender instanceof Player person) {
                person.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, person));

                return;
            }

            sender.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName));

            return;
        }

        if (!crate.isPreviewEnabled()) {
            if (sender instanceof Player person) {
                person.sendMessage(Messages.preview_disabled.getMessage("{crate}", crate.getName(), player));

                return;
            }

            sender.sendMessage(Messages.preview_disabled.getMessage("{crate}", crate.getName()));

            return;
        }

        this.inventoryManager.addViewer(player);
        this.inventoryManager.openNewCratePreview(player, crate);
    }

    @SubCommand("open-others")
    @Permission(value = "crazycrates.command.admin.open.others", def = PermissionDefault.OP)
    public void onAdminCrateOpenOthers(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player, @Optional @Suggestion("key-types") KeyType keyType) {
        if (sender == player && keyType != KeyType.free_key) {
            onAdminCrateOpen(player, crateName);

            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (player == null) {
            if (sender instanceof Player person) {
                sender.sendMessage(Messages.not_online.getMessage(person));

                return;
            }

            sender.sendMessage(Messages.not_online.getMessage());

            return;
        }

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            if (sender instanceof Player person) {
                person.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, person));

                return;
            }

            sender.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName));

            return;
        }

        if (crate.getCrateType() == CrateType.crate_on_the_go || crate.getCrateType() == CrateType.quick_crate || crate.getCrateType() == CrateType.fire_cracker || crate.getCrateType() == CrateType.quad_crate) {
            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            if (sender instanceof Player person) {
                sender.sendMessage(Messages.cant_be_a_virtual_crate.getMessage(placeholders, person));

                return;
            }

            sender.sendMessage(Messages.cant_be_a_virtual_crate.getMessage(placeholders));

            return;
        }

        if (this.crateManager.isInOpeningList(player)) {
            if (sender instanceof Player person) {
                sender.sendMessage(Messages.already_opening_crate.getMessage("{crate}", crate.getName(), person));

                return;
            }

            sender.sendMessage(Messages.already_opening_crate.getMessage("{crate}", crate.getName()));

            return;
        }

        CrateType crateType = crate.getCrateType();

        if (crateType == null) {
            if (sender instanceof Player person) {
                sender.sendMessage(Messages.internal_error.getMessage(person));

                return;
            }

            sender.sendMessage(Messages.internal_error.getMessage());

            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());

            return;
        }

        boolean hasKey = false;
        KeyType type = keyType != null ? keyType : KeyType.physical_key;

        if (type == KeyType.free_key) {
            this.crateManager.openCrate(player, crate, type, player.getLocation(), true, false);

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{crate}", crate.getName());
            placeholders.put("{player}", player.getName());

            player.sendMessage(Messages.opened_a_crate.getMessage(placeholders, player));

            EventManager.logKeyEvent(player, player, crate, type, EventManager.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));

            return;
        }

        boolean hasVirtual = this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1;

        boolean hasPhysical = this.userManager.hasPhysicalKey(player.getUniqueId(), crate.getName(), false);

        if (hasVirtual) {
            hasKey = true;
        } else {
            if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys)) {
                if (hasPhysical) {
                    hasKey = true;
                    type = KeyType.physical_key;
                }
            }
        }

        if (!hasKey) {
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS, 1f, 1f);
            }

            if (sender instanceof Player person) {
                sender.sendMessage(Messages.no_virtual_key.getMessage("{crate}", crate.getName(), person));

                return;
            }

            sender.sendMessage(Messages.no_virtual_key.getMessage("{crate}", crate.getName()));

            return;
        }

        if (MiscUtils.isInventoryFull(player)) {
            if (sender instanceof Player person) {
                sender.sendMessage(Messages.inventory_not_empty.getMessage("{crate}", crate.getName(), person));

                return;
            }

            sender.sendMessage(Messages.inventory_not_empty.getMessage("{crate}", crate.getName()));

            return;
        }

        this.crateManager.openCrate(player, crate, type, player.getLocation(), true, false);

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{player}", player.getName());

        player.sendMessage(Messages.opened_a_crate.getMessage(placeholders, player));

        EventManager.logKeyEvent(player, player, crate, type, EventManager.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));
    }

    @SubCommand("open")
    @Permission(value = "crazycrates.command.admin.open", def = PermissionDefault.OP)
    public void onAdminCrateOpen(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, player));

            return;
        }

        if (crate.getCrateType() == CrateType.crate_on_the_go || crate.getCrateType() == CrateType.quick_crate || crate.getCrateType() == CrateType.fire_cracker || crate.getCrateType() == CrateType.quad_crate) {
            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            player.sendMessage(Messages.cant_be_a_virtual_crate.getMessage(placeholders, player));

            return;
        }

        if (this.crateManager.isInOpeningList(player)) {
            player.sendMessage(Messages.already_opening_crate.getMessage("{crate}", crate.getName(), player));

            return;
        }

        CrateType type = crate.getCrateType();

        if (type == null) {
            player.sendMessage(Messages.internal_error.getMessage(player));

            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());

            return;
        }

        boolean hasKey = false;
        KeyType keyType = KeyType.virtual_key;

        if (this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1) {
            hasKey = true;
        } else {
            if (this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys)) {
                if (this.userManager.hasPhysicalKey(player.getUniqueId(), crate.getName(), false)) {
                    hasKey = true;
                    keyType = KeyType.physical_key;
                }
            }
        }

        if (!hasKey) {
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                player.playSound(player.getLocation(), Sound.valueOf(this.config.getProperty(ConfigKeys.need_key_sound)), SoundCategory.PLAYERS,1f, 1f);
            }

            player.sendMessage(Messages.no_virtual_key.getMessage("{crate}", crate.getName(), player));

            return;
        }

        if (MiscUtils.isInventoryFull(player)) {
            player.sendMessage(Messages.inventory_not_empty.getMessage("{crate}", crate.getName(), player));

            return;
        }

        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

        EventManager.logKeyEvent(player, player, crate, keyType, EventManager.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));
    }

    @SubCommand("mass-open")
    @Permission(value = "crazycrates.command.admin.massopen", def = PermissionDefault.OP)
    public void onAdminCrateMassOpen(Player player, @Suggestion("crates") String crateName, @Suggestion("key-types") String keyType, @Suggestion("numbers") int amount) {
        KeyType type = KeyType.getFromName(keyType);

        if (type == null || type == KeyType.free_key) {
            player.sendMessage(MsgUtils.color(MsgUtils.getPrefix() + "&cPlease use Virtual/V or Physical/P for a Key type."));

            return;
        }

        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null || crate.getCrateType() == CrateType.menu) {
            player.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, player));

            return;
        }

        if (crate.getCrateType() == CrateType.crate_on_the_go || crate.getCrateType() == CrateType.quick_crate || crate.getCrateType() == CrateType.fire_cracker || crate.getCrateType() == CrateType.quad_crate) {
            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            player.sendMessage(Messages.cant_be_a_virtual_crate.getMessage(placeholders, player));

            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        int keys = type == KeyType.physical_key ? this.userManager.getPhysicalKeys(player.getUniqueId(), crate.getName()) : this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName());
        int keysUsed = 0;

        if (keys == 0) {
            player.sendMessage(Messages.no_virtual_key.getMessage("{crate}", crateName, player));

            return;
        }

        for (;keys > 0; keys--) {
            if (MiscUtils.isInventoryFull(player)) break;
            if (keysUsed >= amount) break;
            if (keysUsed >= crate.getMaxMassOpen()) break;

            Prize prize = crate.pickPrize(player);

            PrizeManager.givePrize(player, prize, crate);

            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

            if (prize.useFireworks()) MiscUtils.spawnFirework((player).getLocation().clone().add(.5, 1, .5), null);

            keysUsed++;
        }

        if (crate.getCrateType() != CrateType.cosmic) this.userManager.addOpenedCrate(player.getUniqueId(), keysUsed, crate.getName());

        if (!this.userManager.takeKeys(keysUsed, player.getUniqueId(), crate.getName(), type, false)) {
            this.crateManager.removeCrateInUse(player);
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        this.crateManager.removePlayerFromOpeningList(player);
    }

    @SubCommand("forceopen")
    @Permission(value = "crazycrates.command.admin.forceopen", def = PermissionDefault.OP)
    public void onAdminForceOpen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("online-players") Player player) {
        onAdminCrateOpenOthers(sender, crateName, player, KeyType.free_key);
    }

    @SubCommand("set")
    @Permission(value = "crazycrates.command.admin.set", def = PermissionDefault.OP)
    public void onAdminCrateSet(Player player, @Suggestion("crates") String crateName) {
        Crate crate = this.crateManager.getCrateFromName(crateName);

        if (crate == null) {
            player.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, player));

            return;
        }

        if (crate.getCrateType() == CrateType.menu && !this.config.getProperty(ConfigKeys.enable_crate_menu)) {
            player.sendMessage(Messages.cannot_set_type.getMessage(player));

            return;
        }

        Block block = player.getTargetBlock(null, 5);

        if (block.isEmpty()) {
            player.sendMessage(Messages.must_be_looking_at_block.getMessage(player));

            return;
        }

        this.crateManager.addCrateLocation(block.getLocation(), crate);

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{prefix}", MsgUtils.getPrefix());

        player.sendMessage(Messages.created_physical_crate.getMessage(placeholders, player));
    }

    @SubCommand("give-random")
    @Permission(value = "crazycrates.command.admin.giverandomkey", def = PermissionDefault.OP)
    public void onAdminCrateGiveRandom(CommandSender sender, @Suggestion("key-types") String keyType, @Suggestion("numbers") int amount, @Suggestion("online-players") CustomPlayer target) {
        Crate crate = this.crateManager.getUsableCrates().get((int) MiscUtils.pickNumber(0, (this.crateManager.getUsableCrates().size() - 2)));

        onAdminCrateGive(sender, keyType, crate.getName(), amount, target);
    }

    public record CustomPlayer(String name) {
        private static final CrazyCratesPaper plugin = CrazyCratesPaper.getPlugin(CrazyCratesPaper.class);

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
            if (sender instanceof Player human) {
                human.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, human));

                return;
            }

            sender.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName));

            return;
        }

        if (amount <= 0) {
            if (sender instanceof Player human) {
                human.sendMessage(Messages.not_a_number.getMessage("{number}", String.valueOf(amount), human));

                return;
            }

            sender.sendMessage(Messages.not_a_number.getMessage("{number}", String.valueOf(amount)));

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
                player.getInventory().addItem(crate.getKey(amount, player));
            } else {
                this.userManager.addKeys(amount, player.getUniqueId(), crate.getName(), type);
            }

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", String.valueOf(amount));
            placeholders.put("{player}", player.getName());
            placeholders.put("{keytype}", type.getFriendlyName());
            placeholders.put("{key}", crate.getKeyName());

            boolean fullMessage = this.config.getProperty(ConfigKeys.notify_player_when_inventory_full);
            boolean inventoryCheck = this.config.getProperty(ConfigKeys.give_virtual_keys_when_inventory_full);

            if (sender instanceof Player person) {
                person.sendMessage(Messages.gave_a_player_keys.getMessage(placeholders, person));
            } else {
                sender.sendMessage(Messages.gave_a_player_keys.getMessage(placeholders));
            }

            if (!inventoryCheck || !fullMessage && !MiscUtils.isInventoryFull(player) && player.isOnline()) player.sendMessage(Messages.obtaining_keys.getMessage(placeholders, player));

            EventManager.logKeyEvent(player, sender, crate, type, EventManager.KeyEventType.KEY_EVENT_GIVEN, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));

            return;
        }

        if (!this.userManager.addOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type)) {
            if (sender instanceof Player person) {
                person.sendMessage(Messages.internal_error.getMessage(person));
            } else {
                sender.sendMessage(Messages.internal_error.getMessage());
            }
        } else {
            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", String.valueOf(amount));
            placeholders.put("{keytype}", type.getFriendlyName());
            placeholders.put("{player}", offlinePlayer.getName());

            if (sender instanceof Player person) {
                person.sendMessage(Messages.given_offline_player_keys.getMessage(placeholders, person));
            } else {
                sender.sendMessage(Messages.given_offline_player_keys.getMessage(placeholders));
            }

            EventManager.logKeyEvent(offlinePlayer, sender, crate, type, EventManager.KeyEventType.KEY_EVENT_GIVEN, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));
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
            if (sender instanceof Player human) {
                human.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, human));

                return;
            }

            sender.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName));

            return;
        }

        if (amount <= 0) {
            if (sender instanceof Player human) {
                human.sendMessage(Messages.not_a_number.getMessage("{number}", String.valueOf(amount), human));

                return;
            }

            sender.sendMessage(Messages.not_a_number.getMessage("{number}", String.valueOf(amount)));

            return;
        }
        
        if (target.getPlayer() != null) {
            Player player = target.getPlayer();
            
            takeKey(sender, player, null, crate, type, amount);

            return;
        }
        
        takeKey(sender, null, target.getOfflinePlayer(), crate, type, amount);
    }

    /**
     * Take keys from a player whether offline or not.
     *
     * @param sender the sender of the command.
     * @param player the target of the command.
     * @param offlinePlayer the other target of the command.
     * @param crate the crate.
     * @param type the type of key.
     * @param amount the amount of keys.
     */
    private void takeKey(CommandSender sender, Player player, OfflinePlayer offlinePlayer, Crate crate, KeyType type, int amount) {
        if (player != null) {
            int totalKeys = this.userManager.getTotalKeys(player.getUniqueId(), crate.getName());

            if (totalKeys < 1) {
                if (MiscUtils.isLogging()) this.plugin.getLogger().warning("The player " + player.getName() + " does not have enough keys to take.");

                if (sender instanceof Player human) {
                    human.sendMessage(Messages.cannot_take_keys.getMessage("{player}", player.getName(), human));

                    return;
                }

                sender.sendMessage(Messages.cannot_take_keys.getMessage("{player}", player.getName()));

                return;
            }

            // If total keys is 30, Amount is 35.
            // It will check the key type and fetch the keys of the type, and it will set amount to the current virtual keys or physical keys.
            // If the check doesn't meet, It just uses amount as is.
            if (totalKeys < amount) {
                amount = type == KeyType.physical_key ? this.userManager.getPhysicalKeys(player.getUniqueId(), crate.getName()) : this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName());
            }

            this.userManager.takeKeys(amount, player.getUniqueId(), crate.getName(), type, false);

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{amount}", String.valueOf(amount));
            placeholders.put("{keytype}", type.getFriendlyName());
            placeholders.put("{player}", player.getName());

            if (sender instanceof Player human) {
                human.sendMessage(Messages.take_player_keys.getMessage(placeholders, human));
            } else {
                sender.sendMessage(Messages.take_player_keys.getMessage(placeholders));
            }

            EventManager.logKeyEvent(player, sender, crate, type, EventManager.KeyEventType.KEY_EVENT_REMOVED, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", type.getFriendlyName());
        placeholders.put("{player}", offlinePlayer.getName());

        if (sender instanceof Player human) {
            human.sendMessage(Messages.take_offline_player_keys.getMessage(placeholders, human));
        } else {
            sender.sendMessage(Messages.take_offline_player_keys.getMessage(placeholders));
        }

        this.userManager.takeOfflineKeys(offlinePlayer.getUniqueId(), crate.getName(), amount, type);
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
            if (sender instanceof Player human) {
                human.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName, human));

                return;
            }

            sender.sendMessage(Messages.not_a_crate.getMessage("{crate}", crateName));

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{amount}", String.valueOf(amount));
        placeholders.put("{keytype}", type.getFriendlyName());
        placeholders.put("{key}", crate.getKeyName());

        if (sender instanceof Player human) {
            human.sendMessage(Messages.given_everyone_keys.getMessage(placeholders, human));
        } else {
            sender.sendMessage(Messages.given_everyone_keys.getMessage(placeholders));
        }

        for (Player onlinePlayer : this.plugin.getServer().getOnlinePlayers()) {
            if (Permissions.CRAZYCRATES_PLAYER_EXCLUDE.hasPermission(onlinePlayer)) continue;

            PlayerReceiveKeyEvent event = new PlayerReceiveKeyEvent(onlinePlayer, crate, PlayerReceiveKeyEvent.KeyReceiveReason.GIVE_ALL_COMMAND, amount);
            onlinePlayer.getServer().getPluginManager().callEvent(event);

            if (event.isCancelled()) return;

            onlinePlayer.sendMessage(Messages.obtaining_keys.getMessage(placeholders, onlinePlayer));

            if (crate.getCrateType() == CrateType.crate_on_the_go) {
                onlinePlayer.getInventory().addItem(crate.getKey(amount, onlinePlayer));

                return;
            }

            this.userManager.addKeys(amount, onlinePlayer.getUniqueId(), crate.getName(), type);

            EventManager.logKeyEvent(onlinePlayer, sender, crate, type, EventManager.KeyEventType.KEY_EVENT_GIVEN, this.config.getProperty(ConfigKeys.log_to_file), this.config.getProperty(ConfigKeys.log_to_console));
        }
    }
}