package com.badbones69.crazycrates.commands.crates.types.admin.crates.migrator;

import org.bukkit.configuration.ConfigurationSection;
import java.io.File;

public interface MigratorInterface {

    void run();

    <T> void set(ConfigurationSection section, String path, T value);

    File getCratesDirectory();

}