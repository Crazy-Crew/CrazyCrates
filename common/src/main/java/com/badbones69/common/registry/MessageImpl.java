package com.badbones69.common.registry;

import com.badbones69.common.CrazyCratesPlugin;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.YamlMessageAdapter;
import com.ryderbelserion.fusion.files.FileManager;
import com.ryderbelserion.fusion.kyori.FusionKyori;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.CrazyCrates;
import us.crazycrew.crazycrates.api.constants.MessageKeys;
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

                    final FusionKey key = FusionKey.key(CrazyCrates.namespace,
                            fileName.equalsIgnoreCase("messages.yml") ? "default" : fileName.toLowerCase());

                    final CommentedConfigurationNode configuration = file.getConfiguration();

                    action.addKey(key, MessageKeys.no_prizes_found, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>This crate contains no prizes that you can win.",
                            "errors", "no-prizes-found"
                    ));

                    action.addKey(key, MessageKeys.prize_not_found, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>The prize named {prize} entered could not be found.",
                            "errors", "prize-not-found"
                    ));

                    action.addKey(key, MessageKeys.prize_error, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>An error has occurred while trying to give you the prize in crate called <gold>{crate}<red>. Please contact the server owner and show them this error.",
                            "errors", "prize-error"
                    ));

                    action.addKey(key, MessageKeys.internal_error, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>An internal error has occurred. Please check the console for the full error.",
                            "errors", "internal-error"
                    ));

                    action.addKey(key, MessageKeys.cannot_be_empty, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>{value} cannot be empty!",
                            "errors", "cannot-be-empty"
                    ));

                    action.addKey(key, MessageKeys.cannot_be_air, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>You can't use air silly!~",
                            "errors", "cannot-be-air"
                    ));

                    action.addKey(key, MessageKeys.no_schematics_found, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>No schematics were found. Please make sure files ending in .nbt exist in the schematics folder. If not delete the folder so they regenerate.",
                            "errors", "no-schematics-found"
                    ));

                    action.addKey(key, MessageKeys.key_refund, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>An error has occurred with the crate {crate} that you were opening, A refund for your key has been given.",
                            "errors", "key-refund"
                    ));

                    action.addKey(key, MessageKeys.no_teleporting, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>You may not teleport away while opening a crate.",
                            "misc", "no-teleporting"
                    ));

                    action.addKey(key, MessageKeys.no_commands_while_using_crate, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>You are not allowed to use commands while opening crates.",
                            "misc", "no-commands"
                    ));

                    action.addKey(key, MessageKeys.unknown_command, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>{command} is not a known command.",
                            "misc", "unknown-command"
                    ));

                    action.addKey(key, MessageKeys.no_virtual_key, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>You need a virtual key to open <gold>{crate}.",
                            "misc", "no-virtual-keys"
                    ));

                    action.addKey(key, MessageKeys.no_keys, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>You must have a {key} <red>in your hand to use <gold>{crate}.",
                            "misc", "no-keys"
                    ));

                    action.addKey(key, MessageKeys.correct_usage, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>The correct usage for this command is <yellow>{usage}",
                            "misc", "correct-usage"
                    ));

                    action.addKey(key, MessageKeys.feature_disabled, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>This feature is disabled.",
                            "misc", "correct-usage"
                    ));

                    action.addKey(key, MessageKeys.lacking_flag, new YamlMessageAdapter(
                            configuration,
                            "{prefix}<red>{flag} is not present in the command, expected format: {usage}",
                            "misc", "lacking-flag"
                    ));
                }, () -> this.fusion.log(Level.INFO, "Path %s not found in cache.".formatted(path)));
            }
        });
    }
}