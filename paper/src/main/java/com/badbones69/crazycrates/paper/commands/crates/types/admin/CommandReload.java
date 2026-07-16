package com.badbones69.crazycrates.paper.commands.crates.types.admin;

import us.crazycrew.crazycrates.api.config.impl.types.config.crate.CrateKeys;
import us.crazycrew.crazycrates.api.enums.messages.Message;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import com.ryderbelserion.fusion.files.enums.FileType;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Syntax;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.PermissionDefault;

public class CommandReload extends BaseCommand {

    @Command("reload")
    @Permission(value = "crazycrates.reload", def = PermissionDefault.OP)
    @Syntax("/crazycrates reload")
    public void reload(CommandSender sender) {
        MiscUtils.janitor();

        this.platform.reload();

        this.fileManager.refresh(false)
                .addFolder(this.path.resolve("schematics"), FileType.NBT)
                .addFolder(this.path.resolve("buttons"), FileType.YAML);

        this.fileManager.addPaperFolder(this.path.resolve("crates"))
                .addPaperFolder(this.path.resolve("guis"))
                .addPaperFile(this.path.resolve("data.yml"));

        this.buttonManager.load();

        MiscUtils.save();

        if (this.pluginConfig.getProperty(CrateKeys.take_out_of_preview)) {
            this.inventoryManager.closePreview();
        }

        this.crateManager.loadHolograms();

        this.crateManager.loadCrates();

        Message.command_reload.sendMessage(sender);
    }
}