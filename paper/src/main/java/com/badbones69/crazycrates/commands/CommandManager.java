package com.badbones69.crazycrates.commands;

import com.badbones69.crazycrates.api.objects.other.CrateLocation;
import com.badbones69.crazycrates.commands.relations.ArgumentRelations;
import com.badbones69.crazycrates.commands.relations.MiscRelations;
import com.badbones69.crazycrates.commands.subs.CrateBaseCommand;
import com.badbones69.crazycrates.commands.subs.BaseKeyCommand;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.CrazyCrates;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final BukkitCommandManager<CommandSender> bukkitCommandManager = this.plugin.getCommandManager();

    /**
     * Loads commands.
     */
    public void load() {
        new MiscRelations().build();
        new ArgumentRelations().build();

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> {
            List<String> crates = new ArrayList<>(this.plugin.getFileManager().getAllCratesNames());

            crates.add("Menu");

            return crates;
        });

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> this.plugin.getCrateManager().getCrateLocations().stream().map(CrateLocation::getID).toList());

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            this.plugin.getCrateManager().getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getPrizeNumber()));

            return numbers;
        });

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("tiers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            this.plugin.getCrateManager().getCrateFromName(context.getArgs().get(0)).getTiers().forEach(tier -> numbers.add(tier.getName()));

            return numbers;
        });

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 100; i++) numbers.add(String.valueOf(i));

            return numbers;
        });

        this.bukkitCommandManager.registerArgument(CrateBaseCommand.CustomPlayer.class, (sender, context) -> new CrateBaseCommand.CustomPlayer(context));

        this.bukkitCommandManager.registerCommand(new CrateBaseCommand());
        this.bukkitCommandManager.registerCommand(new BaseKeyCommand());
    }
}