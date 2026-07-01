package com.badbones69.common.api.enums;

import ch.jalu.configme.SettingsManager;
import com.badbones69.common.CrazyCratesPlugin;
import com.badbones69.common.config.ConfigManager;
import com.badbones69.common.config.impl.ConfigKeys;
import com.badbones69.common.enums.State;
import com.ryderbelserion.fusion.core.api.FusionKey;
import com.ryderbelserion.fusion.core.api.registry.message.MessageRegistry;
import com.ryderbelserion.fusion.core.api.registry.message.adapter.YamlMessageAdapter;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import net.kyori.adventure.audience.Audience;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.CratesProvider;
import us.crazycrew.crazycrates.api.adapters.sender.ISenderAdapter;

import java.util.List;
import java.util.Map;
import static us.crazycrew.crazycrates.api.CrazyCrates.namespace;

@NullMarked
public enum PluginMessages {

    prize_not_found("errors.prize.not.found", "{prefix}<red>This crate contains no prizes that you can win.", "errors", "no-prizes-found"),
    prize_error("errors.prize.error", "{prefix}<red>An error has occurred trying to give you a prize from crate <gold>{crate}<red>.", "errors", "prize-error"),

    schematics_empty("errors.schematics.empty", "{prefix}<red>No schematics were found. Files must end in .nbt", "errors", "no-schematics-found"),
    prizes_empty("errors.prizes.empty", "{prefix}<red>This crate contains no prizes that you can win.", "errors", "no-prizes-found"),

    cannot_be_empty("errors.cannot.be.empty", "{prefix}<red>{value} cannot be empty!", "errors", "cannot-be-empty"),
    cannot_be_air("errors.cannot.be.air", "{prefix}<red>You can't use air silly!~", "errors", "cannot-be-air"),

    internal_error("errors.internal.error", "{prefix}<red>An internal error has occurred. Please check the console for the full error.", "errors", "internal-error"),

    no_virtual_keys("misc.no.virtual.keys", "{prefix}<red>You need a virtual key to open <gold>{crate}.", "misc", "no-virtual-keys"),
    no_physical_keys("misc.no.physical.keys", "{prefix}<red>You must have a {key} <red>in your hand to use <gold>{crate}.", "misc", "no-keys"),

    no_teleporting_in_crate("misc.no.teleporting.in.crate", "{prefix}<red>You may not teleport away while opening a crate.", "misc", "no-teleporting"),
    no_command_in_crate("misc.no.command.in.crate", "{prefix}<red>You are not allowed to use commands while opening crates.", "misc", "no-commands"),

    command_unknown("misc.unknown.command", "{prefix}<red>{command} is not a known command.", "misc", "unknown-command"),
    command_usage("misc.correct.command.usage", "{prefix}<red>The correct usage for this command is <yellow>{usage}.", "misc", "correct-usage"),
    command_lacking_flag("misc.lacking.flag", "prefix}<red>{flag} is not present in the command, expected format: {usage}", "misc", "lacking-flag"),

    not_a_crate("crates.not.a.crate", "{prefix}<red>There is no crate called <gold>{crate}.", "crates", "requirements", "not-a-crate"),
    not_a_key("crates.not.a.key", "{prefix}<red>There is no key called <gold>{key}.", "crates", "requirements", "not-a-number"),
    not_a_number("crates.not.a.number", "{prefix}<gold>{number} <red>is not a number.", "crates", "requirements", "not-a-number"),
    not_on_block("crates.not.enough.keys", "{prefix}<gray>You need <red>{key_amount} <gray>keys to open <red>{crate}. <gray>You have <red>{amount}.", "crates", "requirements", "not-enough-keys"),
    not_enough_keys("crates.not.on.block", "{prefix}<red>You must be standing on a block to use <gold>{crate}.", "crates", "not-a-block"),

    crate_respins_max("crates.respins-max", "{prefix}<red>You can no longer respin, Status: {status}", "crates", "respins", "max"),
    crate_respins_claimed("crates.respins.claimed", "{prefix}<red>You have claimed {amount} prizes from your respins!", "crates", "respins", "claimed"),
    crate_respins_redeemed("crates.respins.redeemed", "{prefix}<red>You have redeemed your prize with the name {prize} from the crate named {crate}!", "crates", "respins", "redeemed"),

    crate_pulls_max("crates.pulls.max", "{prefix}<red>This prize can no longer be obtained, {pulls}/{maxpulls}", "crates", "pulls", "max"),

    feature_disabled("misc.feature.disabled", "{prefix}<red>This feature is disabled.", "misc", "feature-disabled");

    private final CrazyCratesPlugin platform = (CrazyCratesPlugin) CratesProvider.api();

    private final ISenderAdapter senderAdapter = this.platform.getSenderAdapter();

    private final SettingsManager config = ConfigManager.getConfig();

    private final String defaultValue;
    private final FusionKey id;
    private final Object[] path;

    PluginMessages(final String id, final String defaultValue, final Object... path) {
        this.id = FusionKey.key(namespace, id);
        this.defaultValue = defaultValue;
        this.path = path;
    }

    public void addKey(final MessageRegistry registry, final CommentedConfigurationNode configuration, final FusionKey id) {
        registry.addKey(
                id,
                this.id,
                new YamlMessageAdapter(
                        configuration,
                        this.defaultValue,
                        this.path
                )
        );
    }

    public void sendMessage(final Audience audience, final Map<String, String> placeholders) {
        final State state = this.config.getProperty(ConfigKeys.message_state);

        switch (state) {
            case send_message -> this.senderAdapter.sendMessage(audience, this.id, placeholders);

            case send_actionbar -> this.senderAdapter.sendActionBar(audience, this.id, placeholders);
        }
    }

    public void sendMessage(final Audience audience, final String placeholder, final String value) {
        sendMessage(audience, Map.of(placeholder, value));
    }

    public void sendMessage(final Audience audience) {
        sendMessage(audience, Map.of());
    }

    public FusionKey getKey() {
        return this.id;
    }
}