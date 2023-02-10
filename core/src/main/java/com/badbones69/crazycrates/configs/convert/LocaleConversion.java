package com.badbones69.crazycrates.configs.convert;

import com.badbones69.crazycrates.configs.Locale;
import com.badbones69.crazycrates.utils.FileUtils;
import com.badbones69.crazycrates.utils.adventure.MsgWrapper;
import net.dehya.ruby.files.FileManager;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class LocaleConversion {

    public void convertMessages(FileManager fileManager, Path directory) {
        // The messages.yml
        File input = new File(directory + "/messages.yml");

        // The old configuration of messages.yml
        YamlConfiguration yamlConfiguration = null;

        try {
            if (input.exists()) yamlConfiguration = YamlConfiguration.loadConfiguration(input);
        } catch (IOException e) {
            MsgWrapper.send(e.getMessage());
        }

        if (yamlConfiguration == null) return;

        ConfigurationSection msgSection = yamlConfiguration.createSection("Messages");

        if (msgSection.getString("No-Teleporting") == null && !input.exists()) {
            MsgWrapper.send("<#11e092>" + Locale.getConfig(fileManager, directory).getName() + " <#E0115F>is up to date!");
            return;
        }

        // All the values of the old file.
        final String unknownCommand = msgSection.getString("Unknown-Command");
        final String noTeleporting = msgSection.getString("No-Teleporting");

        final String noCommandsWhileInCrate = msgSection.getString("No-Commands-While-In-Crate");

        final String noKeys = msgSection.getString("No-Key");
        final String noVirtualKeys = msgSection.getString("No-Virtual-Key");

        final String noPrizesFound = msgSection.getString("No-Prizes-Found");
        final String noSchematicsFound = msgSection.getString("No-Schematics-Found");

        final String internalError = msgSection.getString("Internal-Error");

        final String mustBePlayer = msgSection.getString("Must-Be-A-Player");
        final String mustBeConsole = msgSection.getString("Must-Be-A-Console-Sender");
        final String mustBeLookingAtBlock = msgSection.getString("Must-Be-Looking-At-A-Block");

        final String featureDisabled = msgSection.getString("Feature-Disabled");

        final String correctUsage = msgSection.getString("Correct-Usage");

        org.simpleyaml.configuration.file.YamlConfiguration configuration = Locale.getConfiguration(fileManager, directory);

        if (configuration == null) return;

        ConfigurationSection miscSection = configuration.createSection("misc");

        miscSection.addDefault("unknown-command", unknownCommand);
        miscSection.addDefault("no-teleporting", noTeleporting);
        miscSection.addDefault("no-commands", noCommandsWhileInCrate);
        miscSection.addDefault("no-keys", noKeys);
        miscSection.addDefault("no-virtual-keys", noVirtualKeys);

        miscSection.addDefault("feature-disabled", featureDisabled);
        miscSection.addDefault("correct-usage", correctUsage);

        ConfigurationSection errorSection = configuration.createSection("errors");

        errorSection.addDefault("no-prizes-found", noPrizesFound);
        errorSection.addDefault("no-schematics-found", noSchematicsFound);

        errorSection.addDefault("internal-error", internalError);

        ConfigurationSection playerSection = configuration.createSection("player");

        playerSection.addDefault("requirements.must-be-player", mustBePlayer);
        playerSection.addDefault("requirements.must-be-console-sender", mustBeConsole);
        playerSection.addDefault("requirements.must-be-looking-at-block", mustBeLookingAtBlock);

        final String playerNotOnline = msgSection.getString("Not-Online");

        final String samePlayer = msgSection.getString("Same-Player");

        final String noPermission = msgSection.getString("No-Permission");

        final String inventoryFull = msgSection.getString("Inventory-Full");

        final String obtainingKeys = msgSection.getString("Obtaining-Keys");

        final String closeAnotherPlayer = msgSection.getString("To-Close-To-Another-Player");

        playerSection.addDefault("target-not-online", playerNotOnline);
        playerSection.addDefault("target-same-player", samePlayer);
        playerSection.addDefault("no-permission", noPermission);
        playerSection.addDefault("inventory-not-empty", inventoryFull);
        playerSection.addDefault("obtaining-keys", obtainingKeys);
        playerSection.addDefault("too-close-to-another-player", closeAnotherPlayer);

        final String notACrate = msgSection.getString("Not-A-Crate");
        final String notANumber = msgSection.getString("Not-A-Number");
        final String notOnBlock = msgSection.getString("Not-On-Block");
        final String outOfTime = msgSection.getString("Out-Of-Time");

        final String previewDisabled = msgSection.getString("Preview-Disabled");

        final String crateAlreadyOpened = msgSection.getString("Crate-Already-Opened");

        final String quickCrateInUse = msgSection.getString("Quick-Crate-In-Use");

        final String cannotBeVirtualCrate = msgSection.getString("Cant-Be-A-Virtual-Crate");
        final String needsRoom = msgSection.getString("Needs-More-Room");

        final String worldDisabled = msgSection.getString("World-Disabled");

        final List<String> createdPhysicalCrate = msgSection.getStringList("Created-Physical-Crate");
        final String removedPhysCrate = msgSection.getString("Removed-Physical-Crate");

        ConfigurationSection crateSection = configuration.createSection("crates");

        crateSection.addDefault("requirements.not-a-crate", notACrate);
        crateSection.addDefault(".requirements.not-a-number", notANumber);
        crateSection.addDefault(".not-on-block", notOnBlock);
        crateSection.addDefault(".out-of-time", outOfTime);
        crateSection.addDefault(".crate-preview-disabled", previewDisabled);
        crateSection.addDefault("crate-already-open", crateAlreadyOpened);
        crateSection.addDefault("crate-in-use", quickCrateInUse);
        crateSection.addDefault("cannot-be-a-virtual-crate", cannotBeVirtualCrate);
        crateSection.addDefault("need-more-room", needsRoom);
        crateSection.addDefault("world-disabled", worldDisabled);
        crateSection.addDefault("physical-crate.created", createdPhysicalCrate);
        crateSection.addDefault("physical-crate.removed", removedPhysCrate);

        final String openedCrate = msgSection.getString("Opened-A-Crate");
        
        ConfigurationSection commandSection = configuration.createSection("command");

        commandSection.addDefault("open.opened-a-crate", openedCrate);

        final String givenPlayerKeys = msgSection.getString("Given-A-Player-Keys");
        final String cannotGivePlayerKeys = msgSection.getString("Cannot-Give-Player-Keys");
        final String givenEveryoneKeys = msgSection.getString("Given-Everyone-Keys");
        final String givenOfflinePlayerKeys = msgSection.getString("Given-Offline-Player-Keys");

        commandSection.addDefault("give.given-player-keys", givenPlayerKeys);
        commandSection.addDefault("give.cannot-give-player-keys-because-inventory-not-empty", cannotGivePlayerKeys);
        commandSection.addDefault("give.given-everyone-keys", givenEveryoneKeys);
        commandSection.addDefault("give.given-offline-player-keys", givenOfflinePlayerKeys);

        final String takePlayerKeys = msgSection.getString("Take-A-Player-Keys");
        final String takeOfflineKeys = msgSection.getString("Take-Offline-Player-Keys");

        commandSection.addDefault("take.take-player-keys", takePlayerKeys);
        commandSection.addDefault("take.take-offline-player-keys", takeOfflineKeys);

        final String noItemInHand = msgSection.getString("No-Item-Hand");
        final String addedItem = msgSection.getString("Added-Item-With-Editor");

        commandSection.addDefault("additem.no-item-in-hand", noItemInHand);
        commandSection.addDefault("additem.add-item-from-hand", addedItem);

        final String filesConvertedNone = msgSection.getString("Files-Converted.No-Files-To-Convert");
        final String filesConvertedError = msgSection.getString("Files-Converted.Error-Converting-Files");

        commandSection.addDefault("convert.no-files-to-convert", filesConvertedNone);
        commandSection.addDefault("convert.error-converting-files", filesConvertedError);

        final String reload = msgSection.getString("Reload");

        commandSection.addDefault("reload.reload-complete", reload);

        final String transferKeys = msgSection.getString("Transfer-Keys.Not-Enough-Keys");
        final String transferredKeys = msgSection.getString("Transfer-Keys.Transferred-Keys");
        final String gotTransferKeys = msgSection.getString("Transfer-Keys.Received-Transferred-Keys");

        commandSection.addDefault("transfer.not-enough-keys", transferKeys);
        commandSection.addDefault("transfer.transferred-keys", transferredKeys);
        commandSection.addDefault("transfer.transferred-keys-received", gotTransferKeys);

        final String personalNoVirtualKeys = msgSection.getString("Keys.Personal.No-Virtual-Keys");
        final List<String> personalHeader = msgSection.getStringList("Keys.Personal.Header");
        final String otherPlayer = msgSection.getString("Keys.Other-Player.No-Virtual-Keys");
        final List<String> otherPlayerHeader = msgSection.getStringList("Keys.Other-Player.Header");
        final String perCrate = msgSection.getString("Keys.Per-Crate");

        commandSection.addDefault("keys.personal.no-virtual-keys", personalNoVirtualKeys);
        commandSection.addDefault("keys.personal.virtual-keys-header", personalHeader);
        commandSection.addDefault("keys.other-player.no-virtual-keys", otherPlayer);
        commandSection.addDefault("keys.other-player.virtual-keys-header", otherPlayerHeader);

        commandSection.addDefault("keys.crate-format", perCrate);

        FileUtils.copyFile(new File(directory + "/locale/locale-en.yml"), input, configuration, directory);
    }
}