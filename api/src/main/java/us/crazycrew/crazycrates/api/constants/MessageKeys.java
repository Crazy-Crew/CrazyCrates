package us.crazycrew.crazycrates.api.constants;

import com.ryderbelserion.fusion.core.api.FusionKey;
import static us.crazycrew.crazycrates.api.CrazyCrates.namespace;

public class MessageKeys {

    public static final FusionKey default_locale = FusionKey.key(namespace, "default");

    public static final FusionKey no_prizes_found = FusionKey.key(namespace, "no-prizes-found");

    public static final FusionKey prize_not_found = FusionKey.key(namespace, "prize-not-found");

    public static final FusionKey internal_error = FusionKey.key(namespace, "internal-error");

    public static final FusionKey prize_error = FusionKey.key(namespace, "prize-error");

    public static final FusionKey cannot_be_empty = FusionKey.key(namespace, "cannot-be-empty");

    public static final FusionKey cannot_be_air = FusionKey.key(namespace, "cannot-be-air");

    public static final FusionKey no_schematics_found = FusionKey.key(namespace, "no-schematics-found");

    public static final FusionKey key_refund = FusionKey.key(namespace, "key-refund");

}