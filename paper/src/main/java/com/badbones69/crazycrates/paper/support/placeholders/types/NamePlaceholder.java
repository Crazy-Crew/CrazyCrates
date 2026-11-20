package com.badbones69.crazycrates.paper.support.placeholders.types;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.support.placeholders.api.AbstractPlaceholder;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.items.PlayerBuilder;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class NamePlaceholder implements AbstractPlaceholder {

    // A suffix that ends with another suffix must come before it.
    private static final List<String> suffixes = List.of(
            "_virtual_raw",
            "_virtual",
            "_physical_raw",
            "_physical",
            "_total_raw",
            "_total",
            "_opened_raw",
            "_opened"
    );

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final CrateManager crateManager = this.platform.getCrateManager();

    private final UserManager userManager = this.platform.getUserManager();

    private final Player player;

    public NamePlaceholder(@NonNull final Player player) {
        this.player = player;
    }

    /**
     * Resolves {@code <player>_<crate><suffix>} and {@code <player>_opened[_raw]}, where the crate is a loaded crate's file name.
     *
     * @param placeholder the placeholder without the identifier
     * @return the value, or empty if nothing matches
     */
    @Override
    public @NonNull Optional<String> get(@NonNull final String placeholder) {
        try {
            String reference = placeholder;

            if (reference.regionMatches(true, 0, "{player}", 0, 8)) {
                reference = this.fusion.papi(this.player, "%player_name%") + reference.substring(8);
            }

            final String lowerCase = reference.toLowerCase(Locale.ROOT);

            for (final String suffix : suffixes) {
                if (!lowerCase.endsWith(suffix)) continue;

                final String target = reference.substring(0, reference.length() - suffix.length());

                // Shortest player name first, so the crate name takes every remaining underscore.
                for (int index = target.indexOf('_'); index > 0; index = target.indexOf('_', index + 1)) {
                    final Crate crate = this.crateManager.getCrateFromName(target.substring(index + 1));

                    if (crate == null) continue;

                    final UUID uuid = getUuid(target.substring(0, index));

                    if (uuid == null) continue;

                    final String fileName = crate.getFileName();

                    return Optional.of(switch (suffix) {
                        case "_virtual_raw" -> String.valueOf(this.userManager.getVirtualKeys(uuid, fileName));
                        case "_virtual" -> StringUtils.formatNumber(this.userManager.getVirtualKeys(uuid, fileName));
                        case "_physical_raw" -> String.valueOf(this.userManager.getPhysicalKeys(uuid, fileName));
                        case "_physical" -> StringUtils.formatNumber(this.userManager.getPhysicalKeys(uuid, fileName));
                        case "_total_raw" -> String.valueOf(this.userManager.getTotalKeys(uuid, fileName));
                        case "_total" -> StringUtils.formatNumber(this.userManager.getTotalKeys(uuid, fileName));
                        case "_opened_raw" -> String.valueOf(this.userManager.getCrateOpened(uuid, fileName));
                        default -> StringUtils.formatNumber(this.userManager.getCrateOpened(uuid, fileName));
                    });
                }

                if (target.isEmpty() || !suffix.startsWith("_opened")) continue;

                final UUID uuid = getUuid(target);

                if (uuid == null) continue;

                final int opened = this.userManager.getTotalCratesOpened(uuid);

                return Optional.of(suffix.equals("_opened_raw") ? String.valueOf(opened) : StringUtils.formatNumber(opened));
            }

            return Optional.empty();
        } catch (final Exception exception) {
            this.fusion.log(Level.WARNING, "Failed to parse %s placeholder", exception, "%crazycrates_" + placeholder);

            return Optional.empty();
        }
    }

    private @Nullable UUID getUuid(@NonNull final String playerName) {
        final AtomicReference<UUID> atomic = new AtomicReference<>(this.player.getUniqueId());

        if (!playerName.equals(this.player.getName())) {
            final PlayerBuilder playerBuilder = new PlayerBuilder(playerName);

            playerBuilder.getPlayer().ifPresentOrElse(target -> atomic.set(target.getUniqueId()), () -> playerBuilder.getOfflinePlayer().ifPresent(target -> atomic.set(target.getUniqueId())));
        }

        return atomic.get();
    }
}
