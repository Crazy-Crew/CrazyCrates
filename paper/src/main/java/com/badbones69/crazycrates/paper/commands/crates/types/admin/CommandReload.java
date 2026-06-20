package com.badbones69.crazycrates.paper.commands.crates.types.admin;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.support.MetricsWrapper;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.common.config.impl.ConfigKeys;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.files.enums.FileAction;
import com.ryderbelserion.fusion.files.enums.FileType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    @Syntax("/crazycrates reload")
    public void reload(CommandSender sender) {
        this.plugin.getFusion().reload();

        MiscUtils.janitor();

        this.plugin.getInstance().reload();

        final Path version = this.path.resolve("version.json");

        try {
            Files.deleteIfExists(version);
        } catch (final IOException exception) {
            this.fusion.log(Level.WARNING, "Failed to delete version.json!", exception);
        }

        this.fileManager.refresh(false)
                .addFolder(this.path.resolve("schematics"), FileType.NBT)
                .addFolder(this.path.resolve("logs"), FileType.LOG, action -> action.addAction(FileAction.STATIC_FILE))
                .addFile(version, FileType.JSON);

        this.fileManager.addPaperFolder(this.path.resolve("crates"))
                .addPaperFile(this.path.resolve("guis").resolve("respin-gui.yml"))
                .addPaperFile(this.path.resolve("data.yml"))
                .addPaperFile(this.path.resolve("locations.yml"));

        MiscUtils.save();

        if (this.config.getProperty(ConfigKeys.take_out_of_preview)) {
            this.inventoryManager.closePreview();
        }

        final MetricsWrapper metrics = this.plugin.getMetrics();

        if (metrics != null && !this.config.getProperty(ConfigKeys.toggle_metrics)) {
            final Metrics scheduler = metrics.getMetrics();

            scheduler.shutdown();
        }

        this.crateManager.loadHolograms();

        this.crateManager.loadCrates();

        Messages.reloaded_plugin.sendMessage(sender);
    }
}