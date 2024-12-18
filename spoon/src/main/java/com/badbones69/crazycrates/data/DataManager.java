package com.badbones69.crazycrates.data;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.data.interfaces.Connector;
import com.badbones69.crazycrates.data.types.SqliteConnector;
import java.io.File;

public class DataManager {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private Connector connector;

    public final DataManager init() {
        this.connector = new SqliteConnector().init(new File(this.plugin.getDataFolder(), "crazycrates.db"));

        return this;
    }

    public final Connector getConnector() {
        return this.connector;
    }
}