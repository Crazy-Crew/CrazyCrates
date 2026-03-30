package com.badbones69.crazycrates.paper.api.objects;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.objects.items.DisplayItem;
import com.badbones69.crazycrates.paper.api.objects.other.CrateSound;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.CrateType;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Crate {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CratePlatform platform = this.plugin.getPlatform();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final Map<String, CrateSound> sounds = new HashMap<>();

    private final CommentedConfigurationNode configuration;

    private final DisplayItem displayItem;

    private final CrateType crateType;

    //private final int startingKeys;
    //private final int requiredKeys;

    //private final int bulkOpenLimit;

    //private final boolean isBroadcasting;
    //private final String broadcastValue;

    private final String crateName;

    public Crate(@NotNull final CommentedConfigurationNode configuration, @NotNull final String crateName) {
        this.crateType = CrateType.getFromName(configuration.node("type").getString("CSGO"));

        final CommentedConfigurationNode sound = configuration.node("sound");

        this.sounds.put("cycle", new CrateSound(sound.node("cycle-sound")));
        this.sounds.put("click", new CrateSound(sound.node("click-sound")));
        this.sounds.put("stop", new CrateSound(sound.node("stop-sound")));

        //this.startingKeys = configuration.node("StartingKeys").getInt(0);
        //this.requiredKeys = configuration.node("RequiredKeys").getInt(0);

        //this.bulkOpenLimit = configuration.node("Max-Mass-Open").getInt(10);

        this.displayItem = new DisplayItem(
                configuration.node("display")
        );

        //this.isBroadcasting = configuration.node("OpeningBroadcast").getBoolean(true);
        //this.broadcastValue = configuration.node("BroadCast").getString("");

        this.configuration = configuration;
        this.crateName = crateName;
    }

    public final Optional<CrateSound> getSound(@NotNull final String type) {
        return Optional.ofNullable(this.sounds.get(type));
    }

    /*public final int getStartingKeys() {
        return this.startingKeys;
    }

    public final int getRequiredKeys() {
        return this.requiredKeys;
    }

    public final int getBulkOpenLimit() {
        return this.bulkOpenLimit;
    }

    public void broadcast(@NotNull final Player player, @NotNull final Map<String, String> placeholders) {
        if (!this.isBroadcasting || this.broadcastValue.isBlank()) return;

        this.server.broadcast(this.fusion.asComponent(player, this.broadcastValue, placeholders));
    }*/

    public @NotNull final CommentedConfigurationNode getConfiguration() {
        return this.configuration;
    }

    public @NotNull final DisplayItem getDisplayItem() {
        return this.displayItem;
    }

    public @NotNull final CrateType getCrateType() {
        return this.crateType;
    }

    public @NotNull final String getCrateName() {
        return this.crateName;
    }
}