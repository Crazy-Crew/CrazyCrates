package me.badbones69.crazycrates.cratetypes;

import me.badbones69.crazycrates.Main;
import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.CrateType;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.Prize;
import me.badbones69.crazycrates.multisupport.Version;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class CSGO implements Listener
{

    private static CrazyCrates cc = CrazyCrates.getInstance();

    private static void setGlass(Inventory inv)
    {
        Random r = new Random();
        HashMap<Integer, ItemStack> Glass = new HashMap<Integer, ItemStack>();
        for (int i = 0; i < 10; i++)
        {
            if (i < 9 && i != 3)
            {
                Glass.put(i, inv.getItem(i));
            }
        }
        for (int i : Glass.keySet())
        {
            if (inv.getItem(i) == null)
            {
                int color = r.nextInt(15);
                inv.setItem(i, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
                inv.setItem(i + 18, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
            }
        }
        for (int i = 1; i < 10; i++)
        {
            if (i < 9 && i != 4)
            {
                Glass.put(i, inv.getItem(i));
            }
        }
        int color = r.nextInt(15);
        if (color == 8)
            color = 1;
        inv.setItem(0, Glass.get(1));
        inv.setItem(0 + 18, Glass.get(1));
        inv.setItem(1, Glass.get(2));
        inv.setItem(1 + 18, Glass.get(2));
        inv.setItem(2, Glass.get(3));
        inv.setItem(2 + 18, Glass.get(3));
        inv.setItem(3, Glass.get(5));
        inv.setItem(3 + 18, Glass.get(5));
        inv.setItem(4, Methods.makeItem(Material.STAINED_GLASS, 1, 15, " "));
        inv.setItem(4 + 18, Methods.makeItem(Material.STAINED_GLASS, 1, 15, " "));
        inv.setItem(5, Glass.get(6));
        inv.setItem(5 + 18, Glass.get(6));
        inv.setItem(6, Glass.get(7));
        inv.setItem(6 + 18, Glass.get(7));
        inv.setItem(7, Glass.get(8));
        inv.setItem(7 + 18, Glass.get(8));
        inv.setItem(8, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
        inv.setItem(8 + 18, Methods.makeItem(Material.STAINED_GLASS_PANE, 1, color, " "));
    }

    public static void openCSGO(Player player, Crate crate, KeyType key)
    {
        Inventory inv = Bukkit.createInventory(null, 27, Methods.color(crate.getFile().getString("Crate.CrateName")));
        setGlass(inv);
        for (int i = 9; i > 8 && i < 18; i++)
        {
            inv.setItem(i, cc.pickPrize(player, crate).getDisplayItem());
        }
        player.openInventory(inv);
        cc.takeKeys(1, player, crate, key);
        startCSGO(player, inv, crate);
    }

    private static void startCSGO(final Player player, final Inventory inv, Crate crate)
    {
        cc.addCrateTask(player, new BukkitRunnable()
        {
            int time = 1;
            int full = 0;
            int open = 0;

            @Override
            public void run()
            {
                if (full <= 50)
                {//When Spinning
                    moveItems(inv, player, crate);
                    setGlass(inv);
                    if (Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger())
                    {
                        player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
                    }
                    else
                    {
                        player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
                    }
                }
                open++;
                if (open >= 5)
                {
                    player.openInventory(inv);
                    open = 0;
                }
                full++;
                if (full > 51)
                {
                    if (slowSpin().contains(time))
                    {//When Slowing Down
                        moveItems(inv, player, crate);
                        setGlass(inv);
                        if (Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger())
                        {
                            player.playSound(player.getLocation(), Sound.valueOf("UI_BUTTON_CLICK"), 1, 1);
                        }
                        else
                        {
                            player.playSound(player.getLocation(), Sound.valueOf("CLICK"), 1, 1);
                        }
                    }
                    time++;
                    if (time >= 60)
                    {// When done
                        if (Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger())
                        {
                            player.playSound(player.getLocation(), Sound.valueOf("ENTITY_PLAYER_LEVELUP"), 1, 1);
                        }
                        else
                        {
                            player.playSound(player.getLocation(), Sound.valueOf("LEVEL_UP"), 1, 1);
                        }
                        cc.endCrate(player);
                        Prize prize = null;
                        for (Prize p : crate.getPrizes())
                        {
                            if (inv.getItem(13).isSimilar(p.getDisplayItem()))
                            {
                                prize = p;
                            }
                        }
                        cc.getReward(player, prize);
                        if (prize.toggleFirework())
                        {
                            Methods.fireWork(player.getLocation().add(0, 1, 0));
                        }
                        Bukkit.getPluginManager().callEvent(new PlayerPrizeEvent(player, CrateType.CSGO, crate.getName(), prize));
                        cc.removePlayerFromOpeningList(player);
                        return;
                    }
                }
            }
        }.runTaskTimer(Main.getPlugin(), 1, 1));

    }

    private static ArrayList<Integer> slowSpin()
    {
        ArrayList<Integer> slow = new ArrayList<Integer>();
        int full = 120;
        int cut = 15;
        for (int i = 120; cut > 0; full--)
        {
            if (full <= i - cut || full >= i - cut)
            {
                slow.add(i);
                i = i - cut;
                cut--;
            }
        }
        return slow;
    }

    private static void moveItems(Inventory inv, Player player, Crate crate)
    {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        for (int i = 9; i > 8 && i < 17; i++)
        {
            items.add(inv.getItem(i));
        }
        inv.setItem(9, cc.pickPrize(player, crate).getDisplayItem());
        for (int i = 0; i < 8; i++)
        {
            inv.setItem(i + 10, items.get(i));
        }
    }

}