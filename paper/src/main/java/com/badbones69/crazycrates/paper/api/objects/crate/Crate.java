package com.badbones69.crazycrates.paper.api.objects.crate;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.managers.ItemManager;
import com.badbones69.crazycrates.paper.api.objects.items.DisplayItem;
import com.badbones69.crazycrates.paper.api.objects.other.CratePrize;
import com.badbones69.crazycrates.paper.api.objects.other.CrateSound;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.CrateType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Crate {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CratePlatform platform = this.plugin.getPlatform();

    private final ItemManager itemManager = this.platform.getItemManager();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final Map<String, CrateSound> sounds = new HashMap<>();
    private final Map<String, CratePrize> prizes = new HashMap<>();

    private final CommentedConfigurationNode configuration;

    private final DisplayItem displayItem;

    private final CrateType crateType;

    private final String crateName;

    public Crate(@NotNull final CommentedConfigurationNode configuration, @NotNull final String crateName) {
        this.crateType = CrateType.getFromName(configuration.node("type").getString("CSGO"));

        final CommentedConfigurationNode sound = configuration.node("sound");

        this.sounds.put("cycle", new CrateSound(sound.node("cycle-sound")));
        this.sounds.put("click", new CrateSound(sound.node("click-sound")));
        this.sounds.put("stop", new CrateSound(sound.node("stop-sound")));

        this.displayItem = new DisplayItem(configuration.node("display"));

        final CommentedConfigurationNode prizes = configuration.node("prizes");

        for (final Object key : prizes.childrenMap().keySet()) {
            final CommentedConfigurationNode prize = configuration.node(key);

            final String name = key.toString();

            this.prizes.put(name, new CratePrize(
                    name,
                    prize
            ));
        }

        this.configuration = configuration;
        this.crateName = crateName;
    }

    public final Optional<CrateSound> getSound(@NotNull final String type) {
        return Optional.ofNullable(this.sounds.get(type));
    }

    public final Optional<CratePrize> getPrize(@NotNull final String type) {
        return Optional.ofNullable(this.prizes.get(type));
    }

    public @NotNull final Map<String, CratePrize> getPrizes() {
        return Collections.unmodifiableMap(this.prizes);
    }

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