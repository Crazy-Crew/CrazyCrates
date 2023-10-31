package us.crazycrew.crazycrates.paper.commands;

import com.badbones69.crazycrates.paper.api.objects.CrateLocation;
import dev.triumphteam.cmd.bukkit.BukkitCommandManager;
import dev.triumphteam.cmd.core.suggestion.SuggestionKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.paper.CrazyCrates;
import us.crazycrew.crazycrates.paper.commands.handlers.ArgumentRelations;
import us.crazycrew.crazycrates.paper.commands.handlers.MiscRelations;
import us.crazycrew.crazycrates.paper.commands.subs.CrateBaseCommand;
import us.crazycrew.crazycrates.paper.commands.subs.player.BaseKeyCommand;
import java.util.ArrayList;
import java.util.List;

public class CommandManager {

    @NotNull
    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final BukkitCommandManager<CommandSender> bukkitCommandManager = this.plugin.getCommandManager();

    public void load() {
        this.bukkitCommandManager.registerCommand(new CrateBaseCommand());
        this.bukkitCommandManager.registerCommand(new BaseKeyCommand());

        new MiscRelations().build();
        new ArgumentRelations().build();

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("crates"), (sender, context) -> this.plugin.getFileManager().getAllCratesNames().stream().toList());

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("key-types"), (sender, context) -> List.of("virtual", "v", "physical", "p"));

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("online-players"), (sender, context) -> this.plugin.getServer().getOnlinePlayers().stream().map(Player::getName).toList());

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("locations"), (sender, context) -> this.plugin.getCrateManager().getCrateLocations().stream().map(CrateLocation::getID).toList());

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("prizes"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            this.plugin.getCrateManager().getCrateFromName(context.getArgs().get(0)).getPrizes().forEach(prize -> numbers.add(prize.getName()));

            return numbers;
        });

        this.bukkitCommandManager.registerSuggestion(SuggestionKey.of("numbers"), (sender, context) -> {
            List<String> numbers = new ArrayList<>();

            for (int i = 1; i <= 250; i++) numbers.add(i + "");

            return numbers;
        });

        this.bukkitCommandManager.registerArgument(CrateBaseCommand.CustomPlayer.class, (sender, context) -> new CrateBaseCommand.CustomPlayer(context));
    }
}