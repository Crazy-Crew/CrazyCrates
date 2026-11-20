package com.badbones69.crazycrates.paper.support.placeholders.types;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.support.placeholders.api.AbstractPlaceholder;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public final class SimplePlaceholder implements AbstractPlaceholder {

    // A suffix that ends with another suffix must come before it.
    private static final List<String> suffixes = List.of(
            "_virtual_raw",
            "_virtual",
            "_physical_raw",
            "_physical",
            "_total_raw",
            "_total",
            "_opened_raw",
            "_opened",
            "_raw"
    );

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();

    private final CrateManager crateManager = this.platform.getCrateManager();

    private final UserManager userManager = this.platform.getUserManager();

    private final UUID uuid;

    public SimplePlaceholder(@NonNull final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Resolves {@code <crate>} and {@code <crate><suffix>}, where the crate is a loaded crate's file name.
     *
     * @param placeholder the placeholder without the identifier
     * @return the value, or empty if no loaded crate matches
     */
    @Override
    public @NonNull Optional<String> get(@NonNull final String placeholder) {
        final Crate exact = this.crateManager.getCrateFromName(placeholder);

        if (exact != null) {
            return Optional.of(StringUtils.formatNumber(this.userManager.getVirtualKeys(this.uuid, exact.getFileName())));
        }

        final String lowerCase = placeholder.toLowerCase(Locale.ROOT);

        for (final String suffix : suffixes) {
            if (!lowerCase.endsWith(suffix)) continue;

            final Crate crate = this.crateManager.getCrateFromName(placeholder.substring(0, placeholder.length() - suffix.length()));

            if (crate == null) continue;

            final String fileName = crate.getFileName();

            return Optional.of(switch (suffix) {
                case "_virtual_raw", "_raw" -> String.valueOf(this.userManager.getVirtualKeys(this.uuid, fileName));
                case "_virtual" -> StringUtils.formatNumber(this.userManager.getVirtualKeys(this.uuid, fileName));
                case "_physical_raw" -> String.valueOf(this.userManager.getPhysicalKeys(this.uuid, fileName));
                case "_physical" -> StringUtils.formatNumber(this.userManager.getPhysicalKeys(this.uuid, fileName));
                case "_total_raw" -> String.valueOf(this.userManager.getTotalKeys(this.uuid, fileName));
                case "_total" -> StringUtils.formatNumber(this.userManager.getTotalKeys(this.uuid, fileName));
                case "_opened_raw" -> String.valueOf(this.userManager.getCrateOpened(this.uuid, fileName));
                default -> StringUtils.formatNumber(this.userManager.getCrateOpened(this.uuid, fileName));
            });
        }

        return Optional.empty();
    }
}
