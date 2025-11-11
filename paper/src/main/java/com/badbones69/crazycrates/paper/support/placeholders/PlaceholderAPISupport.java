package com.badbones69.crazycrates.paper.support.placeholders;

import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.api.builders.PlayerBuilder;
import me.clip.placeholderapi.PlaceholderAPI;
import com.badbones69.crazycrates.paper.CrazyCrates;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Server server = this.plugin.getServer();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final NumberFormat instance = NumberFormat.getNumberInstance();

    @Override
    public @NotNull final String onPlaceholderRequest(final Player player, @NotNull final String identifier) {
        if (player == null || identifier.isEmpty()) return "N/A";

        final AtomicReference<UUID> uuid = new AtomicReference<>(player.getUniqueId()); // re-used later

        if (identifier.equalsIgnoreCase("crates_opened_raw")) {
            return String.valueOf(this.userManager.getTotalCratesOpened(uuid.get()));
        }

        if (identifier.equalsIgnoreCase("crates_opened")) {
            return this.instance.format(this.userManager.getTotalCratesOpened(uuid.get()));
        }

        final List<String> values = new ArrayList<>(1);

        final String[] keys = identifier.split("_");

        final String value = keys[0];

        this.crateManager.getCrateByName(value).ifPresentOrElse(crate -> {
            final String name = crate.getFileName();

            if (identifier.equalsIgnoreCase(name)) {
                values.add(this.instance.format(this.userManager.getVirtualKeys(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_physical_raw".formatted(name))) {
                values.add(String.valueOf(this.userManager.getPhysicalKeys(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_physical".formatted(name))) {
                values.add(this.instance.format(this.userManager.getPhysicalKeys(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_total_raw".formatted(name))) {
                values.add(String.valueOf(this.userManager.getTotalKeys(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_total".formatted(name))) {
                values.add(this.instance.format(this.userManager.getTotalKeys(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_opened_raw".formatted(name))) {
                values.add(String.valueOf(this.userManager.getCrateOpened(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_opened".formatted(name))) {
                values.add(this.instance.format(this.userManager.getCrateOpened(uuid.get(), name)));
            }

            if (identifier.equalsIgnoreCase("%s_raw".formatted(name))) {
                values.add(String.valueOf(this.userManager.getVirtualKeys(uuid.get(), name)));
            }
        }, () -> this.fusion.log("warn", "{} is not a valid crate name! Placeholder: {}", value, identifier));

        if (!values.isEmpty()) { // basic placeholders checked!
            return values.getFirst();
        }

        final String playerName = "%" + StringUtils.substringBetween(keys[0], "{", "}") + "%";

        String parsedPlayer = player.getName();

        if (playerName.equalsIgnoreCase("%player%")) {
            parsedPlayer = PlaceholderAPI.setPlaceholders(player, playerName);
        }

        UUID id;

        if (!parsedPlayer.equalsIgnoreCase(player.getName())) {
            final PlayerBuilder builder = new PlayerBuilder(this.server, parsedPlayer);

            final Player target = builder.getPlayer();

            if (target == null) {
                final OfflinePlayer offlineTarget = builder.getOfflinePlayer();

                id = offlineTarget != null ? offlineTarget.getUniqueId() : null;
            } else {
                id = target.getUniqueId();
            }
        } else {
            id = player.getUniqueId();
        }

        if (id == null) {
            return "N/A";
        }

        final String crateName = keys[1];

        this.crateManager.getCrateByName(crateName).ifPresent(crate -> {
            if (identifier.equalsIgnoreCase("%s_%s_opened_raw".formatted(playerName, crateName))) {
                values.add(String.valueOf(this.userManager.getCrateOpened(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_opened".formatted(playerName, crateName))) {
                values.add(this.instance.format(this.userManager.getCrateOpened(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_physical_raw".formatted(playerName, crateName))) {
                values.add(String.valueOf(this.userManager.getPhysicalKeys(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_physical".formatted(playerName, crateName))) {
                values.add(this.instance.format(this.userManager.getPhysicalKeys(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_virtual_raw".formatted(playerName, crateName))) {
                values.add(String.valueOf(this.userManager.getVirtualKeys(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_virtual".formatted(playerName, crateName))) {
                values.add(this.instance.format(this.userManager.getVirtualKeys(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_total_raw".formatted(playerName, crateName))) {
                values.add(String.valueOf(this.userManager.getTotalKeys(id, crateName)));
            }

            if (identifier.equalsIgnoreCase("%s_%s_total".formatted(playerName, crateName))) {
                values.add(this.instance.format(this.userManager.getTotalKeys(id, crateName)));
            }
        });

        if (!values.isEmpty()) {
            return values.getFirst();
        }

        if (identifier.endsWith("opened")) {
            return this.instance.format(this.userManager.getTotalCratesOpened(id));
        }

        if (identifier.endsWith("opened_raw")) {
            return String.valueOf(this.userManager.getTotalCratesOpened(id));
        }

        return "N/A";
    }
    
    @Override
    public final boolean persist() {
        return true;
    }

    @Override
    public final boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull final String getIdentifier() {
        return this.plugin.getName().toLowerCase();
    }
    
    @Override
    public @NotNull final String getAuthor() {
        return "ryderbelserion";
    }
    
    @Override
    public @NotNull final String getVersion() {
        return this.plugin.getPluginMeta().getVersion();
    }
}