package us.crazycrew.crazycrates.paper;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.Methods;
import com.badbones69.crazycrates.paper.api.CrazyManager;
import com.badbones69.crazycrates.paper.api.FileManager;
import com.badbones69.crazycrates.paper.api.enums.settings.Messages;
import com.badbones69.crazycrates.paper.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.ItemBuilder;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.support.libraries.PluginSupport;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.common.CrazyCratesPlugin;
import us.crazycrew.crazycrates.common.config.types.PluginConfig;
import us.crazycrew.crazycrates.paper.api.MigrationService;
import us.crazycrew.crazycrates.paper.api.crates.CrateManager;
import us.crazycrew.crazycrates.paper.api.support.metrics.MetricsWrapper;
import us.crazycrew.crazycrates.paper.api.users.BukkitUserManager;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.regex.Matcher.quoteReplacement;

public class CrazyHandler extends CrazyCratesPlugin {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private BukkitUserManager userManager;

    private CrateManager crateManager;

    private FileManager fileManager;

    private MetricsWrapper metrics;

    private Methods methods;

    public CrazyHandler(File dataFolder) {
        super(dataFolder);
    }

    public void load() {
        super.enable();

        this.fileManager = new FileManager();
        this.fileManager.registerDefaultGenerateFiles("CrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuadCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("CosmicCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("QuickCrateExample.yml", "/crates", "/crates")
                .registerDefaultGenerateFiles("classic.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("nether.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("outdoors.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("sea.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("soul.nbt", "/schematics", "/schematics")
                .registerDefaultGenerateFiles("wooden.nbt", "/schematics", "/schematics")
                .registerCustomFilesFolder("/crates")
                .registerCustomFilesFolder("/schematics")
                .setup();

        this.methods = new Methods();

        this.crateManager = new CrateManager();

        // Creates user manager instance.
        this.userManager = new BukkitUserManager();

        // Migrates 2 config.yml settings to plugin-config.yml
        MigrationService service = new MigrationService();
        service.migrate();

        boolean metrics = getConfigManager().getPluginConfig().getProperty(PluginConfig.toggle_metrics);

        this.metrics = new MetricsWrapper();
        if (metrics) this.metrics.start();
    }

    public void unload() {
        super.disable();
    }

    /**
     * Give a player a prize they have won.
     *
     * @param player The player you wish to give the prize to.
     * @param prize The prize the player has won.
     */
    public void givePrize(Player player, Prize prize, Crate crate) {
        if (prize == null) {
            this.plugin.getLogger().warning("No prize was found when giving " + player.getName() + " a prize.");
            return;
        }

        prize = prize.hasBlacklistPermission(player) ? prize.getAltPrize() : prize;

        for (ItemStack item : prize.getItems()) {
            if (item == null) {
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%Crate%", prize.getCrate());
                placeholders.put("%Prize%", prize.getName());
                player.sendMessage(Messages.PRIZE_ERROR.getMessage(placeholders));
                continue;
            }

            if (!getMethods().isInventoryFull(player)) {
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

            if (!getMethods().isInventoryFull(player)) {
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
                            commandBuilder.append(pickNumber(min, max)).append(" ");
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

            getMethods().sendCommand(command.replaceAll("%player%", player.getName()).replaceAll("%Player%", player.getName()).replaceAll("%reward%", quoteReplacement(prize.getDisplayItemBuilder().getUpdatedName())).replaceAll("%crate%", crate.getCrateInventoryName()));
        }

        if (!crate.getPrizeMessage().isEmpty() && prize.getMessages().isEmpty()) {
            for (String message : crate.getPrizeMessage()) {
                if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                    message = PlaceholderAPI.setPlaceholders(player, message);
                }

                getMethods().sendMessage(player, message.replaceAll("%player%", player.getName()).replaceAll("%Player%", player.getName()).replaceAll("%reward%", quoteReplacement(prize.getDisplayItemBuilder().getName())).replaceAll("%crate%", crate.getCrateInventoryName()), false);
            }

            return;
        }

        for (String message : prize.getMessages()) {
            if (PluginSupport.PLACEHOLDERAPI.isPluginEnabled()) {
                message = PlaceholderAPI.setPlaceholders(player, message);
            }

            getMethods().sendMessage(player, message.replaceAll("%player%", player.getName()).replaceAll("%Player%", player.getName()).replaceAll("%reward%", quoteReplacement(prize.getDisplayItemBuilder().getName())).replaceAll("%crate%", crate.getCrateInventoryName()), false);
        }
    }

    /**
     * Picks the prize for the player.
     * @param player - The player who the prize is for.
     * @param crate - The crate the player is opening.
     * @param prize - The prize the player is being given.
     */
    public void pickPrize(Player player, Crate crate, Prize prize) {
        if (prize != null) {
            this.plugin.getCrazyHandler().givePrize(player, prize, crate);

            if (prize.useFireworks()) getMethods().firework(player.getLocation().add(0, 1, 0));

            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendMessage(getMethods().getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public void checkPrize(Prize prize, Player player, Crate crate) {
        if (prize != null) {
            givePrize(player, prize, crate);

            if (prize.useFireworks()) getMethods().firework(player.getLocation().add(0, 1, 0));

            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            player.sendMessage(getMethods().getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public long pickNumber(long min, long max) {
        max++;

        try {
            // new Random() does not have a nextLong(long bound) method.
            return min + ThreadLocalRandom.current().nextLong(max - min);
        } catch (IllegalArgumentException e) {
            return min;
        }
    }

    @NotNull
    public MetricsWrapper getMetrics() {
        return this.metrics;
    }

    @NotNull
    public Methods getMethods() {
        return this.methods;
    }

    @NotNull
    public FileManager getFileManager() {
        return this.fileManager;
    }

    @NotNull
    public CrateManager getCrateManager() {
        return this.crateManager;
    }

    @Override
    @NotNull
    public BukkitUserManager getUserManager() {
        return this.userManager;
    }
}