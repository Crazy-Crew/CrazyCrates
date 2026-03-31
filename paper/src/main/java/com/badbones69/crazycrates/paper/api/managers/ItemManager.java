package com.badbones69.crazycrates.paper.api.managers;

import com.badbones69.common.api.enums.keys.FileKeys;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import org.bukkit.inventory.ItemType;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.BasicConfigurationNode;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ItemManager {

    private final CratePlatform platform;
    private final CrazyCrates plugin;

    private final FusionPaper fusion;

    public ItemManager(@NotNull final CrazyCrates plugin) {
        this.plugin = plugin;

        this.platform = this.plugin.getPlatform();

        this.fusion = this.platform.getFusion();
    }

    private final Map<String, String> items = new HashMap<>();

    public void load() {
        final BasicConfigurationNode configuration = FileKeys.items.getJsonConfig();

        if (!configuration.hasChild("items")) {
            return;
        }

        final BasicConfigurationNode items = configuration.node("items");

        for (final BasicConfigurationNode entry : items.childrenList()) {
            entry.childrenMap().forEach((object, key) -> this.items.put(object.toString(), key.getString("")));
        }
    }

    public ItemBuilder getItem(@NotNull final String key) {
        if (key.isBlank()) {
            return ItemBuilder.from(ItemType.AIR);
        }

        final String value = this.items.getOrDefault(key, "");

        if (value.isBlank()) {
            return ItemBuilder.from(ItemType.AIR);
        }

        return ItemBuilder.from(value);
    }

    public @NotNull Map<String, String> getItems() {
        return Collections.unmodifiableMap(this.items);
    }
}