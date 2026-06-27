package com.ryderbelserion.crazycrates.paper.api.objects.other;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.crazycrates.paper.api.managers.PrizeManager;
import com.ryderbelserion.crazycrates.paper.api.objects.prize.Prize;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.*;

public final class CratePrize {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CratePlatform platform = this.plugin.getPlatform();

    private final PrizeManager prizeManager = this.platform.getPrizeManager();

    private final List<Prize> keys = new ArrayList<>();

    private final int bulkPulls;
    private final double weight;

    private final String prizeName;

    public CratePrize(@NotNull final String prizeName, @NotNull final CommentedConfigurationNode configuration) {
        final CommentedConfigurationNode prizes = configuration.node("keys");

        final List<String> keys = StringUtils.getStringList(prizes);

        for (final String key : keys) {
            if (key.isBlank()) continue;

            final Optional<Prize> optional = this.prizeManager.getPrize(key);

            if (optional.isEmpty()) continue;

            this.keys.add(optional.get());
        }

        this.bulkPulls = configuration.node("max-pulls").getInt(-1);
        this.weight = configuration.node("weight").getDouble(-1);

        this.prizeName = prizeName;
    }

    public @NotNull final List<Prize> getKeys() {
        return Collections.unmodifiableList(this.keys);
    }

    public @NotNull String getPrizeName() {
        return this.prizeName;
    }

    public int getBulkPulls() {
        return this.bulkPulls;
    }

    public double getWeight() {
        return this.weight;
    }
}