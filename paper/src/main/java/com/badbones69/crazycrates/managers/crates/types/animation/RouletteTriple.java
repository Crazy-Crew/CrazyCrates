package com.badbones69.crazycrates.managers.crates.types.animation;

import com.badbones69.crazycrates.api.builders.CrateBuilder;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import us.crazycrew.crazycrates.api.enums.types.KeyType;

public class RouletteTriple extends CrateBuilder {

    public RouletteTriple(Crate crate, Player player, int size) {
        super(crate, player, size);
    }

    private int counter = 0;

    @Override
    public void run() {
        // If cancelled, we return.
        if (this.isCancelled) {
            return;
        }

        if (counter <= 50) {
            cycle();
        }

        if (counter >= 60) {
            cancel();

            return;
        }

        counter++;
    }

    @Override
    public void open(KeyType type, boolean checkHand) {
        populate();

        getPlayer().openInventory(getInventory());

        //this.plugin.getTimer().schedule(this, 1, 1);
    }

    private void populate() {
        for (int index = 0; index < 9; index++) {
            setCustomGlassPane(index);
            setCustomGlassPane(index+18);
            setCustomGlassPane(index+36);
        }
    }

    private void cycle() {

    }
}