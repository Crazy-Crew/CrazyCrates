package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.enums.misc.Files;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import com.ryderbelserion.vital.paper.api.enums.Support;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.utils.MiscUtils;
import com.badbones69.crazycrates.utils.MsgUtils;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.regex.Matcher.quoteReplacement;

public class PrizeManager {
    
    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final BukkitUserManager userManager = plugin.getUserManager();

    public static int getCap(final Crate crate, final Player player) {
        final String format = "crazycrates.respin." + crate.getFileName() + ".";
        int cap = 0;

        for (final PermissionAttachmentInfo permission : player.getEffectivePermissions()) {
            String node = permission.getPermission();

            if (node.startsWith(format.toLowerCase())) {
                node = node.replace(format.toLowerCase(), "");

                int number = Integer.parseInt(node);

                if (number > cap && cap < crate.getCyclePermissionCap()) {
                    cap = number;
                }
            }
        }

        return cap;
    }

    public static boolean isCapped(final Crate crate, final Player player) {
        boolean isCapped = false;

        if (!crate.isCyclePermissionToggle() || crate.getCyclePermissionCap() < 1 || player.isOp()) {
            return false;
        }

        final int wins = userManager.getCrateRespin(player.getUniqueId(), crate.getFileName(), crate.isCyclePersistRestart());

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
     * Gets the prize for the player.
     *
     * @param player who the prize is for.
     * @param crate the player is opening.
     * @param prize the player is being given.
     */
    public static void givePrize(@NotNull final Player player, @Nullable Prize prize, @NotNull final Crate crate) {
        if (prize == null) {
            if (MiscUtils.isLogging()) plugin.getComponentLogger().warn("No prize was found when giving {} a prize.", player.getName());

            return;
        }

        prize = prize.hasPermission(player) ? prize.getAlternativePrize() : prize;

        if (!player.isOp()) {
            final int pulls = getCurrentPulls(prize, crate);

            if (pulls != -1 && pulls < prize.getMaxPulls()) {
                YamlConfiguration configuration = Files.data.getConfiguration();

                configuration.set("Prizes." + crate.getFileName()  + "." + prize.getSectionName() + ".Pulls", pulls + 1);

                // save to file!
                Files.data.save();
            }
        }

        for (ItemStack item : prize.getEditorItems()) {
            if (!MiscUtils.isInventoryFull(player)) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }

        if (!prize.getItemBuilders().isEmpty()) {
            final boolean isPlaceholderAPIEnabled = Support.placeholder_api.isEnabled();

            for (final ItemBuilder item : prize.getItemBuilders()) {
                if (isPlaceholderAPIEnabled) {
                    final String displayName = item.getDisplayName();

                    if (!displayName.isEmpty()) {
                        item.setDisplayName(PlaceholderAPI.setPlaceholders(player, displayName));
                    }

                    final List<String> displayLore = item.getDisplayLore();

                    if (!displayLore.isEmpty()) {
                        List<String> lore = new ArrayList<>();

                        displayLore.forEach(line -> lore.add(PlaceholderAPI.setPlaceholders(player, line)));

                        item.setDisplayLore(lore);
                    }
                }

                if (!MiscUtils.isInventoryFull(player)) {
                    MiscUtils.addItem(player, item.setPlayer(player).asItemStack());
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item.setPlayer(player).asItemStack());
                }
            }
        }

        for (final String command : crate.getPrizeCommands()) {
            runCommands(player, prize, crate, command);
        }

        for (final String command : prize.getCommands()) {
            runCommands(player, prize, crate, command);
        }

        prize.broadcast(player, crate);

        if (!crate.getPrizeMessage().isEmpty() && prize.getMessages().isEmpty()) {
            for (final String message : crate.getPrizeMessage()) {
                sendMessage(player, prize, crate, message);
            }

            return;
        }

        for (final String message : prize.getMessages()) {
            sendMessage(player, prize, crate, message);
        }
    }

    private static void runCommands(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, @NotNull String command) {
        String cmd = command;

        if (cmd.contains("%random%:")) {
            final StringBuilder commandBuilder = new StringBuilder();

            for (String word : cmd.split(" ")) {
                if (word.startsWith("%random%:")) {// /give %player% iron %random%:1-64
                    word = word.replace("%random%:", "");

                    try {
                        long min = Long.parseLong(word.split("-")[0]);
                        long max = Long.parseLong(word.split("-")[1]);

                        commandBuilder.append(MiscUtils.pickNumber(min, max)).append(" ");
                    } catch (Exception e) {
                        commandBuilder.append("1 ");

                        if (MiscUtils.isLogging()) {
                            plugin.getComponentLogger().warn("The prize {} in the {} crate has caused an error when trying to run a command.", prize.getPrizeName(), prize.getCrateName());
                            plugin.getComponentLogger().warn("Command: {}", cmd);
                        }
                    }
                } else {
                    commandBuilder.append(word).append(" ");
                }
            }

            cmd = commandBuilder.toString();
            cmd = cmd.substring(0, cmd.length() - 1);
        }

        if (Support.placeholder_api.isEnabled() ) cmd = PlaceholderAPI.setPlaceholders(player, cmd);

        final String maxPulls = String.valueOf(prize.getMaxPulls());
        final String pulls = String.valueOf(getCurrentPulls(prize, crate));
        final String prizeName = prize.getPrizeName().replaceAll("%maxpulls%", maxPulls).replaceAll("%pulls%", pulls);

        MiscUtils.sendCommand(cmd
                .replaceAll("%player%", quoteReplacement(player.getName()))
                .replaceAll("%reward%", quoteReplacement(prizeName))
                .replaceAll("%reward_stripped%", quoteReplacement(prize.getStrippedName()))
                .replaceAll("%crate_fancy%", quoteReplacement(crate.getCrateName()))
                .replaceAll("%crate%", quoteReplacement(crate.getFileName()))
                .replaceAll("%maxpulls%", maxPulls)
                .replaceAll("%pulls%", pulls));
    }

    private static void sendMessage(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, String message) {
        if (message.isEmpty()) return;

        final String maxPulls = String.valueOf(prize.getMaxPulls());
        final String pulls = String.valueOf(getCurrentPulls(prize, crate));
        final String prizeName = prize.getPrizeName().replaceAll("%maxpulls%", maxPulls).replaceAll("%pulls%", pulls);

        final String defaultMessage = message
                .replaceAll("%player%", quoteReplacement(player.getName()))
                .replaceAll("%reward%", quoteReplacement(prizeName))
                .replaceAll("%reward_stripped%", quoteReplacement(prize.getStrippedName()))
                .replaceAll("%crate%", quoteReplacement(crate.getCrateName()))
                .replaceAll("%maxpulls%", maxPulls)
                .replaceAll("%pulls%", pulls);

        MsgUtils.sendMessage(player, Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(player, defaultMessage) : defaultMessage, false);
    }

    public static int getCurrentPulls(final Prize prize, final Crate crate) {
        if (prize.getMaxPulls() == -1) return 0;

        final YamlConfiguration configuration = Files.data.getConfiguration();
        final ConfigurationSection section = configuration.getConfigurationSection("Prizes." + crate.getFileName()  + "." + prize.getSectionName());

        if (section == null) return 0;

        return section.getInt("Pulls", 0);
    }

    /**
     * Gets the prize for the player.
     *
     * @param player who the prize is for.
     * @param crate the player is opening.
     * @param prize the player is being given.
     */
    public static void givePrize(@NotNull final Player player, @NotNull final Crate crate, @Nullable final Prize prize) {
        if (prize != null) {
            givePrize(player, prize, crate);

            if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, prize));
        } else {
            Messages.prize_error.sendMessage(player, new HashMap<>() {{
                put("{crate}", crate.getCrateName());
            }});
        }
    }

    public static void getPrize(@NotNull final Crate crate, @NotNull final Inventory inventory, final int slot, @NotNull final Player player) {
        final ItemStack item = inventory.getItem(slot);

        if (item == null) return;

        final Prize prize = crate.getPrize(item);

        givePrize(player, prize, crate);
    }

    public static @Nullable Tier getTier(@NotNull final Crate crate) {
        if (crate.getTiers().isEmpty()) return null;

        final Random random = MiscUtils.useOtherRandom() ? ThreadLocalRandom.current() : new Random();

        double weight = 0.0;

        final List<Tier> tiers = crate.getTiers();

        for (final Tier tier : tiers) {
            weight += tier.getWeight();
        }

        int index = 0;

        for (double value = random.nextDouble() * weight; index < tiers.size() - 1; index++) {
            value -= tiers.get(index).getWeight();

            if (value < 0.0) break;
        }

        return tiers.get(index);
    }
}