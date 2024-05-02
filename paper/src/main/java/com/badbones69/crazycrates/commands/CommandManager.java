package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandAddItem;
import com.badbones69.crazycrates.commands.relations.ArgumentRelations;
import com.badbones69.crazycrates.commands.crates.types.player.CommandHelp;
import com.badbones69.crazycrates.commands.crates.types.admin.CommandAdmin;
import com.badbones69.crazycrates.commands.crates.types.admin.CommandReload;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandDebug;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandList;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandPreview;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandSet;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandTeleport;
import com.badbones69.crazycrates.commands.crates.types.admin.keys.CommandGive;
import com.badbones69.crazycrates.commands.crates.types.admin.keys.CommandOpen;
import com.badbones69.crazycrates.commands.crates.types.admin.keys.CommandTake;
import com.badbones69.crazycrates.commands.crates.types.player.CommandKey;
import com.badbones69.crazycrates.commands.crates.types.player.CommandTransfer;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.util.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private final static @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
    private final static @NotNull CrateManager crateManager = plugin.getCrateManager();

    private final static @NotNull BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> {
            List<String> crates = new ArrayList<>(plugin.getCrateManager().getCrateNames());

            crates.add("Menu");

            return crates;
        });

        commandManager.registerSuggestion(SuggestionKey.of("keys"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> crateManager.getCrateLocations().stream().map(CrateLocation::getID).toList());

        commandManager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            crateManager.getCrateFromName(context.getFirst()).getPrizes().forEach(prize -> numbers.add(prize.getSectionName()));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("tiers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            crateManager.getCrateFromName(context.getFirst()).getTiers().forEach(tier -> numbers.add(tier.getName()));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerArgument(PlayerBuilder.class, (sender, context) -> new PlayerBuilder(context));

        List.of(
                new CommandTeleport(),
                new CommandAddItem(),
                new CommandPreview(),
                new CommandDebug(),
                new CommandList(),
                new CommandSet(),

                new CommandGive(),
                new CommandOpen(),
                new CommandTake(),

                new CommandReload(),
                new CommandAdmin(),

                new CommandTransfer(),
                new CommandKey(),

                new CommandHelp()
        ).forEach(commandManager::registerCommand);
    }

    public static @NotNull BukkitCommandManager<CommandSender> getCommandManager() {
        return commandManager;
    }
}