package com.badbones69.crazycrates.paper.commands.crates.types.admin;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.support.MetricsWrapper;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.ryderbelserion.fusion.core.api.enums.FileAction;
import com.ryderbelserion.fusion.core.api.enums.FileType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bstats.bukkit.Metrics;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    @Syntax("/crazycrates reload")
    public void reload(CommandSender sender) {
        this.plugin.getFusion().reload();

        MiscUtils.janitor();

        this.plugin.getInstance().reload();

        this.fileManager.refresh(false).addFile(this.path.resolve("locations.yml"), FileType.PAPER, new ArrayList<>() {{
                    add(FileAction.STATIC_FILE);
                    add(FileAction.RELOAD_FILE);
                }}, null)
                .addFile(this.path.resolve("data.yml"), FileType.PAPER, new ArrayList<>() {{
                    add(FileAction.STATIC_FILE);
                    add(FileAction.RELOAD_FILE);
                }}, null)
                .addFile(this.path.resolve("guis").resolve("respin-gui.yml"), FileType.PAPER, new ArrayList<>() {{
                    add(FileAction.STATIC_FILE);
                    add(FileAction.RELOAD_FILE);
                }}, null)
                .addFolder(this.path.resolve("logs"), FileType.LOG, new ArrayList<>() {{
                    add(FileAction.EXTRACT_FOLDER);
                    add(FileAction.STATIC_FILE);
                    add(FileAction.RELOAD_FILE);
                }}, null)
                .addFolder(this.path.resolve("crates"), FileType.PAPER, new ArrayList<>() {{
                    add(FileAction.EXTRACT_FOLDER);
                }}, null)
                .addFolder(this.path.resolve("schematics"), FileType.NBT, new ArrayList<>() {{
                    add(FileAction.EXTRACT_FOLDER);
                }}, null);

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