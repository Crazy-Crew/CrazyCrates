package com.badbones69.crazycrates.modules.config.data;

import com.badbones69.crazycrates.modules.config.types.json.PersistEngine;
import com.badbones69.crazycrates.utilities.logger.CrazyLogger;
import java.beans.Transient;

public class Data extends PersistEngine {

    @Transient
    public void load(CrazyLogger crazyLogger) {
        handle(Data.class, crazyLogger);
    }
}