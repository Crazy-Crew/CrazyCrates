package com.badbones69.crazycrates.support.utils;

import com.badbones69.crazycrates.api.CrazyManager;
import com.badbones69.crazycrates.api.objects.Crate;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class KeyUtils {

    private final CrazyManager crazyManager = CrazyManager.getInstance();

    private static final KeyUtils keyUtils = new KeyUtils();

    public static KeyUtils getKeyUtils() {
        return keyUtils;
    }

    public void checkKeys(Player player, String messageHeader, Player otherPlayer) {

        AtomicBoolean hasKeys = new AtomicBoolean(false);

        HashMap<Crate, Integer> keys = crazyManager.getVirtualKeys(player);
        ArrayList<String> messages = new ArrayList<>();
        messages.add(messageHeader);

        keys.keySet().forEach(crate -> {
            int amount = keys.get(crate);

            if (amount > 0) {
                hasKeys.set(true);
                HashMap<String, String> placeholders = new HashMap<>();
                placeholders.put("%Crate%", crate.getFile().getString("Crate.Name"));
                placeholders.put("%Keys%", amount + "");
                //messages.add(Messages.PER_CRATE.getMessageNoPrefix(placeholders));
            }
        });

        if (hasKeys.get()) {
            if (otherPlayer != null) {

                if (otherPlayer.getUniqueId() == player.getUniqueId()) {
                    //player.sendMessage(Messages.SAME_PLAYER.getMessage());
                    return;
                }

                //otherPlayer.sendMessage(Messages.convertList(messages));
                return;
            }

            //player.sendMessage(Messages.convertList(messages));

            return;
        }

        if (otherPlayer != null) {
            //otherPlayer.sendMessage(Messages.OTHER_PLAYER_NO_VIRTUAL_KEYS.getMessage("%player%", player.getName()));
            return;
        }

        //player.sendMessage(Messages.PERSONAL_NO_VIRTUAL_KEYS.getMessage());
    }
}