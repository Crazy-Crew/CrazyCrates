package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.api.objects.Tier;
import com.ryderbelserion.vital.enums.Support;
import com.ryderbelserion.vital.util.builders.items.ItemBuilder;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import org.jetbrains.annotations.Nullable;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import static java.util.regex.Matcher.quoteReplacement;

public class PrizeManager {
    
    private static @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    /**
     * Gets the prize for the player.
     *
     * @param player who the prize is for.
     * @param crate the player is opening.
     * @param prize the player is being given.
     */
    public static void givePrize(@NotNull final Player player, @Nullable Prize prize, @NotNull final Crate crate) {
        if (prize == null) {
            if (MiscUtils.isLogging()) plugin.getLogger().warning("No prize was found when giving " + player.getName() + " a prize.");

            return;
        }

        prize = prize.hasPermission(player) ? prize.getAlternativePrize() : prize;

        if (!prize.getItemBuilders().isEmpty()) {
            for (final ItemBuilder item : prize.getItemBuilders()) {
                if (!MiscUtils.isInventoryFull(player)) {
                    player.getInventory().addItem(item.setPlayer(player).getStack());
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), item.setPlayer(player).getStack());
                }
            }
        } else {
            // Only give them the display item as a reward if prize commands are empty.
            if (prize.getCommands().isEmpty()) {
                if (!MiscUtils.isInventoryFull(player)) {
                    player.getInventory().addItem(prize.getPrizeItem().setPlayer(player).getStack());
                } else {
                    player.getWorld().dropItemNaturally(player.getLocation(), prize.getPrizeItem().setPlayer(player).getStack());
                }
            }
        }

        for (final String command : crate.getPrizeCommands()) {
            runCommands(player, prize, crate, command);
        }

        for (final String command : prize.getCommands()) {
            runCommands(player, prize, crate, command);
        }

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

                        plugin.getLogger().warning("The prize " + prize.getPrizeName() + " in the " + prize.getCrateName() + " crate has caused an error when trying to run a command.");
                        plugin.getLogger().warning("Command: " + cmd);
                    }
                } else {
                    commandBuilder.append(word).append(" ");
                }
            }

            cmd = commandBuilder.toString();
            cmd = cmd.substring(0, cmd.length() - 1);
        }

        if (Support.placeholder_api.isEnabled() ) cmd = PlaceholderAPI.setPlaceholders(player, cmd);

        final ItemBuilder builder = prize.getPrizeItem();

        final String display = builder.getStrippedName();

        MiscUtils.sendCommand(cmd
                .replaceAll("%player%", quoteReplacement(player.getName()))
                .replaceAll("%reward%", quoteReplacement(builder.getDisplayName()))
                .replaceAll("%reward_stripped%", quoteReplacement(display))
                .replaceAll("%crate%", quoteReplacement(crate.getCrateInventoryName())));
    }

    private static void sendMessage(@NotNull final Player player, @NotNull final Prize prize, @NotNull final Crate crate, String message) {
        if (message.isEmpty()) return;

        final ItemBuilder builder = prize.getPrizeItem();

        final String display = builder.getStrippedName();

        final String defaultMessage = message
                .replaceAll("%player%", quoteReplacement(player.getName()))
                .replaceAll("%reward%", quoteReplacement(builder.getDisplayName()))
                .replaceAll("%reward_stripped%", quoteReplacement(display))
                .replaceAll("%crate%", quoteReplacement(crate.getCrateInventoryName()));

        MsgUtils.sendMessage(player, Support.placeholder_api.isEnabled()  ? PlaceholderAPI.setPlaceholders(player, defaultMessage) : defaultMessage, false);
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

            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendRichMessage(MsgUtils.getPrefix("<red>No prize was found, please report this issue if you think this is an error."));
        }
    }

    public static void getPrize(@NotNull final Crate crate, @NotNull final Inventory inventory, final int slot, @NotNull final Player player) {
        final ItemStack item = inventory.getItem(slot);

        if (item == null) return;

        final Prize prize = crate.getPrize(item);

        givePrize(player, prize, crate);
    }

    public static @Nullable Tier getTier(@NotNull final Crate crate) {
        if (!crate.getTiers().isEmpty()) {
            for (int stopLoop = 0; stopLoop <= 100; stopLoop++) {
                for (final Tier tier : crate.getTiers()) {
                    final int chance = tier.getChance();

                    final int num = MiscUtils.useOtherRandom() ? ThreadLocalRandom.current().nextInt(tier.getMaxRange()) : new Random().nextInt(tier.getMaxRange());

                    if (num >= 1 && num <= chance) {
                        return tier;
                    }
                }
            }
        }

        return null;
    }
}