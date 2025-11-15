package com.badbones69.crazycrates.paper.support.placeholders;

import com.badbones69.crazycrates.paper.utils.MiscUtils;
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
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class PlaceholderAPISupport extends PlaceholderExpansion {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final Server server = this.plugin.getServer();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

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

        if (identifier.equalsIgnoreCase(value)) {
            values.add(this.instance.format(this.userManager.getVirtualKeys(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_physical_raw".formatted(value))) {
            values.add(String.valueOf(this.userManager.getPhysicalKeys(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_physical".formatted(value))) {
            values.add(this.instance.format(this.userManager.getPhysicalKeys(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_total_raw".formatted(value))) {
            values.add(String.valueOf(this.userManager.getTotalKeys(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_total".formatted(value))) {
            values.add(this.instance.format(this.userManager.getTotalKeys(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_opened_raw".formatted(value))) {
            values.add(String.valueOf(this.userManager.getCrateOpened(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_opened".formatted(value))) {
            values.add(this.instance.format(this.userManager.getCrateOpened(uuid.get(), value)));
        }

        if (identifier.equalsIgnoreCase("%s_raw".formatted(value))) {
            values.add(String.valueOf(this.userManager.getVirtualKeys(uuid.get(), value)));
        }

        if (!values.isEmpty()) { // basic placeholders checked!
            return values.getFirst();
        }

        final int index = identifier.lastIndexOf("_");

        this.fusion.log("warn", "Parse: {}", identifier.substring(0, index));

        final String playerName = PlaceholderAPI.setPlaceholders(player,"%" + StringUtils.substringBetween(identifier.substring(0, index), "{", "}") + "%");

        if (playerName.isBlank() || playerName.equalsIgnoreCase("%player_name%")) {
            this.fusion.log("warn", "The player name using {}_{} cannot be blank, or %player%", "crazycrates", identifier);

            return "N/A";
        }

        final PlayerBuilder builder = new PlayerBuilder(this.server, playerName);

        final Player target = builder.getPlayer();

        if (target == null) {
            final OfflinePlayer offlineTarget = builder.getOfflinePlayer();

            uuid.set(offlineTarget != null ? offlineTarget.getUniqueId() : null);
        } else {
            uuid.set(target.getUniqueId());
        }

        final UUID id = uuid.get();

        if (id == null) {
            this.fusion.log("warn", "The player name using %s_%s (%s) cannot be null".formatted("crazycrates", identifier, playerName));

            return "N/A";
        }

        final String crateName = keys[2];

        final Map<String, String> placeholders = new HashMap<>() {{
            put("{player_name}", playerName);
        }};

        final String parsed = MiscUtils.populatePlaceholders(null, identifier, placeholders);

        if (parsed.equalsIgnoreCase("%s_%s_opened_raw".formatted(playerName, crateName))) {
            values.add(String.valueOf(this.userManager.getCrateOpened(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_opened".formatted(playerName, crateName))) {
            values.add(this.instance.format(this.userManager.getCrateOpened(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_physical_raw".formatted(playerName, crateName))) {
            values.add(String.valueOf(this.userManager.getPhysicalKeys(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_physical".formatted(playerName, crateName))) {
            values.add(this.instance.format(this.userManager.getPhysicalKeys(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_virtual_raw".formatted(playerName, crateName))) {
            values.add(String.valueOf(this.userManager.getVirtualKeys(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_virtual".formatted(playerName, crateName))) {
            values.add(this.instance.format(this.userManager.getVirtualKeys(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_total_raw".formatted(playerName, crateName))) {
            values.add(String.valueOf(this.userManager.getTotalKeys(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_%s_total".formatted(playerName, crateName))) {
            values.add(this.instance.format(this.userManager.getTotalKeys(id, crateName)));
        }

        if (parsed.equalsIgnoreCase("%s_opened_raw".formatted(playerName))) {
            values.add(String.valueOf(this.userManager.getTotalCratesOpened(id)));
        }

        if (parsed.equalsIgnoreCase("%s_opened".formatted(playerName))) {
            values.add(this.instance.format(this.userManager.getTotalCratesOpened(id)));
        }

        return values.isEmpty() ? "N/A" : values.getFirst();
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