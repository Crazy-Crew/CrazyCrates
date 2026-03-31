package com.badbones69.crazycrates.paper.api.objects.other;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.CratePlatform;
import com.badbones69.crazycrates.paper.api.managers.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.prize.Prize;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import java.util.*;

public class CratePrize {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CratePlatform platform = this.plugin.getPlatform();

    private final PrizeManager prizeManager = this.platform.getPrizeManager();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final List<Prize> keys = new ArrayList<>();

    private final int bulkPulls;
    private final double weight;

    private final String prizeName;

    public CratePrize(@NotNull final String prizeName, @NotNull final CommentedConfigurationNode configuration) {
        final List<String> keys = StringUtils.getStringList(configuration.node("keys"));

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

    public @NotNull final String getPrizeName() {
        return this.prizeName;
    }

    public @NotNull final List<Prize> getKeys() {
        return Collections.unmodifiableList(this.keys);
    }

    public final int getBulkPulls() {
        return this.bulkPulls;
    }

    public final double getWeight() {
        return this.weight;
    }
}