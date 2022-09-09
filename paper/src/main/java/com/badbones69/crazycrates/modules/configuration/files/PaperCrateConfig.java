package com.badbones69.crazycrates.modules.configuration.files;

import com.badbones69.crazycrates.CrazyCrates;
import com.google.common.collect.Lists;
import java.io.File;
import java.util.List;

public class PaperCrateConfig {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final List<String> files = Lists.newArrayList();

    public void setup() {

    }

    public List<String> getCrateNames() {

        String[] crates = new File(plugin.getDataFolder(), "/crates").list();

        if (crates != null) {
            for (String crateName : crates) {
                if (!crateName.endsWith(".yml")) continue;

                files.add(crateName.replaceAll(".yml", ""));
            }
        }

        return files;
    }
}