package com.badbones69.crazycrates.api.managers;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.enums.types.KeyType;
import org.bukkit.entity.Player;

public interface CrateManager {

    CrazyCrates plugin = CrazyCrates.getPlugin();

    CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    void openCrate(Player player, Crate crate, KeyType keyType, boolean checkHand);

}