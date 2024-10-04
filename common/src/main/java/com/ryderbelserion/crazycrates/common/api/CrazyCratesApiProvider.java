package com.ryderbelserion.crazycrates.common.api;

import com.ryderbelserion.crazycrates.common.plugin.configs.ConfigManager;
import com.ryderbelserion.crazycrates.common.Settings;
import com.ryderbelserion.crazycrates.common.plugin.bootstrap.CrazyCratesPlugin;
import com.ryderbelserion.vital.common.util.FileUtil;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.CrazyCratesApi;
import us.crazycrew.crazycrates.api.users.UserManager;
import us.crazycrew.crazycrates.platform.IServer;
import us.crazycrew.crazycrates.platform.ISettings;
import java.io.File;
import java.util.List;
import java.util.Map;

public class CrazyCratesApiProvider implements CrazyCratesApi, IServer {

    private final CrazyCratesPlugin plugin;

    private final Settings settings;

    public CrazyCratesApiProvider(final CrazyCratesPlugin plugin) {
        this.plugin = plugin;

        this.settings = new Settings();
    }

    @Override
    public void reload() {
        ConfigManager.refresh();

        this.plugin.reload();
    }

    @Override
    public @NotNull File getCrateFolder() {
        return new File(getDataFolder(), "crates");
    }

    @Override
    public @NotNull File getDataFolder() {
        return this.plugin.getDataDirectory().toFile();
    }

    @Override
    public List<String> getCrateFiles() {
        return FileUtil.getFiles(getCrateFolder(), ".yml", false);
    }

    @Override
    public @NotNull UserManager getUserManager() {
        return this.plugin.getUserManager();
    }

    @Override
    public @NotNull ISettings getSettings() {
        return this.settings;
    }

    @Override
    public String parse(Audience audience, String line, Map<String, String> placeholders) {
        return this.plugin.parse(audience, line, placeholders);
    }
}