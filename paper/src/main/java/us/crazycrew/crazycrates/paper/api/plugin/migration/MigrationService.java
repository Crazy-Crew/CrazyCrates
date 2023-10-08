package us.crazycrew.crazycrates.paper.api.plugin.migration;

import ch.jalu.configme.SettingsManager;
import ch.jalu.configme.SettingsManagerBuilder;
import com.badbones69.crazycrates.paper.CrazyCrates;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.config.types.Config;
import us.crazycrew.crazycrates.common.config.types.Messages;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.common.config.types.menus.CrateMainMenu;
import us.crazycrew.crazycrates.common.config.types.menus.CratePreviewMenu;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MigrationService {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private SettingsManager pluginConfig;

    public void migrate() {
        // Migrate some options from the config.yml to plugin-config.yml
        copyPluginConfig();

        copyConfig();

        copyMessages();

        // Rename file if found.
        File file = new File(this.plugin.getDataFolder(), "data.yml");
        if (file.exists()) file.renameTo(new File(this.plugin.getDataFolder(), "users.yml"));
    }

    private void copyPluginConfig() {
        File input = new File(this.plugin.getDataFolder(),"config.yml");

        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(input)).join();

        // Check if this exists, and if not we return.
        if (file.getString("Settings.Prefix") == null) return;

        // Fetch the values I want to migrate
        String oldPrefix = file.getString("Settings.Prefix");
        boolean oldMetrics = file.getBoolean("Settings.Toggle-Metrics");

        // Create the plugin-config.yml file.
        File pluginConfigFile = new File(this.plugin.getDataFolder(), "plugin-config.yml");

        // Bind it to settings manager
        this.pluginConfig = SettingsManagerBuilder
                .withYamlFile(pluginConfigFile)
                .useDefaultMigrationService()
                .configurationData(PluginConfig.class)
                .create();

        this.pluginConfig.setProperty(PluginConfig.toggle_metrics, oldMetrics);

        if (oldPrefix != null) {
            this.pluginConfig.setProperty(PluginConfig.command_prefix, oldPrefix);
        }

        file.set("Settings.Prefix", null);
        file.set("Settings.Toggle-Metrics", null);

        try {
            this.pluginConfig.save();

            file.save(input);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void copyConfig() {
        File input = new File(this.plugin.getDataFolder(), "config.yml");

        // Rename config.yml to this.
        File backupFile = new File(this.plugin.getDataFolder(), "Config-Backup.yml");
        input.renameTo(backupFile);

        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(backupFile)).join();

        boolean enableCrateMenu = file.getBoolean("Settings.Enable-Crate-Menu");
        boolean crateActionsLogFile = file.getBoolean("Settings.Crate-Actions.Log-File");
        boolean crateActionsLogConsole = file.getBoolean("Settings.Crate-Actions.Log-Console");

        String inventoryName = file.getString("Settings.InventoryName");
        int inventorySize = file.getInt("Settings.InventorySize");

        boolean knockBack = file.getBoolean("Settings.KnockBack");

        boolean physicalAcceptsVirtual = file.getBoolean("Settings.Physical-Accepts-Virtual-Keys");
        boolean physicalAcceptsPhysical = file.getBoolean("Settings.Physical-Accepts-Physical-Keys");
        boolean virtualAcceptsPhysical = file.getBoolean("Settings.Virtual-Accepts-Physical-Keys");

        boolean giveVirtualKeysFullInvMessage = file.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full-Message");
        boolean giveVirtualKeysFullInv = file.getBoolean("Settings.Give-Virtual-Keys-When-Inventory-Full");

        String needKeySound = file.getString("Settings.Need-Key-Sound");

        int quadCrateTimer = file.getInt("Settings.QuadCrate.Timer");

        List<String> disabledWorlds = file.getStringList("Settings.DisabledWorlds");

        String previewButtonMenuItem = file.getString("Settings.Preview.Buttons.Menu.Item");
        String previewButtonMenuName = file.getString("Settings.Preview.Buttons.Menu.Name");
        List<String> previewButtonMenuLore = file.getStringList("Settings.Preview.Buttons.Menu.Lore");

        String previewButtonNextItem = file.getString("Settings.Preview.Buttons.Next.Item");
        String previewButtonNextName = file.getString("Settings.Preview.Buttons.Next.Name");
        List<String> previewButtonNextLore = file.getStringList("Settings.Preview.Buttons.Next.Lore");

        String previewButtonBackItem = file.getString("Settings.Preview.Buttons.Next.Item");
        String previewButtonBackName = file.getString("Settings.Preview.Buttons.Next.Name");
        List<String> previewButtonBackLore = file.getStringList("Settings.Preview.Buttons.Next.Lore");

        boolean fillerToggle = file.getBoolean("Settings.Filler.Toggle");
        String fillerItem = file.getString("Settings.Filler.Item");
        String fillerName = file.getString("Settings.Filler.Name");
        List<String> fillerLore = file.getStringList("Settings.Filler.Lore");

        List<String> customizer = file.getStringList("Settings.GUI-Customizer");

        File mainMenuFile = new File(this.plugin.getDataFolder(), "/menus/crate-menu.yml");

        // Bind it to settings manager
        SettingsManager config = SettingsManagerBuilder
                .withYamlFile(input)
                .useDefaultMigrationService()
                .configurationData(Config.class)
                .create();

        // Bind it to settings manager
        SettingsManager crateMainMenu = SettingsManagerBuilder
                .withYamlFile(mainMenuFile)
                .useDefaultMigrationService()
                .configurationData(CrateMainMenu.class)
                .create();

        File previewMenuFile = new File(this.plugin.getDataFolder(), "/menus/preview-menu.yml");

        // Bind it to settings manager
        SettingsManager cratePreviewMenu = SettingsManagerBuilder
                .withYamlFile(previewMenuFile)
                .useDefaultMigrationService()
                .configurationData(CratePreviewMenu.class)
                .create();

        config.setProperty(Config.log_to_file, crateActionsLogFile);
        config.setProperty(Config.log_to_console, crateActionsLogConsole);

        config.setProperty(Config.crate_knock_back, knockBack);

        config.setProperty(Config.physical_accepts_virtual, physicalAcceptsVirtual);
        config.setProperty(Config.physical_accepts_physical, physicalAcceptsPhysical);
        config.setProperty(Config.virtual_accepts_physical_keys, virtualAcceptsPhysical);

        config.setProperty(Config.give_virtual_keys, giveVirtualKeysFullInv);
        config.setProperty(Config.give_virtual_keys_message, giveVirtualKeysFullInvMessage);

        if (needKeySound != null) {
            config.setProperty(Config.key_sound_toggle, !needKeySound.isBlank());
            config.setProperty(Config.key_sound_name, needKeySound);
        }

        config.setProperty(Config.quad_crate_timer, quadCrateTimer);

        config.setProperty(Config.disabled_worlds_toggle, !disabledWorlds.isEmpty());
        config.setProperty(Config.disabled_worlds, disabledWorlds);

        config.setProperty(Config.customizer_toggle, !customizer.isEmpty());
        config.setProperty(Config.customizer_item_list, convert(customizer, false));

        if (inventoryName != null) crateMainMenu.setProperty(CrateMainMenu.crate_menu_title, convert(inventoryName));
        crateMainMenu.setProperty(CrateMainMenu.crate_menu_toggle, enableCrateMenu);
        crateMainMenu.setProperty(CrateMainMenu.crate_menu_size, inventorySize);

        crateMainMenu.setProperty(CrateMainMenu.crate_menu_filler_toggle, fillerToggle);
        if (fillerItem != null) crateMainMenu.setProperty(CrateMainMenu.crate_menu_filler_item, fillerItem);
        if (fillerName != null) crateMainMenu.setProperty(CrateMainMenu.crate_menu_filler_name, convert(fillerName));
        crateMainMenu.setProperty(CrateMainMenu.crate_menu_filler_lore, convert(fillerLore, false));

        if (previewButtonMenuItem != null) cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_menu_button_material, previewButtonMenuItem);
        if (previewButtonMenuName!= null) cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_menu_button_name, convert(previewButtonMenuName));

        cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_menu_button_lore, convert(previewButtonMenuLore, false));

        if (previewButtonNextItem != null) cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_next_button_material, previewButtonNextItem);
        if (previewButtonNextName != null) cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_next_button_name, convert(previewButtonNextName));

        cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_next_button_lore, convert(previewButtonNextLore, false));

        if (previewButtonBackItem != null) cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_back_button_material, previewButtonBackItem);
        if (previewButtonBackName != null) cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_back_button_name, convert(previewButtonBackName));

        cratePreviewMenu.setProperty(CratePreviewMenu.crate_preview_back_button_lore, convert(previewButtonBackLore, false));

        // Save new config.
        config.save();
        crateMainMenu.save();
        cratePreviewMenu.save();
    }

    private void copyMessages() {
        File input = new File(this.plugin.getDataFolder(), "Messages.yml");

        // If the input does not exist, We don't need to do anything else.
        if (!input.exists()) return;

        // Rename config.yml to this.
        File backupFile = new File(this.plugin.getDataFolder(), "Messages-Backup.yml");
        input.renameTo(backupFile);

        // Load configuration of input.
        YamlConfiguration file = CompletableFuture.supplyAsync(() -> YamlConfiguration.loadConfiguration(backupFile)).join();

        String noTeleporting = convert("{prefix}" + file.getString("Messages.No-Teleporting"));
        String noCommandsWhileInCrate = convert("{prefix}" + file.getString("Messages.No-Commands-While-In-Crate"));
        String featureDisabled = convert("{prefix}" + file.getString("Messages.Feature-Disabled"));
        String noKey = convert("{prefix}" + file.getString("Messages.No-Key"));
        String noVirtualKey = convert("{prefix}" + file.getString("Messages.No-Virtual-Key"));
        String notOnBlock = convert("{prefix}" + file.getString("Messages.Not-On-Block"));
        String quickCrateInUse = convert("{prefix}" + file.getString("Messages.Quick-Crate-In-Use"));
        String worldDisabled = convert("{prefix}" + file.getString("Messages.World-Disabled"));
        String reload = convert("{prefix}" + file.getString("Messages.Reload"));
        String notOnline = convert("{prefix}" + file.getString("Messages.Not-Online"));
        String noPermission = convert("{prefix}" + file.getString("Messages.No-Permission"));
        String noCratePermission = convert("{prefix}" + file.getString("Messages.No-Crate-Permission"));
        String crateAlreadyOpened = convert("{prefix}" + file.getString("Messages.Crate-Already-Opened"));
        String cantBeAVirtualCrate = convert("{prefix}" + file.getString("Messages.Cant-Be-A-Virtual-Crate"));
        String inventoryFull = convert("{prefix}" + file.getString("Messages.Inventory-Full"));
        String toCloseToAnotherPlayer = convert("{prefix}" + file.getString("Messages.To-Close-To-Another-Player"));
        String needsMoreRoom = convert("{prefix}" + file.getString("Messages.Needs-More-Room"));
        String outOfTime = convert("{prefix}" + file.getString("Messages.Out-Of-Time"));
        String mustBeAPlayer = convert("{prefix}" + file.getString("Messages.Must-Be-A-Player"));
        String mustBeAConsoleSender = convert("{prefix}" + file.getString("Messages.Must-Be-A-Console-Sender"));
        String mustBeLookingAtABlock = convert("{prefix}" + file.getString("Messages.Must-Be-Looking-At-A-Block"));
        String notACrate = convert("{prefix}" + file.getString("Messages.Not-A-Crate"));
        String notANumber = convert("{prefix}" + file.getString("Messages.Not-A-Number"));
        String givenAPlayerKeys = convert("{prefix}" + file.getString("Messages.Given-A-Player-Keys"));
        String cannotGivePlayerKeys = convert("{prefix}" + file.getString("Messages.Cannot-Give-Player-Keys"));
        String obtainingKeys = convert("{prefix}" + file.getString("Messages.Obtaining-Keys"));
        String givenEveryoneKeys = convert("{prefix}" + file.getString("Messages.Given-Everyone-Keys"));
        String givenOfflinePlayerKeys = convert("{prefix}" + file.getString("Messages.Given-Offline-Player-Keys"));
        String takeAPlayerKeys = convert("{prefix}" + file.getString("Messages.Take-A-Player-Keys"));
        String takeOfflinePlayerKeys = convert("{prefix}" + file.getString("Messages.Take-Offline-Player-Keys"));
        String openedACrate = convert("{prefix}" + file.getString("Messages.Opened-A-Crate"));
        String internalError = convert("{prefix}" + file.getString("Messages.Internal-Error"));
        String unknownCommand = convert("{prefix}" + file.getString("Messages.Unknown-Command"));
        String correctUsage = convert("{prefix}" + file.getString("Messages.Correct-Usage"));
        String noItemInHand = convert("{prefix}" + file.getString("Messages.No-Item-In-Hand"));
        String addedItemWithEditor = convert("{prefix}" + file.getString("Messages.Added-Item-With-Editor"));
        String previewDisabled = convert("{prefix}" + file.getString("Messages.Preview-Disabled"));
        String noSchematicsFound = convert("{prefix}" + file.getString("Messages.No-Schematics-Found"));
        String noPrizesFound = convert("{prefix}" + file.getString("Messages.No-Prizes-Found"));
        String samePlayer = convert("{prefix}" + file.getString("Messages.Same-Player"));
        String prizeError = convert("{prefix}" + file.getString("Messages.Prize-Error"));
        String requiredKeys = convert("{prefix}" + file.getString("Messages.Required-Keys"));

        String notEnoughKeys = convert("{prefix}" + file.getString("Messages.Transfer-Keys.Not-Enough-Keys"));
        String transferredKeys = convert("{prefix}" + file.getString("Messages.Transfer-Keys.Transferred-Keys"));
        String receivedTransferredKeys = convert("{prefix}" + file.getString("Messages.Transfer-Keys.Received-Transferred-Keys"));

        List<String> createdPhysicalCrate = convert(file.getStringList("Messages.Created-Physical-Crate"), true);

        String removedPhysicalCrate = convert("{prefix}" + file.getString("Messages.Removed-Physical-Crate"));

        String keysPersonalNoVirtualKeys = convert("{prefix}" + file.getString("Messages.Keys.Personal.No-Virtual-Keys"));
        List<String> keysPersonalNoVirtualKeysHeader = file.getStringList("Messages.Keys.Personal.No-Virtual-Keys.Header");

        String keysOtherNoVirtualKeys = convert("{prefix}" + file.getString("Messages.Keys.Other-Player.No-Virtual-Keys"));
        List<String> keysOtherNoVirtualKeysHeader = file.getStringList("Messages.Keys.Other-Player.No-Virtual-Keys.Header");

        //noinspection DataFlowIssue
        String perCrate = convert(file.getString("Messages.Keys.Per-Crate"));

        List<String> help = file.getStringList("Messages.Help");

        List<String> adminHelp = convert(file.getStringList("Messages.Admin-Help"), false);

        // Check if directory exists and create it if not.
        File localeDir = new File(this.plugin.getDataFolder(), "locale");
        if (!localeDir.exists()) localeDir.mkdirs();

        // Create messages file.
        File messagesFile = new File(localeDir, this.pluginConfig.getProperty(PluginConfig.locale_file) + ".yml");

        SettingsManager messages = SettingsManagerBuilder
                .withYamlFile(messagesFile)
                .useDefaultMigrationService()
                .configurationData(Messages.class)
                .create();

        messages.setProperty(Messages.no_teleporting, noTeleporting);
        messages.setProperty(Messages.no_commands, noCommandsWhileInCrate);
        messages.setProperty(Messages.feature_disabled, featureDisabled);
        messages.setProperty(Messages.no_keys, noKey);
        messages.setProperty(Messages.no_virtual_keys, noVirtualKey);
        messages.setProperty(Messages.crate_requirements_not_on_block, notOnBlock);
        messages.setProperty(Messages.crate_requirements_in_use, quickCrateInUse);
        messages.setProperty(Messages.crate_requirements_world_disabled,worldDisabled);
        messages.setProperty(Messages.command_reload_completed, reload);
        messages.setProperty(Messages.player_requirements_target_not_online, notOnline);
        messages.setProperty(Messages.player_requirements_no_permission, noPermission);
        messages.setProperty(Messages.crate_requirements_no_permission, noCratePermission);
        messages.setProperty(Messages.crate_requirements_cannot_be_a_virtual_crate, cantBeAVirtualCrate);
        messages.setProperty(Messages.player_requirements_inventory_not_empty, inventoryFull);
        messages.setProperty(Messages.player_requirements_too_close_to_another_player, toCloseToAnotherPlayer);
        messages.setProperty(Messages.crate_requirements_need_more_room, needsMoreRoom);
        messages.setProperty(Messages.crate_requirements_out_of_time, outOfTime);
        messages.setProperty(Messages.player_requirements_must_be_player, mustBeAPlayer);
        messages.setProperty(Messages.player_requirements_must_be_console_sender, mustBeAConsoleSender);
        messages.setProperty(Messages.player_requirements_must_be_looking_at_block, mustBeLookingAtABlock);
        messages.setProperty(Messages.crate_requirements_not_a_crate, notACrate);
        messages.setProperty(Messages.crate_requirements_not_a_number, notANumber);
        messages.setProperty(Messages.command_give_player_keys, givenAPlayerKeys);
        messages.setProperty(Messages.command_give_cannot_give_player_keys, cannotGivePlayerKeys);
        messages.setProperty(Messages.player_requirements_obtaining_keys, obtainingKeys);
        messages.setProperty(Messages.command_give_everyone_keys, givenEveryoneKeys);
        messages.setProperty(Messages.command_give_offline_player_keys, givenOfflinePlayerKeys);
        messages.setProperty(Messages.command_take_player_keys, takeAPlayerKeys);
        messages.setProperty(Messages.command_take_offline_player_keys, takeOfflinePlayerKeys);
        messages.setProperty(Messages.command_open_crate, openedACrate);
        messages.setProperty(Messages.internal_error, internalError);
        messages.setProperty(Messages.unknown_command, unknownCommand);
        messages.setProperty(Messages.correct_usage, correctUsage);
        messages.setProperty(Messages.command_add_item_no_item_in_hand, noItemInHand);
        messages.setProperty(Messages.command_add_item_from_hand, addedItemWithEditor);
        messages.setProperty(Messages.crate_requirements_preview_disabled, previewDisabled);
        messages.setProperty(Messages.no_schematics_found, noSchematicsFound);
        messages.setProperty(Messages.no_prizes_found, noPrizesFound);
        messages.setProperty(Messages.player_requirements_same_player, samePlayer);
        messages.setProperty(Messages.prize_error, List.of(prizeError));
        messages.setProperty(Messages.player_requirements_required_keys, requiredKeys);
        messages.setProperty(Messages.command_transfer_not_enough_keys, notEnoughKeys);
        messages.setProperty(Messages.command_transfer_keys_received, receivedTransferredKeys);
        messages.setProperty(Messages.command_transfer_keys, transferredKeys);
        messages.setProperty(Messages.crates_physical_crate_created, createdPhysicalCrate);

        messages.setProperty(Messages.crates_physical_crate_removed, removedPhysicalCrate);

        messages.setProperty(Messages.crate_requirements_already_open, crateAlreadyOpened);

        messages.setProperty(Messages.command_keys_personal_no_virtual_keys, keysPersonalNoVirtualKeys);
        messages.setProperty(Messages.command_keys_personal_virtual_keys_header, keysPersonalNoVirtualKeysHeader);

        messages.setProperty(Messages.command_keys_other_player_no_virtual_keys, keysOtherNoVirtualKeys);
        messages.setProperty(Messages.command_keys_other_player_virtual_keys_header, keysOtherNoVirtualKeysHeader);

        messages.setProperty(Messages.command_keys_crate_format, perCrate);

        messages.setProperty(Messages.player_help, help);
        messages.setProperty(Messages.admin_help, adminHelp);

        // Save the file.
        messages.save();
    }

    private List<String> convert(List<String> list, boolean hasPrefix) {
        List<String> newList = new ArrayList<>();

        list.forEach(line -> {
            if (hasPrefix) {
                newList.add(convert("{prefix}" + line));
                return;
            }
            
            newList.add(convert(line));
        });

        return newList;
    }

    private String convert(String message) {
        return message
                .replaceAll("%page%", "{page}")
                .replaceAll("%key%", "{key}")
                .replaceAll("%world%", "{world}")
                .replaceAll("%player%", "{player}")
                .replaceAll("%crate%", "{crate}")
                .replaceAll("%number%", "{number}")
                .replaceAll("%amount%", "{amount}")
                .replaceAll("%usage%", "{usage}")
                .replaceAll("%cratetype%", "{cratetype}")
                .replaceAll("%slot%", "{slot}")
                .replaceAll("%reward%", "{reward}")
                .replaceAll("%random%", "{random}")
                .replaceAll("%prize%", "{prize}")
                .replaceAll("%key-amount%", "{key-amount}")
                .replaceAll("%prefix%", "")
                .replaceAll("%Prefix%", "")
                .replaceAll("%id%", "{id}")
                .replaceAll("%keys%", "{keys}")
                .replaceAll("Slot:", "slot:")
                .replaceAll("Item:", "item:")
                .replaceAll("Name:", "name:")
                .replaceAll("https://github.com/Crazy-Crew/CrazyCrates/wiki/Commands-and-Permissions", "https://docs.crazycrew.us/crazycrates/info/commands/permissions");
    }
}