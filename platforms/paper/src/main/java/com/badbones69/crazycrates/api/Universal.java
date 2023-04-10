package com.badbones69.crazycrates.api;

import com.badbones69.crazycrates.CrazyCrates;

public interface Universal {

    CrazyCrates plugin = CrazyCrates.getPlugin();

    CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

    FileManager fileManager = plugin.getStarter().getFileManager();

    EventLogger eventLogger = plugin.getStarter().getEventLogger();

}