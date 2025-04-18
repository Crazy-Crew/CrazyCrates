package com.badbones69.crazycrates.paper.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.badbones69.crazycrates.paper.managers.events.EventManager;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.ArgName;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import org.jetbrains.annotations.Nullable;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Logger;

public class CommandOpen extends BaseCommand {

    private boolean isCancelled(final Player player, final String crateName) {
        if (crateName == null || crateName.isBlank()) {
            Messages.cannot_be_empty.sendMessage(player, "{value}", "crate name");

            return true;
        }

        // Check if player is in opening list first.
        if (this.crateManager.isInOpeningList(player)) {
            Messages.already_opening_crate.sendMessage(player, "{crate}", crateName);

            return true;
        }

        if (MiscUtils.isInventoryFull(player)) {
            Messages.inventory_not_empty.sendMessage(player, "{crate}", crateName);

            return true;
        }

        return false;
    }

    @Command("open")
    @Permission(value = "crazycrates.open", def = PermissionDefault.OP)
    public void open(Player player, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("key_type") @Suggestion("keys") String type) {
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null || crate.getCrateType() == CrateType.menu) {
            Messages.internal_error.sendMessage(player);

            if (MiscUtils.isLogging()) this.logger.error("An error has occurred: The crate type is null or Menu for the crate named {}", crateName);

            return;
        }

        final String fancyName = crate.getCrateName();
        final String fileName = crate.getFileName();

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crateType.getName());
            placeholders.put("{crate}", fancyName);

            Messages.cant_be_a_virtual_crate.sendMessage(player, placeholders);

            return;
        }

        KeyType keyType = getKeyType(type);

        boolean hasKey = this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && keyType == KeyType.physical_key ? this.userManager.getTotalKeys(player.getUniqueId(), fileName) >= 1 : this.userManager.getVirtualKeys(player.getUniqueId(), fileName) >= 1;

        // If no key, run this.
        if (!hasKey) {
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                Sound sound = Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.MASTER, 1f, 1f);

                player.playSound(sound);
            }

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{crate}", fancyName);
            placeholders.put("{key}", crate.getKeyName());

            Messages.no_keys.sendMessage(player, placeholders);

            return;
        }

        // They passed the check.
        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false, EventType.event_crate_opened);
    }

    @Command("open-others")
    @Permission(value = "crazycrates.open-others", def = PermissionDefault.OP)
    public void others(CommandSender sender, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("player") @Suggestion("players") Player player, @ArgName("key_type") @Suggestion("keys") String type) {
        // If the command is cancelled.
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null) {
            Messages.internal_error.sendMessage(sender);

            if (MiscUtils.isLogging()) this.logger.error("An error has occurred: The crate type is null for the crate named {}", crateName);

            return;
        }

        final String fancyName = crate.getCrateName();
        final String fileName = crate.getFileName();

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crateType.getName());
            placeholders.put("{crate}", fancyName);

            Messages.cant_be_a_virtual_crate.sendMessage(sender, placeholders);

            return;
        }

        KeyType keyType = getKeyType(type);

        if (sender == player) {
            open(player, crateName, type);

            return;
        }

        boolean hasKey = this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && keyType == KeyType.physical_key ? this.userManager.getTotalKeys(player.getUniqueId(), fileName) >= 1 : this.userManager.getVirtualKeys(player.getUniqueId(), fileName) >= 1;

        if (!hasKey) {
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                Sound sound = Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.MASTER, 1f, 1f);

                player.playSound(sound);
            }

            Messages.no_keys.sendMessage(sender, new HashMap<>() {{
                put("{crate}", fancyName);
                put("{key}", crate.getKeyName());
            }});

            return;
        }

        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false, EventType.event_crate_opened);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", fancyName);
        placeholders.put("{player}", player.getName());

        Messages.opened_a_crate.sendMessage(sender, placeholders);
    }


    @Command("forceopen")
    @Permission(value = "crazycrates.forceopen", def = PermissionDefault.OP)
    public void forceopen(CommandSender sender, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("player") @Suggestion("players") Player player) {
        // If the command is cancelled.
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        final Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            Messages.not_a_crate.sendMessage(sender, "{crate}", crateName);

            return;
        }

        final CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null) {
            Messages.internal_error.sendMessage(sender);

            if (MiscUtils.isLogging()) this.logger.error("An error has occurred: The crate type is null for the crate named {}", crateName);

            return;
        }

        final String fancyName = crate.getCrateName();

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crateType.getName());
            placeholders.put("{crate}", fancyName);

            Messages.cant_be_a_virtual_crate.sendMessage(sender, placeholders);

            return;
        }

        this.crateManager.openCrate(player, crate, KeyType.free_key, player.getLocation(), true, false, EventType.event_crate_force_opened);

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", fancyName);
        placeholders.put("{player}", player.getName());

        Messages.opened_a_crate.sendMessage(sender, placeholders);
    }

    @Command("mass-open")
    @Permission(value = "crazycrates.massopen", def = PermissionDefault.OP)
    public void mass(Player player, @ArgName("crate") @Suggestion("crates") String crateName, @ArgName("key_type") @Suggestion("keys") String type, @ArgName("amount") @Suggestion("numbers") int amount) {
        // If the command is cancelled.
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        final Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            Messages.not_a_crate.sendMessage(player, "{crate}", crateName);

            return;
        }

        final CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null) {
            Messages.internal_error.sendMessage(player);

            if (MiscUtils.isLogging()) this.logger.error("An error has occurred: The crate type is null for the crate named {}", crate.getFileName());

            return;
        }

        final String fancyName = crate.getCrateName();
        final String fileName = crate.getFileName();
        final String keyName = crate.getKeyName();

        final KeyType keyType = getKeyType(type);

        int keys = keyType == KeyType.physical_key ? this.userManager.getPhysicalKeys(player.getUniqueId(), fileName) : this.userManager.getVirtualKeys(player.getUniqueId(), fileName);
        int used = 0;

        if (keys == 0) {
            Messages.no_keys.sendMessage(player, new HashMap<>() {{
                put("{crate}", fancyName);
                put("{key}", keyName);
            }});

            return;
        }

        final int requiredKeys = crate.getRequiredKeys();

        if (crate.useRequiredKeys() && keys < requiredKeys) {
            final int finalKeys = keys;
            
            Messages.not_enough_keys.sendMessage(player, new HashMap<>() {{
                put("{required_amount}", String.valueOf(requiredKeys));
                put("{key_amount}", String.valueOf(requiredKeys)); // deprecated, remove in next major version of minecraft.
                put("{amount}", String.valueOf(finalKeys));
                put("{crate}", fancyName);
                put("{key}", keyName);
            }});

            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        for (;keys > 0; keys--) {
            if (MiscUtils.isInventoryFull(player)) break;
            if (used >= amount) break;
            if (used >= crate.getMaxMassOpen()) break;

            switch (crateType) {
                case casino -> {
                    final ConfigurationSection section = crate.getFile().getConfigurationSection("Crate.random");

                    if (section != null) {
                        final boolean isRandom = section.getBoolean("toggle", false);

                        if (isRandom) {
                            final List<Tier> tiers = crate.getTiers();

                            final int size = tiers.size();

                            final ThreadLocalRandom random = ThreadLocalRandom.current();

                            final Tier tier = tiers.get(random.nextInt(size));

                            PrizeManager.givePrize(player, crate, crate.pickPrize(player, tier));
                            PrizeManager.givePrize(player, crate, crate.pickPrize(player, tier));
                            PrizeManager.givePrize(player, crate, crate.pickPrize(player, tier));
                        } else {
                            @Nullable final Tier row_uno = crate.getTier(section.getString("types.row-1", ""));
                            @Nullable final Tier row_dos = crate.getTier(section.getString("types.row-2", ""));
                            @Nullable final Tier row_tres = crate.getTier(section.getString("types.row-3", ""));

                            if (row_uno == null || row_dos == null || row_tres == null) {
                                if (MiscUtils.isLogging()) {
                                    List.of(
                                            "One of your rows has a tier that doesn't exist supplied in " + fileName,
                                            "You can find this in your crate config, search for row-1, row-2, and row-3"
                                    ).forEach(this.logger::warn);
                                }

                                used--;

                                break;
                            }

                            PrizeManager.givePrize(player, crate, crate.pickPrize(player, row_uno));
                            PrizeManager.givePrize(player, crate, crate.pickPrize(player, row_dos));
                            PrizeManager.givePrize(player, crate, crate.pickPrize(player, row_tres));
                        }
                    }
                }

                case cosmic -> {
                    final List<Tier> tiers = crate.getTiers();

                    if (tiers.isEmpty()) {
                        used--;

                        break;
                    }

                    final int size = tiers.size();

                    final ThreadLocalRandom random = ThreadLocalRandom.current();

                    final CosmicCrateManager cosmicCrateManager = (CosmicCrateManager) crate.getManager();

                    final int totalPrizes = cosmicCrateManager.getTotalPrizes();

                    for (int i = 0; i < totalPrizes; i++) {
                        final Tier tier = tiers.get(random.nextInt(size));

                        final Prize prize = crate.pickPrize(player, tier);

                        PrizeManager.givePrize(player, crate, prize);
                    }
                }

                case quad_crate -> {
                    for (int i = 0; i < 4; i++) {
                        PrizeManager.givePrize(player, crate, crate.pickPrize(player));
                    }
                }

                default -> {
                    final Prize prize = crate.pickPrize(player);

                    PrizeManager.givePrize(player, crate, prize);
                }
            }

            used++;
        }

        //if (crateType != CrateType.cosmic) this.userManager.addOpenedCrate(player.getUniqueId(), fileName, used); //todo() hmm?, since we account for cosmic crate, it should be fine to let it count as opened?
        this.userManager.addOpenedCrate(player.getUniqueId(), fileName, used);

        EventManager.logEvent(EventType.event_crate_opened, player.getName(), player, crate, keyType, used);
        EventManager.logEvent(EventType.event_key_taken, player.getName(), player, crate, keyType, used);

        if (!this.userManager.takeKeys(player.getUniqueId(), fileName, keyType, used, false)) {
            this.crateManager.removeCrateInUse(player);
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        this.crateManager.removePlayerFromOpeningList(player);
    }
}