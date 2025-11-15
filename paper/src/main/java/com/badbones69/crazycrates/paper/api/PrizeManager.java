package com.badbones69.crazycrates.paper.api;

import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Tier;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.utils.CommandUtils;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.PluginManager;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.utils.MsgUtils;
import org.jetbrains.annotations.Nullable;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class PrizeManager {
    
    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final FusionPaper fusion = plugin.getFusion();
    private static final Server server = plugin.getServer();
    private static final PluginManager pluginManager = server.getPluginManager();
    private static final BukkitUserManager userManager = plugin.getUserManager();

    public static int getCap(@NotNull final Crate crate, @NotNull final Player player) {
        final String format = "crazycrates.respin." + crate.getFileName() + ".";
        final String lowerCase = format.toLowerCase();

        int cap = 0;

        for (final PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String node = permission.getPermission();

            if (node.startsWith(lowerCase)) {
                node = node.replace(lowerCase, "");

                int number = Integer.parseInt(node);

                if (number > cap && cap < crate.getCyclePermissionCap()) {
                    cap = number;
                }
            }
        }

        return cap;
    }

    public static boolean isCapped(@NotNull final Crate crate, @NotNull final Player player) {
        boolean isCapped = false;

        if (!crate.isCyclePermissionToggle() || crate.getCyclePermissionCap() < 1 || player.isOp()) {
            return false;
        }

        final int wins = userManager.getCrateRespin(player.getUniqueId(), crate.getFileName());

        int cap = getCap(crate, player);

        final String format = "crazycrates.respin." + crate.getFileName() + ".";
        final String node = format + cap;

        if (player.hasPermission(node)) {
            if (wins >= cap) {
                isCapped = true;
            }
        } else {
            isCapped = true;
        }

        return isCapped;
    }

    /**
     * Gets the prize for the player with an offset location.
     *
     * @param player who the prize is for
     * @param crate the player is opening
     * @param prize the player is being given
     *
     * @deprecated
     */
    @Deprecated(forRemoval = true, since = "4.2.1")
    public static void givePrize(@NotNull final Player player, @Nullable final Prize prize, @NotNull final Crate crate) {
        givePrize(player, crate, prize);
    }

    /**
     * Gets the prize for the player with an offset location.
     *
     * @param player who the prize is for
     * @param crate the player is opening
     * @param prize the player is being given
     */
    public static void givePrize(@NotNull final Player player, @NotNull final Crate crate, @Nullable final Prize prize) {
        if (prize != null) {
            givePrize(player, player.getLocation().clone().add(0, 1, 0), crate, prize);
        } else {
            Messages.prize_error.sendMessage(player, Map.of(
                    "{crate}", crate.getCrateName(),
                    "{player}", player.getName()
            ));
        }
    }

    /**
     * Gets the prize for the player.
     *
     * @param player who the prize is for
     * @param location the location
     * @param crate the player is opening
     * @param prize the player is being given
     */
    public static void givePrize(@NotNull final Player player, @NotNull final Location location, @NotNull final Crate crate, @Nullable Prize prize) {
        if (prize == null) {
            fusion.log("warn", "No prize was found when giving {} a prize.", player.getName());

            return;
        }

        pluginManager.callEvent(new PlayerPrizeEvent(player, crate, prize));

        if (prize.useFireworks()) {
            MiscUtils.spawnFirework(location, null);
        }

        prize = prize.hasPermission(player) ? prize.getAlternativePrize() : prize;

        if (!player.isOp()) {
            final int pulls = getCurrentPulls(prize, crate);

            if (pulls != -1 && pulls < prize.getMaxPulls()) {
                YamlConfiguration configuration = FileKeys.data.getConfiguration();

                configuration.set("Prizes." + crate.getFileName()  + "." + prize.getSectionName() + ".Pulls", pulls + 1);

                // save to file!
                FileKeys.data.save();
            }
        }

        MiscUtils.dropItems(prize.getEditorItems(), player); // drops any leftover editor items.

        MiscUtils.dropBuilders(prize.getItems(), player);

        for (final String command : crate.getPrizeCommands()) {
            runCommands(player, prize, crate, command);
        }

        for (final String command : prize.getCommands()) {
            runCommands(player, prize, crate, command);
        }

        prize.broadcast(player, crate);

        final List<String> cratePrizeMessages = crate.getPrizeMessage();
        final List<String> prizeMessages = prize.getMessages();

        if (!cratePrizeMessages.isEmpty() && prizeMessages.isEmpty()) {
            for (final String message : cratePrizeMessages) {
                sendMessage(player, prize, crate, message);
            }

            return;
        }

        for (final String message : prizeMessages) {
            sendMessage(player, prize, crate, message);
        }
    }

    private static void runCommands(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, @NotNull final String command) {
        final String value = MsgUtils.getRandomNumber(command);

        final String maxPulls = String.valueOf(prize.getMaxPulls());
        final String pulls = String.valueOf(getCurrentPulls(prize, crate));
        final String playerName = player.getName();
        final String fancyName = crate.getCrateName();
        final String crateName = crate.getFileName();

        final String chance = StringUtils.format(crate.getChance(prize.getWeight()));
        final String weight = String.valueOf(prize.getWeight());

        final Map<String, String> placeholders = new HashMap<>() {{
            put("%player%", playerName);

            put("%crate_fancy%", fancyName);
            put("%crate%", crateName);

            put("%maxpulls%", maxPulls);
            put("%pulls%", pulls);

            put("%chance%", chance);
            put("%weight%", weight);

            put("{player}", playerName);

            put("{crate_fancy}", fancyName);
            put("{crate}", crateName);

            put("{maxpulls}", maxPulls);
            put("{pulls}", pulls);

            put("{chance}", chance);
            put("{weight}", weight);
        }};

        final String prizeName = fusion.replacePlaceholder(prize.getPrizeName(), placeholders);

        final String strippedName = prize.getStrippedName();

        placeholders.put("%reward_stripped%", strippedName);
        placeholders.put("%reward%", prizeName);

        placeholders.put("{reward_stripped}", strippedName);
        placeholders.put("{reward}", prizeName);

        CommandUtils.executeCommand(player, value, placeholders);
    }

    private static void sendMessage(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, @NotNull final String message) {
        if (message.isEmpty()) return;

        final String maxPulls = String.valueOf(prize.getMaxPulls());
        final String pulls = String.valueOf(getCurrentPulls(prize, crate));
        final String playerName = player.getName();
        final String fancyName = crate.getCrateName();
        final String crateName = crate.getFileName();

        final String chance = StringUtils.format(crate.getChance(prize.getWeight()));
        final String weight = String.valueOf(prize.getWeight());

        final Map<String, String> placeholders = new HashMap<>() {{
            put("%player%", playerName);

            put("%crate_fancy%", fancyName);
            put("%crate%", crateName);

            put("%maxpulls%", maxPulls);
            put("%pulls%", pulls);

            put("%chance%", chance);
            put("%weight%", weight);

            put("{player}", playerName);

            put("{crate_fancy}", fancyName);
            put("{crate}", crateName);

            put("{maxpulls}", maxPulls);
            put("{pulls}", pulls);

            put("{chance}", chance);
            put("{weight}", weight);
        }};

        final String prizeName = fusion.replacePlaceholder(prize.getPrizeName(), placeholders);

        final String strippedName = prize.getStrippedName();

        placeholders.put("%reward_stripped%", strippedName);
        placeholders.put("%reward%", prizeName);

        placeholders.put("{reward_stripped}", strippedName);
        placeholders.put("{reward}", prizeName);

        player.sendMessage(fusion.parse(player, message, placeholders));
    }

    public static int getCurrentPulls(final Prize prize, final Crate crate) {
        if (prize.getMaxPulls() == -1) return 0;

        final YamlConfiguration configuration = FileKeys.data.getConfiguration();
        final ConfigurationSection section = configuration.getConfigurationSection("Prizes." + crate.getFileName()  + "." + prize.getSectionName());

        if (section == null) return 0;

        return section.getInt("Pulls", 0);
    }

    public static void getPrize(@NotNull final Crate crate, @NotNull final Inventory inventory, final int slot, @NotNull final Player player) {
        final ItemStack item = inventory.getItem(slot);

        if (item == null) return;

        givePrize(player, player.getLocation().clone().add(0, 1, 0), crate, crate.getPrize(item));
    }

    public static @Nullable Tier getTier(@NotNull final Crate crate) {
        final List<Tier> tiers = crate.getTiers();

        if (tiers.isEmpty()) return null;

        final Random random = MiscUtils.getRandom();

        double weight = 0.0;

        for (final Tier tier : tiers) {
            weight += tier.getWeight();
        }

        int index = 0;

        for (double value = random.nextDouble() * weight; index < tiers.size() - 1; index++) {
            value -= tiers.get(index).getWeight();

            if (value <= 0.0) break;
        }

        return tiers.get(index);
    }
}