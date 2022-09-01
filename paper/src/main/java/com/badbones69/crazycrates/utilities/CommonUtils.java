package com.badbones69.crazycrates.utilities;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.events.player.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.bukkit.entity.Player;
import java.util.ArrayList;

@Singleton
public class CommonUtils {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    @Inject private CrazyManager crazyManager;

    @Inject private Methods methods;

    public void pickPrize(Player player, Crate crate, Prize prize) {
        if (prize != null) {
            crazyManager.givePrize(player, prize);

            if (prize.useFireworks()) methods.firework(player.getLocation().add(0, 1, 0));

            plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));
        } else {
            // player.sendMessage(methods.getPrefix("&cNo prize was found, please report this issue if you think this is an error."));
        }
    }

    public ArrayList<Integer> slowSpin() {
        ArrayList<Integer> slow = new ArrayList<>();
        int full = 46;
        int cut = 9;

        for (int i = 46; cut > 0; full--) {
            if (full <= i - cut || full >= i - cut) {
                slow.add(i);
                i -= cut;
                cut--;
            }
        }

        return slow;
    }
}