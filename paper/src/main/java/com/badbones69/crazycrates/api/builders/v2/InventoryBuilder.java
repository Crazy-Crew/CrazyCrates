package com.badbones69.crazycrates.api.builders.v2;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import us.crazycrew.crazycrates.api.users.UserManager;

public abstract class InventoryBuilder {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final CrateManager crateManager = this.plugin.getCrateManager();

    protected final UserManager userManager = this.plugin.getUserManager();

    protected final InventoryManager inventoryManager = this.plugin.getInventoryManager();

    protected final SettingsManager config = ConfigManager.getConfig();

}