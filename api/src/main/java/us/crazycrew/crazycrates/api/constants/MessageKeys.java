package us.crazycrew.crazycrates.api.constants;

import com.ryderbelserion.fusion.core.api.FusionKey;
import org.jetbrains.annotations.ApiStatus;
import static us.crazycrew.crazycrates.api.CrazyCrates.namespace;

@ApiStatus.Internal
public class MessageKeys {

    public static final FusionKey default_locale = FusionKey.key(namespace, "default");

    public static final FusionKey no_prizes_found = FusionKey.key(namespace, "errors.no-prizes-found");

    public static final FusionKey prize_not_found = FusionKey.key(namespace, "errors.prize-not-found");

    public static final FusionKey internal_error = FusionKey.key(namespace, "errors.internal-error");

    public static final FusionKey prize_error = FusionKey.key(namespace, "errors.prize-error");

    public static final FusionKey cannot_be_empty = FusionKey.key(namespace, "errors.cannot-be-empty");

    public static final FusionKey cannot_be_air = FusionKey.key(namespace, "errors.cannot-be-air");

    public static final FusionKey no_schematics_found = FusionKey.key(namespace, "errors.no-schematics-found");

    public static final FusionKey key_refund = FusionKey.key(namespace, "errors.key-refund");

    // misc
    public static final FusionKey no_teleporting = FusionKey.key(namespace, "misc.no-teleporting");
    public static final FusionKey no_commands_while_using_crate = FusionKey.key(namespace, "misc.no-commands");
    public static final FusionKey unknown_command = FusionKey.key(namespace, "misc.unknown-command");
    public static final FusionKey no_virtual_key = FusionKey.key(namespace, "misc.no-virtual-keys");
    public static final FusionKey no_keys = FusionKey.key(namespace, "misc.no-keys");
    public static final FusionKey correct_usage = FusionKey.key(namespace, "misc.correct-usage");
    public static final FusionKey feature_disabled = FusionKey.key(namespace, "misc.feature-disabled");
    public static final FusionKey lacking_flag = FusionKey.key(namespace, "misc.lacking-flag");

}