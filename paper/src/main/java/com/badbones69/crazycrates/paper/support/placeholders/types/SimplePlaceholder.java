package com.badbones69.crazycrates.paper.support.placeholders.types;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrazyCratesPaper;
import com.badbones69.crazycrates.paper.support.placeholders.api.AbstractPlaceholder;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.Optional;
import java.util.UUID;

public final class SimplePlaceholder implements AbstractPlaceholder {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrazyCratesPaper platform = this.plugin.getPlatform();
    
    private final UserManager userManager = this.platform.getUserManager();

    private final UUID uuid;

    public SimplePlaceholder(@NonNull final UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public @NonNull Optional<String> get(@NonNull final String placeholder) {
        final String[] index = placeholder.split("_");

        if (index.length == 0) {
            return Optional.empty();
        }

        final String reference = index[0];
        
        if (placeholder.equalsIgnoreCase(reference)) {
            return Optional.of(StringUtils.formatNumber(this.userManager.getVirtualKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_virtual_raw".formatted(reference))) {
            return Optional.of(String.valueOf(this.userManager.getVirtualKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_virtual".formatted(reference))) {
            return Optional.of(StringUtils.formatNumber(this.userManager.getVirtualKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_physical_raw".formatted(reference))) {
            return Optional.of(String.valueOf(this.userManager.getPhysicalKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_physical".formatted(reference))) {
            return Optional.of(StringUtils.formatNumber(this.userManager.getPhysicalKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_total_raw".formatted(reference))) {
            return Optional.of(String.valueOf(this.userManager.getTotalKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_total".formatted(reference))) {
            return Optional.of(StringUtils.formatNumber(this.userManager.getTotalKeys(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_opened_raw".formatted(reference))) {
            return Optional.of(String.valueOf(this.userManager.getCrateOpened(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_opened".formatted(reference))) {
            return Optional.of(StringUtils.formatNumber(this.userManager.getCrateOpened(this.uuid, reference)));
        }

        if (placeholder.equalsIgnoreCase("%s_raw".formatted(reference))) {
            return Optional.of(String.valueOf(this.userManager.getVirtualKeys(this.uuid, reference)));
        }

        return Optional.empty();
    }
}