package com.badbones69.crazycrates.commands.crates.types.admin;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.support.MetricsWrapper;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.common.config.impl.ConfigKeys;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import java.util.concurrent.ScheduledExecutorService;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    public void reload(CommandSender sender) {
        this.plugin.getVital().reload();

        MiscUtils.janitor();

        this.plugin.getInstance().reload();

        this.fileManager.reloadFiles().init();

        MiscUtils.save();

        if (this.config.getProperty(ConfigKeys.take_out_of_preview)) {
            this.inventoryManager.closePreview();
        }

        @Nullable final MetricsWrapper metrics = this.plugin.getMetrics();

        if (metrics != null && !this.config.getProperty(ConfigKeys.toggle_metrics)) {
            @Nullable final ScheduledExecutorService scheduler = metrics.getScheduler();

            if (scheduler != null) {
                scheduler.shutdown();
            }
        }

        this.crateManager.loadHolograms();

        this.crateManager.loadCrates();

        Messages.reloaded_plugin.sendMessage(sender);
    }
}