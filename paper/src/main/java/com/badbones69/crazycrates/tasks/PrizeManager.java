package com.badbones69.crazycrates.tasks;

import com.badbones69.crazycrates.api.objects.Tier;
import org.apache.commons.lang.WordUtils;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.ItemBuilder;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.support.libraries.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.util.HashMap;
import static java.util.regex.Matcher.quoteReplacement;

public class PrizeManager {
    
    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    /**
     * Give a player a prize they have won.
     *
     * @param player you wish to give the prize to.
     * @param prize the player has won.
     */
    public void givePrize(Player player, Prize prize, Crate crate) {
        if (prize == null) {
            if (this.plugin.isLogging()) this.plugin.getLogger().warning("No prize was found when giving " + player.getName() + " a prize.");
            return;
        }

        prize = prize.hasBlacklistPermission(player) ? prize.getAltPrize() : prize;

        for (ItemStack item : prize.getItems()) {
            if (item == null) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%crate%", prize.getCrate());
                placeholders.put("%prize%", prize.getName());
                player.sendMessage(Messages.prize_error.getMessage(placeholders).toString());
                continue;
            }

            if (!MiscUtils.isInventoryFull(player)) {
                player.getInventory().addItem(item);
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
            }
        }

        for (ItemBuilder item : prize.getItemBuilders()) {
            ItemBuilder clone = new ItemBuilder(item);

            if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                clone.setName(PlaceholderAPI.setPlaceholders(player, clone.getName()));
                clone.setLore(PlaceholderAPI.setPlaceholders(player, clone.getLore()));
            }

            if (!MiscUtils.isInventoryFull(player)) {
                player.getInventory().addItem(clone.build());
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), clone.build());
            }
        }

        for (String command : prize.getCommands()) { // /give %player% iron %random%:1-64
            if (command.contains("%random%:")) {
                String cmd = command;
                StringBuilder commandBuilder = new StringBuilder();

                for (String word : cmd.split(" ")) {
                    if (word.startsWith("%random%:")) {
                        word = word.replace("%random%:", "");

                        try {
                            long min = Long.parseLong(word.split("-")[0]);
                            long max = Long.parseLong(word.split("-")[1]);
                            commandBuilder.append(MiscUtils.pickNumber(min, max)).append(" ");
                        } catch (Exception e) {
                            commandBuilder.append("1 ");
                            this.plugin.getLogger().warning("The prize " + prize.getName() + " in the " + prize.getCrate() + " crate has caused an error when trying to run a command.");
                            this.plugin.getLogger().warning("Command: " + cmd);
                        }
                    } else {
                        commandBuilder.append(word).append(" ");
                    }
                }

                command = commandBuilder.toString();
                command = command.substring(0, command.length() - 1);
            }

            if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) command = PlaceholderAPI.setPlaceholders(player, command);

            String name = prize.getDisplayItemBuilder().getName() == null || prize.getDisplayItemBuilder().getName().isEmpty() ? MsgUtils.color(WordUtils.capitalizeFully(prize.getDisplayItemBuilder().getMaterial().getKey().getKey().replaceAll("_", " "))) : prize.getDisplayItemBuilder().getName();

            MiscUtils.sendCommand(command
                    .replaceAll("%player%", quoteReplacement(player.getName()))
                    .replaceAll("%Player%", quoteReplacement(player.getName()))
                    .replaceAll("%reward%", quoteReplacement(name))
                    .replaceAll("%crate%", quoteReplacement(crate.getCrateInventoryName())));
        }

        if (!crate.getPrizeMessage().isEmpty() && prize.getMessages().isEmpty()) {
            for (String message : crate.getPrizeMessage()) {
                if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                    message = PlaceholderAPI.setPlaceholders(player, message);
                }

                String name = prize.getDisplayItemBuilder().getName() == null || prize.getDisplayItemBuilder().getName().isEmpty() ? MsgUtils.color(WordUtils.capitalizeFully(prize.getDisplayItemBuilder().getMaterial().getKey().getKey().replaceAll("_", " "))) : prize.getDisplayItemBuilder().getName();

                MsgUtils.sendMessage(player, message
                        .replaceAll("%player%", quoteReplacement(player.getName()))
                        .replaceAll("%Player%", quoteReplacement(player.getName()))
                        .replaceAll("%reward%", quoteReplacement(name))
                        .replaceAll("%crate%", quoteReplacement(crate.getCrateInventoryName())), false);
            }

            return;
        }

        for (String message : prize.getMessages()) {
            if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }

            String name = prize.getDisplayItemBuilder().getName() == null || prize.getDisplayItemBuilder().getName().isEmpty() ? MsgUtils.color(WordUtils.capitalizeFully(prize.getDisplayItemBuilder().getMaterial().getKey().getKey().replaceAll("_", " "))) : prize.getDisplayItemBuilder().getName();

            MsgUtils.sendMessage(player, message
                    .replaceAll("%player%", quoteReplacement(player.getName()))
                    .replaceAll("%Player%", quoteReplacement(player.getName()))
                    .replaceAll("%reward%", quoteReplacement(name))
                    .replaceAll("%crate%", quoteReplacement(crate.getCrateInventoryName())), false);
        }
    }

    /**
     * Picks the prize for the player.
     *
     * @param player who the prize is for.
     * @param crate the player is opening.
     * @param prize the player is being given.
     */
    public void pickPrize(Player player, Crate crate, Prize prize) {
        if (prize != null) {
            givePrize(player, prize, crate);

            if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendMessage(MsgUtils.getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public void checkPrize(Prize prize, Player player, Crate crate) {
        if (prize != null) {
            givePrize(player, prize, crate);

            if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendMessage(MsgUtils.getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public void checkPrize(Tier tier, Prize prize, Player player, Crate crate) {
        if (prize != null && tier != null) {
            if (prize.getTiers().contains(tier)) {
                givePrize(player, prize, crate);

                if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

                this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
            }
        } else {
            player.sendMessage(MsgUtils.getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }
}