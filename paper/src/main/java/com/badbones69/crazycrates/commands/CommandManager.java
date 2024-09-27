package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.crates.CrateLocation;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandAddItem;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.CommandMigrate;
import com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator.enums.MigrationType;
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
import com.ryderbelserion.vital.paper.api.builders.PlayerBuilder;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();
    private static final CrateManager crateManager = plugin.getCrateManager();

    private static final BukkitCommandManager<CommandSender> commandManager = BukkitCommandManager.create(plugin);

    /**
     * Loads commands.
     */
    public static void load() {
        new ArgumentRelations().build();

        commandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> {
            final List<String> crates = new ArrayList<>(crateManager.getCrateNames());

            crates.add("Menu");

            return crates;
        });

        commandManager.registerSuggestion(SuggestionKey.of("keys"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        commandManager.registerSuggestion(SuggestionKey.of("admin-keys"), (sender, context) -> List.of("virtual", "v", "physical", "p", "free", "f"));

        commandManager.registerSuggestion(SuggestionKey.of("players"), (sender, context) -> plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        commandManager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> crateManager.getCrateLocations().stream().map(CrateLocation::getID).toList());

        commandManager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            final List<String> prizes = new ArrayList<>();

            Crate crate = crateManager.getCrateFromName(context.getFirst());

            if (crate != null) {
                crate.getPrizes().forEach(prize -> prizes.add(prize.getSectionName()));
            }

            return prizes;
        });

        commandManager.registerSuggestion(SuggestionKey.of("tiers"), (sender, context) -> {
            final List<String> tiers = new ArrayList<>();

            Crate crate = crateManager.getCrateFromName(context.getFirst());

            if (crate != null) {
                crate.getTiers().forEach(tier -> tiers.add(tier.getName()));
            }

            return tiers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            final List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("doubles"), (sender, context) -> {
            final List<String> numbers = new ArrayList<>();

            int count = 0;

            while (count <= 1000) {
                double x = count / 10.0;

                numbers.add(String.valueOf(x));

                count++;
            }

            return numbers;
        });

        commandManager.registerSuggestion(SuggestionKey.of("migrators"), (sender, context) -> {
            final List<String> migrators = new ArrayList<>();

            for (MigrationType value : MigrationType.values()) {
                final String name = value.getName();

                migrators.add(name);
            }

            return migrators;
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

                new CommandMigrate(),
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