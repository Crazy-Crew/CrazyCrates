package com.badbones69.crazycrates.paper.commands.crates.types.admin;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.support.MetricsWrapper;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    public void reload(CommandSender sender) {
        this.plugin.getFusion().reload();

        MiscUtils.janitor();

        this.plugin.getInstance().reload();

        this.fileManager.reloadFiles().init();

        MiscUtils.save();

        if (this.config.getProperty(ConfigKeys.take_out_of_preview)) {
            this.inventoryManager.closePreview();
        }

        @Nullable final MetricsWrapper metrics = this.plugin.getMetrics();

        if (metrics != null && !this.config.getProperty(ConfigKeys.toggle_metrics)) {
            final Metrics scheduler = metrics.getMetrics();

            if (scheduler != null) {
                scheduler.shutdown();
            }
        }

        this.crateManager.loadHolograms();

        this.crateManager.loadCrates();

        Messages.reloaded_plugin.sendMessage(sender);
    }
}