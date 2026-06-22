package com.badbones69.crazycrates.paper.support.placeholders.types;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.support.placeholders.api.AbstractPlaceholder;
import com.ryderbelserion.fusion.core.api.enums.Level;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.items.PlayerBuilder;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;
import us.crazycrew.crazycrates.api.users.UserManager;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public final class NamePlaceholder implements AbstractPlaceholder {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final UserManager userManager = this.plugin.getUserManager();

    private final Player player;

    public NamePlaceholder(@NonNull final Player player) {
        this.player = player;
    }

    @Override
    public @NonNull Optional<String> get(@NonNull final String placeholder) {
        final String[] index = placeholder.split("_");

        if (index.length == 0) {
            return Optional.empty();
        }

        try {
            final String reference = placeholder.substring(0, placeholder.lastIndexOf("_"));

            final String[] splitter = reference.split("_");

            if (splitter.length == 0) {
                return Optional.empty();
            }

            final String name = this.player.getName();

            String playerName = splitter[0];

            final AtomicReference<UUID> atomic = new AtomicReference<>(this.player.getUniqueId());

            if (!playerName.equals(name)) {
                final PlayerBuilder playerBuilder = new PlayerBuilder(playerName);

                playerBuilder.getPlayer().ifPresentOrElse(target -> atomic.set(target.getUniqueId()), () -> playerBuilder.getOfflinePlayer().ifPresent(target -> atomic.set(target.getUniqueId())));
            } else {
                playerName = name;
            }

            final UUID uuid = atomic.get();

            if (uuid == null) {
                return Optional.empty();
            }

            if (placeholder.equalsIgnoreCase("%s_opened_raw".formatted(playerName))) {
                return Optional.of(String.valueOf(this.userManager.getTotalCratesOpened(uuid)));
            }

            if (placeholder.equalsIgnoreCase("%s_opened".formatted(playerName))) {
                return Optional.of(StringUtils.formatNumber(this.userManager.getTotalCratesOpened(uuid)));
            }

            if (splitter.length == 1) {
                return Optional.empty();
            }

            final String crateName = splitter[1];

            if (placeholder.equalsIgnoreCase("%s_%s_opened_raw".formatted(playerName, crateName))) {
                return Optional.of(String.valueOf(this.userManager.getCrateOpened(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_opened".formatted(playerName, crateName))) {
                return Optional.of(StringUtils.formatNumber(this.userManager.getCrateOpened(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_physical_raw".formatted(playerName, crateName))) {
                return Optional.of(String.valueOf(this.userManager.getPhysicalKeys(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_physical".formatted(playerName, crateName))) {
                return Optional.of(StringUtils.formatNumber(this.userManager.getPhysicalKeys(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_virtual_raw".formatted(playerName, crateName))) {
                return Optional.of(String.valueOf(this.userManager.getVirtualKeys(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_virtual".formatted(playerName, crateName))) {
                return Optional.of(StringUtils.formatNumber(this.userManager.getVirtualKeys(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_total_raw".formatted(playerName, crateName))) {
                return Optional.of(String.valueOf(this.userManager.getTotalKeys(uuid, crateName)));
            }

            if (placeholder.equalsIgnoreCase("%s_%s_total".formatted(playerName, crateName))) {
                return Optional.of(StringUtils.formatNumber(this.userManager.getTotalKeys(uuid, crateName)));
            }

            return Optional.empty();
        } catch (final Exception exception) {
            this.fusion.log(Level.WARNING, "Failed to parse %s placeholder", exception, "%crazycrates_" + placeholder);

            return Optional.empty();
        }
    }
}