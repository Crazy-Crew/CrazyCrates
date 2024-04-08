package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.commands.relations.ArgumentRelations;
import com.badbones69.crazycrates.commands.relations.MiscRelations;
import com.badbones69.crazycrates.commands.subs.CrateBaseCommand;
import com.badbones69.crazycrates.commands.subs.BaseKeyCommand;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    private final static @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final static @NotNull CrateManager crateManager = plugin.getCrateManager();

    private final static @NotNull BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new MiscRelations().build();
        new ArgumentRelations().build();

        commandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> {
            List<String> crates = new ArrayList<>(Arrays.stream(plugin.getInstance().getCrateFiles()).map(File::getName).toList());

            crates.add("Menu");

            return crates;
        });

        commandManager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        commandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> crateManager.getCrateLocations().stream().map(CrateLocation::getID).toList());

        commandManager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            crateManager.getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getPrizeNumber()));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("tiers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            crateManager.getCrateFromName(context.getArgs().get(0)).getTiers().forEach(tier -> numbers.add(tier.getName()));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerArgument(CrateBaseCommand.CustomPlayer.class, (sender, context) -> new CrateBaseCommand.CustomPlayer(context));

        commandManager.registerCommand(new CrateBaseCommand());
        commandManager.registerCommand(new BaseKeyCommand());
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}