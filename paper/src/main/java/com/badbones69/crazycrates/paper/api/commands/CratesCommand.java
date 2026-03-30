package com.badbones69.crazycrates.paper.api.commands;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CrateManager;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.commands.PaperCommand;

public abstract class CratesCommand extends PaperCommand {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final CratePlatform platform = this.plugin.getPlatform();

    protected final CrateManager crateManager = this.platform.getCrateManager();

    protected final FusionPaper fusion = this.platform.getFusion();

}