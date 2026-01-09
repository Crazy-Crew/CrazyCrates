package com.badbones69.crazycrates.paper.utils;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.scheduler.FoliaScheduler;
import com.ryderbelserion.fusion.paper.scheduler.Scheduler;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Map;

public class CommandUtils {

    private static final CrazyCrates plugin = CrazyCrates.getPlugin();

    private static final FusionPaper fusion = plugin.getFusion();

    private static final Server server = plugin.getServer();

    public static void executeCommand(@Nullable final Audience audience, @NotNull final String command, @NotNull final Map<String, String> placeholders) {
        if (command.isEmpty()) return;

        final String result = fusion.papi(audience, fusion.replacePlaceholder(command, placeholders));

        new FoliaScheduler(plugin, Scheduler.global_scheduler) {
            @Override
            public void run() {
                server.dispatchCommand(server.getConsoleSender(), result);
            }
        }.runNow();
    }
}