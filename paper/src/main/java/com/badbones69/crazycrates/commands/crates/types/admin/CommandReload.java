package com.badbones69.crazycrates.commands.crates.types.admin;

import com.badbones69.crazycrates.api.enums.CustomFiles;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.ryderbelserion.vital.core.config.YamlFile;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    public void reload(CommandSender sender) {
        ConfigManager.refresh();

        final YamlFile locations = CustomFiles.locations.getYamlFile();
        final YamlFile data = CustomFiles.data.getYamlFile();

        if (!locations.contains("Locations")) {
            locations.set("Locations.Clear", null);

            CustomFiles.locations.save();
        }

        if (!data.contains("Players")) {
            data.set("Players.Clear", null);

            CustomFiles.data.save();
        }

        if (this.config.getProperty(ConfigKeys.take_out_of_preview)) {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (this.inventoryManager.inCratePreview(player)) {
                    this.inventoryManager.closeCratePreview(player);

                    if (this.config.getProperty(ConfigKeys.send_preview_taken_out_message)) {
                        player.sendRichMessage(Messages.reloaded_forced_out_of_preview.getMessage(player));
                    }
                }
            });
        }

        this.crateManager.loadHolograms();

        this.crateManager.loadCrates();

        sender.sendRichMessage(Messages.reloaded_plugin.getMessage(sender));
    }
}