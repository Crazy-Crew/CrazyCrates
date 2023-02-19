package com.badbones69.crazycrates.utils.interfaces;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.CrazyManager;

public interface Universal {

    CrazyCrates plugin = CrazyCrates.getPlugin();

    CrazyManager crazyManager = plugin.getStarter().getCrazyManager();

}