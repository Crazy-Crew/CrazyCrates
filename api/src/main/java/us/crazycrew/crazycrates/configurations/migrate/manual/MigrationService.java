package us.crazycrew.crazycrates.configurations.migrate.manual;

import org.simpleyaml.configuration.file.YamlConfiguration;
import org.simpleyaml.configuration.implementation.api.QuoteStyle;
import us.crazycrew.crazycore.CrazyCore;
import us.crazycrew.crazycore.CrazyLogger;
import us.crazycrew.crazycore.utils.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Description: Migrate old values to new values manually.
 */
public class MigrationService {

    private final File path = CrazyCore.api().getDirectory().toFile();

    private final String prefix = "Settings.";

    public void convert() {
        // We copy this first.
        copyPluginSettings();

        // We copy this next.
        copyConfigSettings();
        
        // We copy this last.
        copyMessages();
    }

    private void copyPluginSettings() {
        // The new plugin-settings.yml
        File output = new File(path + "/plugin-settings.yml");

        File input = new File(path + "/config.yml");

        // The old configuration of config.yml.
        YamlConfiguration config = null;

        if (!input.exists()) return;

        try {
            config = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert config != null;
        if (config.getString(prefix + "Prefix") == null) return;

        YamlConfiguration pluginSettings = null;

        try {
            //noinspection ResultOfMethodCallIgnored
            output.createNewFile(); // Create the file if it doesn't exist.
            pluginSettings = YamlConfiguration.loadConfiguration(output); // Load the output.
        } catch (IOException e) {
            e.printStackTrace();
        }

        String oldPrefix = config.getString(prefix + "Prefix");
        boolean oldMetrics = config.getBoolean(prefix + "Toggle-Metrics");
        boolean oldUpdate = config.getBoolean(prefix + "Update-Checker");

        if (pluginSettings == null) return;

        pluginSettings.set(prefix.toLowerCase() + "prefix.command", oldPrefix);
        pluginSettings.set(prefix.toLowerCase() + "toggle-metrics", oldMetrics);
        pluginSettings.set(prefix.toLowerCase() + "update-checker", oldUpdate);

        config.set(prefix + "Prefix", null);
        config.set(prefix + "Toggle-Metrics", null);
        config.set(prefix + "Update-Checker", null);
        config.set(prefix + "Config-Version", null);

        try {
            config.save(input);
            pluginSettings.save(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyConfigSettings() {
        File output = new File(path + "/config-v1.yml");

        File input = new File(path + "/config.yml");

        // The old configuration of config.yml.
        YamlConfiguration config;
        YamlConfiguration configV1 = null;

        try {
            config = YamlConfiguration.loadConfiguration(input);

            if (config.getString(prefix + "Enable-Crate-Menu") == null && !output.exists()) return;

            input.renameTo(output);

            if (!input.exists()) input.createNewFile();

            configV1 = YamlConfiguration.loadConfiguration(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (configV1 == null) return;

        boolean enableCrateMenu = configV1.getBoolean(prefix + "Enable-Crate-Menu");
        boolean crateLogFile = configV1.getBoolean(prefix + "Crate-Actions.Log-File");
        boolean crateLogConsole = configV1.getBoolean(prefix + "Crate-Actions.Log-Console");

        String invName = configV1.getString(prefix + "InventoryName");
        int invSize = configV1.getInt(prefix + "InventorySize");

        boolean knockBack = configV1.getBoolean(prefix + "KnockBack");

        boolean physAcceptsVirtual = configV1.getBoolean(prefix + "Physical-Accepts-Virtual-Keys");
        boolean physAcceptsPhys = configV1.getBoolean(prefix + "Physical-Accepts-Physical-Keys");
        boolean virtualAcceptsPhys = configV1.getBoolean(prefix + "Virtual-Accepts-Physical-Keys");
        boolean giveVirtualKeysInventoryMessage = configV1.getBoolean(prefix + "Give-Virtual-Keys-When-Inventory-Full-Message");
        boolean giveVirtualKeysInventory = configV1.getBoolean(prefix + "Give-Virtual-Keys-When-Inventory-Full");

        // TODO() Move this to per crate.
        String needKeySound = configV1.getString(prefix + "Need-Key-Sound");

        // TODO() Move this to QuadCrate type.
        int quadCrateTimer = configV1.getInt(prefix + "QuadCrate.Timer");

        List<String> disabledWorlds = configV1.getStringList(prefix + "DisabledWorlds");

        // TODO() Move this to its own configuration file.
        String menuName = configV1.getString(prefix + "Preview.Buttons.Menu.Name");
        String menuItem = configV1.getString(prefix + "Preview.Buttons.Menu.Item");
        List<String> menuLore = configV1.getStringList(prefix + "Preview.Buttons.Menu.Lore");

        String nextName = configV1.getString(prefix + "Preview.Buttons.Next.Name");
        String nextItem = configV1.getString(prefix + "Preview.Buttons.Next.Item");
        List<String> nextLore = configV1.getStringList(prefix + "Preview.Buttons.Next.Lore");

        String backName = configV1.getString(prefix + "Preview.Buttons.Back.Name");
        String backItem = configV1.getString(prefix + "Preview.Buttons.Back.Item");
        List<String> backLore = configV1.getStringList(prefix + "Preview.Buttons.Back.Lore");

        boolean fillerToggle = configV1.getBoolean(prefix + "Filler.Toggle");
        String fillerItem = configV1.getString(prefix + "Filler.Item");
        String fillerName = configV1.getString(prefix + "Filler.Name");
        List<String> fillerLore = configV1.getStringList(prefix + "Filler.Lore");

        List<String> guiCustomizer = configV1.getStringList(prefix + "GUI-Customizer");

        YamlConfiguration other = null;

        try {
            other = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            e.printStackTrace();
        }

        assert other != null;

        other.options().charset(StandardCharsets.UTF_8);
        configV1.options().charset(StandardCharsets.UTF_8);

        other.set("crate-settings.crate-actions.log-to-file", crateLogFile);
        other.set("crate-settings.crate-actions.log-to-console", crateLogConsole);

        other.set("crate-settings.preview-menu.toggle", enableCrateMenu);
        other.set("crate-settings.preview-menu.name", invName);
        other.set("crate-settings.preview-menu.size", invSize);

        other.set("crate-settings.knock-back", knockBack);
        other.set("crate-settings.keys.physical-accepts-virtual-keys", physAcceptsVirtual);
        other.set("crate-settings.keys.physical-accepts-physical-keys", physAcceptsPhys);
        other.set("crate-settings.keys.virtual-accepts-physical-keys", virtualAcceptsPhys);

        other.set("crate-settings.keys.inventory-not-empty.give-virtual-keys-message", giveVirtualKeysInventoryMessage);
        other.set("crate-settings.keys.inventory-not-empty.give-virtual-keys", giveVirtualKeysInventory);

        other.set("crate-settings.keys.key-sound.name", needKeySound);

        other.set("crate-settings.quad-crate.timer", quadCrateTimer);

        other.set("crate-settings.disabled-worlds.worlds", disabledWorlds);

        other.set("gui-settings.filler-items.toggle", fillerToggle);
        other.set("gui-settings.filler-items.item", fillerItem);
        other.set("gui-settings.filler-items.name", fillerName);

        other.set("gui-settings.filler-items.lore", fillerLore);

        other.set("gui-settings.buttons.menu.item", menuItem);
        other.set("gui-settings.buttons.menu.name", menuName);
        other.set("gui-settings.buttons.menu.lore", menuLore);

        other.set("gui-settings.buttons.next.item", nextItem);
        other.set("gui-settings.buttons.next.name", nextName);
        other.set("gui-settings.buttons.next.lore", nextLore);

        other.set("gui-settings.buttons.back.item", backItem);
        other.set("gui-settings.buttons.back.name", backName);
        other.set("gui-settings.buttons.back.lore", backLore);

        other.set("gui-settings.customizer.items", guiCustomizer);

        try {
            other.save(input);
            output.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void copyMessages() {
        // The messages.yml
        File input = new File(path + "/messages.yml");

        File output = new File(path + "/locale/en-US.yml");
        
        // The old configuration of messages.yml
        YamlConfiguration yamlConfiguration = null;
        // The configuration of /locale/en-US.yml
        YamlConfiguration secondConfiguration = null;

        try {
            if (input.exists()) yamlConfiguration = YamlConfiguration.loadConfiguration(input);
            
            if (!output.exists()) output.createNewFile();
            
            if (input.exists()) secondConfiguration = YamlConfiguration.loadConfiguration(output);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (yamlConfiguration == null) return;

        yamlConfiguration.options().charset(StandardCharsets.UTF_8);
        yamlConfiguration.options().quoteStyleDefaults().setDefaultQuoteStyle(QuoteStyle.PLAIN);

        // All the values of the old file.
        final String unknownCommand = yamlConfiguration.getString("Messages.Unknown-Command");

        final String noTeleporting = yamlConfiguration.getString("Messages.No-Teleporting");

        final String noCommandsWhileInCrate = yamlConfiguration.getString("Messages.No-Commands-While-In-Crate");

        final String noKeys = yamlConfiguration.getString("Messages.No-Key");

        final String noVirtualKeys = yamlConfiguration.getString("Messages.No-Virtual-Key");

        final String noPrizesFound = yamlConfiguration.getString("Messages.No-Prizes-Found");

        final String noSchematicsFound = yamlConfiguration.getString("Messages.No-Schematics-Found");

        final String internalError = yamlConfiguration.getString("Messages.Internal-Error");

        final String mustBePlayer = yamlConfiguration.getString("Messages.Must-Be-A-Player");
        final String mustBeConsole = yamlConfiguration.getString("Messages.Must-Be-A-Console-Sender");
        final String mustBeLookingAtBlock = yamlConfiguration.getString("Messages.Must-Be-Looking-At-A-Block");

        final String featureDisabled = yamlConfiguration.getString("Messages.Feature-Disabled");

        final String correctUsage = yamlConfiguration.getString("Messages.Correct-Usage");


        final String playerNotOnline = yamlConfiguration.getString("Messages.Not-Online");

        final String samePlayer = yamlConfiguration.getString("Messages.Same-Player");

        final String noPermission = yamlConfiguration.getString("Messages.No-Permission");

        final String inventoryFull = yamlConfiguration.getString("Messages.Inventory-Full");

        final String obtainingKeys = yamlConfiguration.getString("Messages.Obtaining-Keys");

        final String closeAnotherPlayer = yamlConfiguration.getString("Messages.To-Close-To-Another-Player");

        final String notACrate = yamlConfiguration.getString("Messages.Not-A-Crate");
        final String notANumber = yamlConfiguration.getString("Messages.Not-A-Number");
        final String notOnBlock = yamlConfiguration.getString("Messages.Not-On-Block");
        final String outOfTime = yamlConfiguration.getString("Messages.Out-Of-Time");

        final String previewDisabled = yamlConfiguration.getString("Messages.Preview-Disabled");

        final String crateAlreadyOpened = yamlConfiguration.getString("Messages.Crate-Already-Opened");

        final String quickCrateInUse = yamlConfiguration.getString("Messages.Quick-Crate-In-Use");

        final String cannotBeVirtualCrate = yamlConfiguration.getString("Messages.Cant-Be-A-Virtual-Crate");
        final String needsRoom = yamlConfiguration.getString("Messages.Needs-More-Room");

        final String worldDisabled = yamlConfiguration.getString("Messages.World-Disabled");
        final String removedPhysCrate = yamlConfiguration.getString("Messages.Removed-Physical-Crate");

        final String openedCrate = yamlConfiguration.getString("Messages.Opened-A-Crate");

        final String givenPlayerKeys = yamlConfiguration.getString("Messages.Given-A-Player-Keys");

        final String cannotGivePlayerKeys = yamlConfiguration.getString("Messages.Cannot-Give-Player-Keys");

        final String givenEveryoneKeys = yamlConfiguration.getString("Messages.Given-Everyone-Keys");

        final String givenOfflinePlayerKeys = yamlConfiguration.getString("Messages.Given-Offline-Player-Keys");

        final String takePlayerKeys = yamlConfiguration.getString("Messages.Take-A-Player-Keys");

        final String takeOfflineKeys = yamlConfiguration.getString("Messages.Take-Offline-Player-Keys");

        final String noItemInHand = yamlConfiguration.getString("Messages.No-Item-Hand");
        final String addedItem = yamlConfiguration.getString("Messages.Added-Item-With-Editor");

        final String filesConvertedNone = yamlConfiguration.getString("Messages.Files-Converted.No-Files-To-Convert");
        final String filesConvertedError = yamlConfiguration.getString("Messages.Files-Converted.Error-Converting-Files");

        final String reload = yamlConfiguration.getString("Messages.Reload");

        final String transferKeys = yamlConfiguration.getString("Messages.Transfer-Keys.Not-Enough-Keys");
        final String transferredKeys = yamlConfiguration.getString("Messages.Transfer-Keys.Transferred-Keys");

        final String gotTransferKeys = yamlConfiguration.getString("Messages.Transfer-Keys.Received-Transferred-Keys");

        final String personalNoVirtualKeys = yamlConfiguration.getString("Messages.Keys.Personal.No-Virtual-Keys");

        final String otherPlayer = yamlConfiguration.getString("Messages.Keys.Other-Player.No-Virtual-Keys");

        final String perCrate = yamlConfiguration.getString("Messages.Keys.Per-Crate");

        if (secondConfiguration == null) return;

        secondConfiguration.options().charset(StandardCharsets.UTF_8);
        secondConfiguration.options().quoteStyleDefaults().setDefaultQuoteStyle(QuoteStyle.PLAIN);

        secondConfiguration.set("misc.unknown-command", unknownCommand);
        secondConfiguration.set("misc.no-teleporting", noTeleporting);
        secondConfiguration.set("misc.no-commands", noCommandsWhileInCrate);
        secondConfiguration.set("misc.no-keys", noKeys);
        secondConfiguration.set("misc.no-virtual-keys", noVirtualKeys);

        secondConfiguration.set("misc.feature-disabled", featureDisabled);
        secondConfiguration.set("misc.correct-usage", correctUsage);

        secondConfiguration.set("errors.no-prizes-found", noPrizesFound);

        secondConfiguration.set("errors.no-schematics-found", noSchematicsFound);

        secondConfiguration.set("errors.internal-error", internalError);

        secondConfiguration.set("player.requirements.must-be-player", mustBePlayer);
        secondConfiguration.set("player.requirements.must-be-console-sender", mustBeConsole);
        secondConfiguration.set("player.requirements.must-be-looking-at-block", mustBeLookingAtBlock);

        secondConfiguration.set("player.target-not-online", playerNotOnline);
        secondConfiguration.set("player.target-same-player", samePlayer);
        secondConfiguration.set("player.no-permission", noPermission);
        secondConfiguration.set("player.inventory-not-empty", inventoryFull);
        secondConfiguration.set("player.obtaining-keys", obtainingKeys);
        secondConfiguration.set("player.too-close-to-another-player", closeAnotherPlayer);

        secondConfiguration.set("crates.requirements.not-a-crate", notACrate);
        secondConfiguration.set("crates.requirements.not-a-number", notANumber);
        secondConfiguration.set("crates.not-on-block", notOnBlock);
        secondConfiguration.set("crates.out-of-time", outOfTime);
        secondConfiguration.set("crates.crate-preview-disabled", previewDisabled);
        secondConfiguration.set("crates.crate-already-open", crateAlreadyOpened);
        secondConfiguration.set("crates.crate-in-use", quickCrateInUse);
        secondConfiguration.set("crates.cannot-be-a-virtual-crate", cannotBeVirtualCrate);
        secondConfiguration.set("crates.need-more-room", needsRoom);
        secondConfiguration.set("crates.world-disabled", worldDisabled);
        secondConfiguration.set("crates.physical-crate.removed", removedPhysCrate);

        secondConfiguration.set("command.open.opened-a-crate", openedCrate);

        secondConfiguration.set("command.give.given-player-keys", givenPlayerKeys);
        secondConfiguration.set("command.give.cannot-give-player-keys-because-inventory-not-empty", cannotGivePlayerKeys);
        secondConfiguration.set("command.give.given-everyone-keys", givenEveryoneKeys);
        secondConfiguration.set("command.give.given-offline-player-keys", givenOfflinePlayerKeys);

        secondConfiguration.set("command.take.take-player-keys", takePlayerKeys);
        secondConfiguration.set("command.take.take-offline-player-keys", takeOfflineKeys);

        secondConfiguration.set("command.additem.no-item-in-hand", noItemInHand);
        secondConfiguration.set("command.additem.add-item-from-hand", addedItem);

        secondConfiguration.set("command.convert.no-files-to-convert", filesConvertedNone);
        secondConfiguration.set("command.convert.error-converting-files", filesConvertedError);

        secondConfiguration.set("command.reload.reload-complete", reload);

        secondConfiguration.set("command.transfer.not-enough-keys", transferKeys);
        secondConfiguration.set("command.transfer.transferred-keys", transferredKeys);
        secondConfiguration.set("command.transfer.transferred-keys-received", gotTransferKeys);

        secondConfiguration.set("command.keys.personal.no-virtual-keys", personalNoVirtualKeys);
        secondConfiguration.set("command.keys.other-player.no-virtual-keys", otherPlayer);

        secondConfiguration.set("command.keys.crate-format", perCrate);

        //FileUtils.extract("/locale/", CrazyCore.api().getDirectory().resolve("locale"), false);
    }
}