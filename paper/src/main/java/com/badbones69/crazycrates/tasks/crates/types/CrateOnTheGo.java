package com.badbones69.crazycrates.tasks.crates.types;

import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import java.util.UUID;

public class CrateOnTheGo extends CrateBuilder {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull CrateManager crateManager = this.plugin.getCrateManager();

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    public CrateOnTheGo(Crate crate, Player player) {
        super(crate, player);
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        // Crate event failed so we return.
        if (isCrateEventValid(KeyType.physical_key, checkHand)) {
            return;
        }

        Player player = getPlayer();
        UUID uuid = player.getUniqueId();
        Crate crate = getCrate();
        String crateName = crate.getName();

        boolean keyCheck = this.userManager.takeKeys(1, uuid, crateName, KeyType.physical_key, true);

        if (!keyCheck) {
            // Remove from opening list.
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        Prize prize = crate.pickPrize(player);
        PrizeManager.givePrize(player, prize, crate);

        if (prize.useFireworks()) MiscUtils.spawnFirework(player.getLocation().add(0, 1, 0), null);

        this.crateManager.removePlayerKeyType(player);
    }

    @Override
    public void run() {

    }
}