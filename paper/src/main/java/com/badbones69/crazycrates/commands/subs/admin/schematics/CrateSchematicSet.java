package com.badbones69.crazycrates.commands.subs.admin.schematics;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.commands.CrateBaseCommand;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import java.util.HashMap;
import java.util.UUID;

public class CrateSchematicSet extends CrateBaseCommand implements Listener {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    @SubCommand("set")
    public void set(Player player, int set) {
        Block block = player.getTargetBlock(10);

        if (block == null || block.isEmpty()) {
            // player.sendMessage(Messages.MUST_BE_LOOKING_AT_A_BLOCK.getMessage());
            return;
        }

        HashMap<UUID, Location[]> schem = crazyManager.getSchematicLocations();

        if (schem.containsKey(player.getUniqueId())) {
            Location locationOne = set == 1 ? block.getLocation() : schem.getOrDefault(player.getUniqueId(), null)[0];
            Location locationTwo = set == 2 ? block.getLocation() : schem.getOrDefault(player.getUniqueId(), null)[1];
            schem.put(player.getUniqueId(), new Location[]{locationOne, locationTwo});
        }

        // player.sendMessage(Messages.SCHEMATICS_LOCATION_SET.getMessage().replace("%set%", set));
    }

    @SubCommand("save")
    public void save(Player player) {

    }
}