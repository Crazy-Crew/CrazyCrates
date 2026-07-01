package com.badbones69.common.registry;

import com.badbones69.common.CrazyCratesPlugin;
import com.badbones69.common.api.enums.PluginMessages;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import java.nio.file.Path;
import java.util.List;

public class MessageImpl {

    private final CrazyCratesPlugin plugin = (CrazyCratesPlugin) CratesProvider.api();

    private final FusionKyori fusion = this.plugin.getFusion();

    private final MessageRegistry registry = this.fusion.getMessageRegistry();

    private final FileManager fileManager = this.fusion.getFileManager();

    private final Path path = this.plugin.getDataPath();

    public void reload() {
        init();
    }

    public void init() {
        final List<Path> paths = this.fileManager.getFilesByPath(this.path.resolve("locale"), ".yml", 1);

        paths.add(this.path.resolve("messages.yml")); // add to list
        
        this.registry.init(action -> {
            for (final Path path : paths) {
                this.fileManager.getYamlFile(path).ifPresentOrElse(file -> {
                    final String fileName = file.getFileName();

                    final FusionKey key = FusionKey.key(CrazyCrates.namespace, fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = file.getConfiguration();

                    for (final PluginMessages message : PluginMessages.values()) {
                        message.addKey(action, configuration, key);
                    }

                    /*action.addKey(key, MessageKeys.key_refund, new YamlMessageAdapter(configuration, "{prefix}<red>An error has occurred with the crate {crate} that you were opening, A refund for your key has been given.", "errors", "key-refund"));

                    // crates
                    action.addKey(key, MessageKeys.out_of_time, new YamlMessageAdapter(configuration, "{prefix}<red>You took <gold>5 minutes <red>to open <gold>{crate} <red>so it closed.", "crates", "out-of-time"));

                    action.addKey(key, MessageKeys.reloaded_forced_out_of_preview, new YamlMessageAdapter(configuration, "{prefix}<red>A reload has forced you out of the preview.", "crates", "forced-out-of-preview"));

                    action.addKey(key, MessageKeys.cant_be_a_virtual_crate, new YamlMessageAdapter(configuration, "{prefix}<gold>{crate} <red>cannot be used as a Virtual Crate.", "crates", "cannot-be-a-virtual-crate"));
                    action.addKey(key, MessageKeys.cannot_set_type, new YamlMessageAdapter(configuration, "{prefix}<red>You cannot set the Menu to a block because the crate menu is disabled", "crates", "cannot-set-menu-type"));

                    action.addKey(key, MessageKeys.no_crate_permission, new YamlMessageAdapter(configuration, "{prefix}<red>You do not have permission to use {crate}.", "crates", "crate-no-permission"));
                    action.addKey(key, MessageKeys.preview_disabled, new YamlMessageAdapter(configuration, "{prefix}<red>The preview for <gold>{crate} <red>is currently disabled.", "crates", "crate-preview-disabled"));
                    action.addKey(key, MessageKeys.already_opening_crate, new YamlMessageAdapter(configuration, "{prefix}<red>You are already opening <gold>{crate}.", "crates", "crate-already-open"));
                    action.addKey(key, MessageKeys.already_redeemed_prize, new YamlMessageAdapter(configuration, "{prefix}<red>You have already redeemed this prize!", "crates", "already-redeemed-prize"));
                    action.addKey(key, MessageKeys.needs_more_room, new YamlMessageAdapter(configuration, "{prefix}<red>There is not enough space to open that here", "crates", "need-more-room"));
                    action.addKey(key, MessageKeys.world_disabled, new YamlMessageAdapter(configuration, "{prefix}<red>I am sorry, but crates are disabled in {world}.", "crates", "world-disabled"));

                    action.addKey(key, MessageKeys.editor_enter, new YamlMessageAdapter(configuration, "{prefix}<red>You are now in editor mode.", "crates", "editor", "enter"));
                    action.addKey(key, MessageKeys.editor_exit, new YamlMessageAdapter(configuration, "{prefix}<red>You are no longer in editor mode.", "crates", "editor", "exit"));
                    action.addKey(key, MessageKeys.editor_already_in, new YamlMessageAdapter(configuration, "{prefix}<red>You are already in the editor mode.", "crates", "editor", "already-in"));
                    action.addKey(key, MessageKeys.force_editor_exit, new YamlMessageAdapter(configuration, "{prefix}<red>You have been forced out of the editor mode for {reason}.", "crates", "editor", "force-exit"));

                    action.addKey(key, MessageKeys.created_physical_crate, new YamlMessageAdapter(configuration, StringUtils.toString(List.of(
                            "{prefix}<gray>You have set that block to {crate}.",
                            "<gray>To remove the crate shift break in creative to remove."
                    )), "crates", "physical-crate", "created"));
                    action.addKey(key, MessageKeys.physical_crate_overridden, new YamlMessageAdapter(configuration, "{prefix}<gray>You have overridden the crate's location with id <gold>{id} with {new_crate}.", "crates", "physical-crate", "override"));
                    action.addKey(key, MessageKeys.physical_crate_already_exists, new YamlMessageAdapter(configuration, "{prefix}<gray>This location already has a crate named <gold>{crate} <gray>with id: <gold>{id}.", "crates", "physical-crate", "exists"));
                    action.addKey(key, MessageKeys.removed_physical_crate, new YamlMessageAdapter(configuration, "{prefix}<gray>You have removed <gold>{id}.", "crates", "physical-crate", "removed"));

                    action.addKey(key, MessageKeys.crate_locations, new YamlMessageAdapter(configuration, StringUtils.toString(List.of(
                            "<bold><gold>━━━━━━━━━━━━━━━━━━━ Crate Statistics ━━━━━━━━━━━━━━━━━━━</gold></bold>",
                            "<dark_gray>»</dark_gray> <green>Active Crates: ",
                            " ⤷ {active_crates}</green>",
                            "<dark_gray>»</dark_gray> <red>Broken Crates: ",
                            " ⤷ {broken_crates}</red>",
                            "<dark_gray>»</dark_gray> <yellow>Crate Locations: ",
                            " ⤷ {active_locations}</yellow>",
                            "",
                            "{locations}",
                            "",
                            "<bold><gold>━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━</gold></bold>"
                    )), "crates", "list", "format"));
                    action.addKey(key, MessageKeys.crate_location_format, new YamlMessageAdapter(configuration, "<dark_gray>[<blue>{id}<dark_gray>]: <red>{crate_name}<dark_gray>, <red>{world}<dark_gray>, <red>{x}<dark_gray>, <red>{y}<dark_gray>, <red>{z}", "crates", "list", "per-crate"));

                    action.addKey(key, MessageKeys.crate_teleported, new YamlMessageAdapter(configuration, "{prefix}<red>You have been teleported to the location with the name: <gold>{name}.", "crates", "teleport", "success"));
                    action.addKey(key, MessageKeys.crate_cannot_teleport, new YamlMessageAdapter(configuration, "{prefix}<red>There is no location with the name: <gold>{id}.", "crates", "teleport", "failed"));

                    action.addKey(key, MessageKeys.crate_in_use, new YamlMessageAdapter(configuration, "{prefix}<red>This prize can no longer be obtained, {pulls}/{maxpulls}", "crates", "pulls", "max"));

                    action.addKey(key, MessageKeys.crate_prize_max_pulls, new YamlMessageAdapter(configuration, "{prefix}<red>You can no longer respin, Status: {status}", "crates", "respins", "max"));
                    action.addKey(key, MessageKeys.crate_prize_max_respins, new YamlMessageAdapter(configuration, "{prefix}<red>You have claimed {amount} prizes from your respins!", "crates", "respins", "claimed"));
                    action.addKey(key, MessageKeys.crate_prize_respins_claimed, new YamlMessageAdapter(configuration, "{prefix}<red>You have redeemed your prize with the name {prize} from the crate named {crate}!", "crates", "respins", "redeemed"));
                    action.addKey(key, MessageKeys.crate_prize_respins_redeemed, new YamlMessageAdapter(configuration, "{prefix}<red>If you didn't get your prize, please run /crazycrates claim {crate}", "crates", "respins", "not-claimed"));
                    action.addKey(key, MessageKeys.crate_prize_respins_empty, new YamlMessageAdapter(configuration, "{prefix}There is no prize to claim from the crate {crate}.", "crates", "respins", "empty"));
                    action.addKey(key, MessageKeys.crate_prize_max_respins_left, new YamlMessageAdapter(configuration, "{respins_left}/{respins_total}", "crates", "respins", "format"));
                    action.addKey(key, MessageKeys.crate_prize_max_respins_none, new YamlMessageAdapter(configuration, "N/A", "crates", "respins", "none"));*/
                }, () -> this.fusion.log(Level.INFO, "Path %s not found in cache.".formatted(path)));
            }
        });
    }
}