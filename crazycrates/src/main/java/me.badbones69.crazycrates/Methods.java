package me.badbones69.crazycrates;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.enums.Messages;
import me.badbones69.crazycrates.controlers.FileManager;
import me.badbones69.crazycrates.controlers.FireworkDamageAPI;
import me.badbones69.crazycrates.multisupport.Version;
import me.badbones69.crazycrates.v1_10_R1.NMS_v1_10_R1;
import me.badbones69.crazycrates.v1_11_R1.NMS_v1_11_R1;
import me.badbones69.crazycrates.v1_12_R1.NMS_v1_12_R1;
import me.badbones69.crazycrates.v1_8_R1.NMS_v1_8_R1;
import me.badbones69.crazycrates.v1_8_R2.NMS_v1_8_R2;
import me.badbones69.crazycrates.v1_8_R3.NMS_v1_8_R3;
import me.badbones69.crazycrates.v1_9_R1.NMS_v1_9_R1;
import me.badbones69.crazycrates.v1_9_R2.NMS_v1_9_R2;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.logging.Level;

public class Methods
{

    public static HashMap<Player, String> path = new HashMap<Player, String>();
    public static Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("CrazyCrates");
    private static CrazyCrates cc = CrazyCrates.getInstance();

    public static String color(String msg)
    {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static String removeColor(String msg)
    {
        return ChatColor.stripColor(msg);
    }

    public static HashMap<ItemStack, String> getItems(Player player)
    {
        HashMap<ItemStack, String> items = new HashMap<ItemStack, String>();
        FileConfiguration file = cc.getOpeningCrate(player).getFile();
        for (String reward : file.getConfigurationSection("Crate.Prizes").getKeys(false))
        {
            String id = file.getString("Crate.Prizes." + reward + ".DisplayItem");
            String name = file.getString("Crate.Prizes." + reward + ".DisplayName");
            int chance = file.getInt("Crate.Prizes." + reward + ".Chance");
            int max = 99;
            if (file.contains("Crate.Prizes." + reward + ".MaxRange"))
            {
                max = file.getInt("Crate.Prizes." + reward + ".MaxRange") - 1;
            }
            try
            {
                ItemStack item = makeItem(id, 1, name);
                Random number = new Random();
                int num;
                for (int counter = 1; counter <= 1; counter++)
                {
                    num = 1 + number.nextInt(max);
                    if (num >= 1 && num <= chance) items.put(item, "Crate.Prizes." + reward);
                }
            }
            catch (Exception e)
            {
                continue;
            }
        }
        return items;
    }

    public static void fireWork(Location loc)
    {
        final Firework fw = loc.getWorld().spawn(loc, Firework.class);
        FireworkMeta fm = fw.getFireworkMeta();
        fm.addEffects(FireworkEffect.builder().with(FireworkEffect.Type.BALL_LARGE).withColor(Color.RED).withColor(Color.AQUA).withColor(Color.ORANGE).withColor(Color.YELLOW).trail(false).flicker(false).build());
        fm.setPower(0);
        fw.setFireworkMeta(fm);
        FireworkDamageAPI.addFirework(fw);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {
            public void run()
            {
                fw.detonate();
            }
        }, 2);
    }

    public static ItemStack makeItem(Material material, int amount, int type, String name)
    {
        ItemStack item = new ItemStack(material, amount, (short) type);
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(color(name));
        item.setItemMeta(m);
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(String id, int amount, String name, List<String> lore)
    {
        ArrayList<String> l = new ArrayList<String>();
        int ty = 0;
        PotionType potionType = null;
        if (id.contains(":"))
        {
            String[] b = id.split(":");
            id = b[0];
            if (isInt(b[1]))
            {
                ty = Integer.parseInt(b[1]);
            }
            else
            {
                potionType = getPotionType(PotionEffectType.getByName(b[1]));
            }
        }
        Material m = Material.matchMaterial(id);
        if (m == Material.MOB_SPAWNER)
        {
            ty = 0;
        }
        ItemStack item = new ItemStack(m, amount, (short) ty);
        if (m == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        ItemMeta me = item.getItemMeta();
        me.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        me.setLore(l);
        item.setItemMeta(me);
        if (Version.getVersion().comparedTo(Version.v1_8_R3) == 1)
        {
            if (item.getType() == Material.TIPPED_ARROW && potionType != null)
            {
                PotionMeta pm = (PotionMeta) item.getItemMeta();
                pm.setBasePotionData(new PotionData(potionType));
                item.setItemMeta(pm);
            }
        }
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(String id, int amount, String name, List<String> lore, Boolean Enchanted)
    {
        ArrayList<String> l = new ArrayList<String>();
        int ty = 0;
        PotionType potionType = null;
        if (id.contains(":"))
        {
            String[] b = id.split(":");
            id = b[0];
            if (isInt(b[1]))
            {
                ty = Integer.parseInt(b[1]);
            }
            else
            {
                potionType = getPotionType(PotionEffectType.getByName(b[1]));
            }
        }
        Material m = Material.matchMaterial(id);
        if (m == Material.MOB_SPAWNER)
        {
            ty = 0;
        }
        ItemStack item = new ItemStack(m, amount, (short) ty);
        if (m == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        ItemMeta me = item.getItemMeta();
        me.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        me.setLore(l);
        item.setItemMeta(me);
        if (Version.getVersion().comparedTo(Version.v1_8_R3) == 1)
        {
            if (item.getType() == Material.TIPPED_ARROW && potionType != null)
            {
                PotionMeta pm = (PotionMeta) item.getItemMeta();
                pm.setBasePotionData(new PotionData(potionType));
                item.setItemMeta(pm);
            }
        }
        if (Enchanted)
        {
            item = addGlow(item);
        }
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(String id, int amount, String name)
    {
        int ty = 0;
        PotionType potionType = null;
        if (id.contains(":"))
        {
            String[] b = id.split(":");
            id = b[0];
            if (isInt(b[1]))
            {
                ty = Integer.parseInt(b[1]);
            }
            else
            {
                potionType = getPotionType(PotionEffectType.getByName(b[1]));
            }
        }
        Material m = Material.matchMaterial(id);
        if (m == Material.MOB_SPAWNER)
        {
            ty = 0;
        }
        ItemStack item = new ItemStack(m, amount, (short) ty);
        if (m == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        ItemMeta me = item.getItemMeta();
        me.setDisplayName(color(name));
        item.setItemMeta(me);
        if (Version.getVersion().comparedTo(Version.v1_8_R3) == 1)
        {
            if (item.getType() == Material.TIPPED_ARROW && potionType != null)
            {
                PotionMeta pm = (PotionMeta) item.getItemMeta();
                pm.setBasePotionData(new PotionData(potionType));
                item.setItemMeta(pm);
            }
        }
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(Material material, int amount, int ty, String name, List<String> lore)
    {
        ArrayList<String> l = new ArrayList<String>();
        ItemStack item = new ItemStack(material, amount, (short) ty);
        ItemMeta m = item.getItemMeta();
        if (material == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        m.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        m.setLore(l);
        item.setItemMeta(m);
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(String id, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants)
    {
        ArrayList<String> l = new ArrayList<String>();
        String type = id;
        int ty = 0;
        PotionType potionType = null;
        if (type.contains(":"))
        {
            String[] b = type.split(":");
            type = b[0];
            if (isInt(b[1]))
            {
                ty = Integer.parseInt(b[1]);
            }
            else
            {
                potionType = getPotionType(PotionEffectType.getByName(b[1]));
            }
        }
        Material material = Material.matchMaterial(type);
        if (material == Material.MOB_SPAWNER)
        {
            ty = 0;
        }
        ItemStack item = new ItemStack(material, amount, (short) ty);
        if (material == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        m.setLore(l);
        item.setItemMeta(m);
        item.addUnsafeEnchantments(enchants);
        if (Version.getVersion().comparedTo(Version.v1_8_R3) == 1)
        {
            if (item.getType() == Material.TIPPED_ARROW && potionType != null)
            {
                PotionMeta pm = (PotionMeta) item.getItemMeta();
                pm.setBasePotionData(new PotionData(potionType));
                item.setItemMeta(pm);
            }
        }
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(String id, int amount, String name, List<String> lore, Map<Enchantment, Integer> enchants, Boolean glowing)
    {
        ArrayList<String> l = new ArrayList<String>();
        String type = id;
        int ty = 0;
        PotionType potionType = null;
        if (type.contains(":"))
        {
            String[] b = type.split(":");
            type = b[0];
            if (isInt(b[1]))
            {
                ty = Integer.parseInt(b[1]);
            }
            else
            {
                potionType = getPotionType(PotionEffectType.getByName(b[1]));
            }
        }
        Material material = Material.matchMaterial(type);
        if (material == Material.MOB_SPAWNER)
        {
            ty = 0;
        }
        ItemStack item = new ItemStack(material, amount, (short) ty);
        if (material == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        m.setLore(l);
        item.setItemMeta(m);
        item.addUnsafeEnchantments(enchants);
        if (Version.getVersion().comparedTo(Version.v1_8_R3) == 1)
        {
            if (item.getType() == Material.TIPPED_ARROW && potionType != null)
            {
                PotionMeta pm = (PotionMeta) item.getItemMeta();
                pm.setBasePotionData(new PotionData(potionType));
                item.setItemMeta(pm);
            }
        }
        if (glowing)
        {
            item = addGlow(item);
        }
        return item;
    }

    @SuppressWarnings("deprecation")
    public static ItemStack makeItem(Material material, int amount, int ty, String name, List<String> lore, Map<Enchantment, Integer> enchants)
    {
        ArrayList<String> l = new ArrayList<String>();
        ItemStack item = new ItemStack(material, amount, (short) ty);
        if (material == Material.MONSTER_EGG)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    item = NMS_v1_12_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_11_R1:
                    item = NMS_v1_11_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_10_R1:
                    item = NMS_v1_10_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R2:
                    item = NMS_v1_9_R2.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                case v1_9_R1:
                    item = NMS_v1_9_R1.getSpawnEgg(EntityType.fromId(ty), amount);
                    break;
                default:
                    break;
            }
        }
        ItemMeta m = item.getItemMeta();
        m.setDisplayName(color(name));
        for (String L : lore)
            l.add(color(L));
        m.setLore(l);
        item.setItemMeta(m);
        item.addUnsafeEnchantments(enchants);
        return item;
    }

    /**
     * @param player
     * @param name
     * @param amount
     * @param lore
     * @param enchants
     * @param glowing
     * @return
     */
    public static ItemStack makePlayerHead(String player, int amount, String name, ArrayList<String> lore, Map<Enchantment, Integer> enchants, boolean glowing)
    {
        ItemStack head = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta m = (SkullMeta) head.getItemMeta();
        if (!player.equals(""))
        {
            m.setOwner(player);
        }
        if (name != null)
        {
            m.setDisplayName(color(name));
        }
        if (lore != null)
        {
            ArrayList<String> l = new ArrayList<String>();
            for (String L : lore)
            {
                l.add(color(L));
            }
            m.setLore(l);
        }
        head.setItemMeta(m);
        if (enchants != null)
        {
            head.addUnsafeEnchantments(enchants);
        }
        if (glowing)
        {
            head = addGlow(head);
        }
        return head;
    }

    public static ItemStack addLore(ItemStack item, String i)
    {
        ArrayList<String> lore = new ArrayList<String>();
        ItemMeta m = item.getItemMeta();
        if (item.getItemMeta().hasLore())
        {
            lore.addAll(item.getItemMeta().getLore());
        }
        lore.add(color(i));
        m.setLore(lore);
        item.setItemMeta(m);
        return item;
    }

    public static PotionType getPotionType(PotionEffectType type)
    {
        PotionType potionType = null;
        if (type.equals(PotionEffectType.FIRE_RESISTANCE))
        {
            potionType = PotionType.FIRE_RESISTANCE;
        }
        else if (type.equals(PotionEffectType.HARM))
        {
            potionType = PotionType.INSTANT_DAMAGE;
        }
        else if (type.equals(PotionEffectType.HEAL))
        {
            potionType = PotionType.INSTANT_HEAL;
        }
        else if (type.equals(PotionEffectType.INVISIBILITY))
        {
            potionType = PotionType.INVISIBILITY;
        }
        else if (type.equals(PotionEffectType.JUMP))
        {
            potionType = PotionType.JUMP;
        }
        else if (type.equals(PotionEffectType.LUCK))
        {
            potionType = PotionType.LUCK;
        }
        else if (type.equals(PotionEffectType.NIGHT_VISION))
        {
            potionType = PotionType.NIGHT_VISION;
        }
        else if (type.equals(PotionEffectType.POISON))
        {
            potionType = PotionType.POISON;
        }
        else if (type.equals(PotionEffectType.REGENERATION))
        {
            potionType = PotionType.REGEN;
        }
        else if (type.equals(PotionEffectType.SLOW))
        {
            potionType = PotionType.SLOWNESS;
        }
        else if (type.equals(PotionEffectType.SPEED))
        {
            potionType = PotionType.SPEED;
        }
        else if (type.equals(PotionEffectType.INCREASE_DAMAGE))
        {
            potionType = PotionType.STRENGTH;
        }
        else if (type.equals(PotionEffectType.WATER_BREATHING))
        {
            potionType = PotionType.WATER_BREATHING;
        }
        else if (type.equals(PotionEffectType.WEAKNESS))
        {
            potionType = PotionType.WEAKNESS;
        }
        return potionType;
    }

    public static boolean isInt(String s)
    {
        try
        {
            Integer.parseInt(s);
        }
        catch (NumberFormatException nfe)
        {
            return false;
        }
        return true;
    }

    public static Player getPlayer(String name)
    {
        return Bukkit.getServer().getPlayer(name);
    }

    public static boolean isOnline(String name, CommandSender sender)
    {
        for (Player player : Bukkit.getServer().getOnlinePlayers())
        {
            if (player.getName().equalsIgnoreCase(name))
            {
                return true;
            }
        }
        HashMap<String, String> placeholders = new HashMap<>();
        placeholders.put("%player%", name);
        sender.sendMessage(Messages.NOT_ONLINE.getMessage(placeholders));
        return false;
    }

    public static void removeItem(ItemStack item, Player player)
    {
        try
        {
            if (item.getAmount() <= 1)
            {
                player.getInventory().removeItem(item);
            }
            else
            {
                item.setAmount(item.getAmount() - 1);
            }
        }
        catch (Exception e)
        {
        }
    }

    public static void removeItem(ItemStack item, Player player, int amount)
    {
        try
        {
            int left = amount;
            for (ItemStack check : player.getInventory().getContents())
            {
                if (isSimilar(item, check))
                {
                    int i = check.getAmount();
                    if ((left - i) >= 0)
                    {
                        player.getInventory().removeItem(check);
                        left -= i;
                    }
                    else
                    {
                        check.setAmount(check.getAmount() - left);
                        left = 0;
                    }
                    if (left <= 0)
                    {
                        break;
                    }
                    else
                    {
                        continue;
                    }
                }
            }
        }
        catch (Exception e)
        {
        }
    }

    @SuppressWarnings("deprecation")
    public static ItemStack getItemInHand(Player player)
    {
        if (Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger())
        {
            return player.getInventory().getItemInMainHand();
        }
        else
        {
            return player.getItemInHand();
        }
    }

    @SuppressWarnings("deprecation")
    public static void setItemInHand(Player player, ItemStack item)
    {
        if (Version.getVersion().getVersionInteger() >= Version.v1_9_R1.getVersionInteger())
        {
            player.getInventory().setItemInMainHand(item);
        }
        else
        {
            player.setItemInHand(item);
        }
    }

    public static boolean permCheck(Player player, String perm)
    {
        if (!player.hasPermission("me.badbones.me.badbones69.crazycrates." + perm.toLowerCase()))
        {
            player.sendMessage(Messages.NO_PERMISSION.getMessage());
            return false;
        }
        return true;
    }

    public static String getPrefix()
    {
        return color(FileManager.Files.CONFIG.getFile().getString("Settings.Prefix"));
    }

    public static String getPrefix(String msg)
    {
        return color(FileManager.Files.CONFIG.getFile().getString("Settings.Prefix") + msg);
    }

    public static void pasteSchem(String schem, Location loc)
    {
        switch (Version.getVersion())
        {
            case TOO_NEW:
                Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. " + "Please update or remove this plugin to stop further Errors.");
                break;
            case v1_12_R1:
                NMS_v1_12_R1.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_11_R1:
                NMS_v1_11_R1.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_10_R1:
                NMS_v1_10_R1.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_9_R2:
                NMS_v1_9_R2.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_9_R1:
                NMS_v1_9_R1.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_8_R3:
                NMS_v1_8_R3.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_8_R2:
                NMS_v1_8_R2.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case v1_8_R1:
                NMS_v1_8_R1.pasteSchematic(new File(plugin.getDataFolder() + "/Schematics/" + schem), loc);
                break;
            case TOO_OLD:
                Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. " + "Please update or remove this plugin to stop further Errors.");
                break;
            default:
                break;
        }
    }

    public static List<Location> getLocations(String shem, Location loc)
    {
        switch (Version.getVersion())
        {
            case TOO_NEW:
                Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. " + "Please update or remove this plugin to stop further Errors.");
                break;
            case v1_12_R1:
                return NMS_v1_12_R1.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_11_R1:
                return NMS_v1_11_R1.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_10_R1:
                return NMS_v1_10_R1.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_9_R2:
                return NMS_v1_9_R2.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_9_R1:
                return NMS_v1_9_R1.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_8_R3:
                return NMS_v1_8_R3.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_8_R2:
                return NMS_v1_8_R2.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case v1_8_R1:
                return NMS_v1_8_R1.getLocations(new File(plugin.getDataFolder() + "/Schematics/" + shem), loc);
            case TOO_OLD:
                Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. " + "Please update or remove this plugin to stop further Errors.");
                break;
            default:
                break;
        }
        return null;
    }

    public static void playChestAction(Block b, boolean open)
    {
        Location location = b.getLocation();
        Material type = b.getType();
        if (type == Material.CHEST || type == Material.TRAPPED_CHEST || type == Material.ENDER_CHEST)
        {
            switch (Version.getVersion())
            {
                case TOO_NEW:
                    Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too new for this plugin. " + "Please update or remove this plugin to stop further Errors.");
                    break;
                case v1_12_R1:
                    NMS_v1_12_R1.openChest(b, location, open);
                    break;
                case v1_11_R1:
                    NMS_v1_11_R1.openChest(b, location, open);
                    break;
                case v1_10_R1:
                    NMS_v1_10_R1.openChest(b, location, open);
                    break;
                case v1_9_R2:
                    NMS_v1_9_R2.openChest(b, location, open);
                    break;
                case v1_9_R1:
                    NMS_v1_9_R1.openChest(b, location, open);
                    break;
                case v1_8_R3:
                    NMS_v1_8_R3.openChest(b, location, open);
                    break;
                case v1_8_R2:
                    NMS_v1_8_R2.openChest(b, location, open);
                    break;
                case v1_8_R1:
                    NMS_v1_8_R1.openChest(b, location, open);
                    break;
                case TOO_OLD:
                    Bukkit.getLogger().log(Level.SEVERE, "[Crazy Crates]>> Your server is too far out of date. " + "Please update or remove this plugin to stop further Errors.");
                    break;
                default:
                    break;
            }
        }
    }

    public static ItemStack addGlow(ItemStack item)
    {
        switch (Version.getVersion())
        {
            case v1_12_R1:
                return NMS_v1_12_R1.addGlow(item);
            case v1_11_R1:
                return NMS_v1_11_R1.addGlow(item);
            case v1_10_R1:
                return NMS_v1_10_R1.addGlow(item);
            case v1_9_R2:
                return NMS_v1_9_R2.addGlow(item);
            case v1_9_R1:
                return NMS_v1_9_R1.addGlow(item);
            case v1_8_R3:
                return NMS_v1_8_R3.addGlow(item);
            case v1_8_R2:
                return NMS_v1_8_R2.addGlow(item);
            case v1_8_R1:
                return NMS_v1_8_R1.addGlow(item);
            default:
                break;
        }
        return item;
    }

    public static ItemStack addUnbreaking(ItemStack item)
    {
        switch (Version.getVersion())
        {
            case v1_12_R1:
                return NMS_v1_12_R1.addUnbreaking(item);
            case v1_11_R1:
                return NMS_v1_11_R1.addUnbreaking(item);
            case v1_10_R1:
                return NMS_v1_10_R1.addUnbreaking(item);
            case v1_9_R2:
                return NMS_v1_9_R2.addUnbreaking(item);
            case v1_9_R1:
                return NMS_v1_9_R1.addUnbreaking(item);
            default:
                break;
        }
        return item;
    }

    public static ItemStack addUnbreaking(ItemStack item, Boolean toggle)
    {
        if (toggle)
        {
            switch (Version.getVersion())
            {
                case v1_12_R1:
                    return NMS_v1_12_R1.addUnbreaking(item);
                case v1_11_R1:
                    return NMS_v1_11_R1.addUnbreaking(item);
                case v1_10_R1:
                    return NMS_v1_10_R1.addUnbreaking(item);
                case v1_9_R2:
                    return NMS_v1_9_R2.addUnbreaking(item);
                case v1_9_R1:
                    return NMS_v1_9_R1.addUnbreaking(item);
                default:
                    break;
            }
        }
        return item;
    }

    public static String pickRandomSchem()
    {
        File f = new File(plugin.getDataFolder() + "/Schematics/");
        String[] schems = f.list();
        ArrayList<String> schematics = new ArrayList<String>();
        for (String i : schems)
        {
            if (!i.equalsIgnoreCase(".DS_Store"))
            {
                schematics.add(i);
            }
        }
        Random r = new Random();
        return schematics.get(r.nextInt(schematics.size()));
    }

    public static boolean isInvFull(Player player)
    {
        if (player.getInventory().firstEmpty() == -1)
        {
            return true;
        }
        return false;
    }

    public static Integer randomNumber(int min, int max)
    {
        Random i = new Random();
        return min + i.nextInt(max - min);
    }

    public static boolean isSimilar(ItemStack one, ItemStack two)
    {
        if (one != null && two != null)
        {
            if (one.getType() == two.getType())
            {
                if (one.hasItemMeta() && two.hasItemMeta())
                {
                    if (one.getItemMeta().hasDisplayName() && two.getItemMeta().hasDisplayName())
                    {
                        if (one.getItemMeta().getDisplayName().equalsIgnoreCase(two.getItemMeta().getDisplayName()))
                        {
                            if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore())
                            {
                                if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size())
                                {
                                    int i = 0;
                                    for (String lore : one.getItemMeta().getLore())
                                    {
                                        if (!lore.equals(two.getItemMeta().getLore().get(i)))
                                        {
                                            return false;
                                        }
                                        i++;
                                    }
                                    return true;
                                }
                            }
                            else if (!one.getItemMeta().hasLore() && !two.getItemMeta().hasLore())
                            {
                                return true;
                            }
                        }
                    }
                    else if (!one.getItemMeta().hasDisplayName() && !two.getItemMeta().hasDisplayName())
                    {
                        if (one.getItemMeta().hasLore() && two.getItemMeta().hasLore())
                        {
                            if (one.getItemMeta().getLore().size() == two.getItemMeta().getLore().size())
                            {
                                int i = 0;
                                for (String lore : one.getItemMeta().getLore())
                                {
                                    if (!lore.equals(two.getItemMeta().getLore().get(i)))
                                    {
                                        return false;
                                    }
                                    i++;
                                }
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }
                        else if (!one.getItemMeta().hasLore() && !two.getItemMeta().hasLore())
                        {
                            return true;
                        }
                    }
                }
                else if (!one.hasItemMeta() && !two.hasItemMeta())
                {
                    return true;
                }
            }
        }
        return false;
    }

    public static void hasUpdate()
    {
        try
        {
            HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=17599").getBytes("UTF-8"));
            String oldVersion = plugin.getDescription().getVersion();
            String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
            if (!newVersion.equals(oldVersion))
            {
                Bukkit.getConsoleSender().sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
            }
        }
        catch (Exception e)
        {
            return;
        }
    }

    public static void hasUpdate(Player player)
    {
        try
        {
            HttpURLConnection c = (HttpURLConnection) new URL("http://www.spigotmc.org/api/general.php").openConnection();
            c.setDoOutput(true);
            c.setRequestMethod("POST");
            c.getOutputStream().write(("key=98BE0FE67F88AB82B4C197FAF1DC3B69206EFDCC4D3B80FC83A00037510B99B4&resource=17599").getBytes("UTF-8"));
            String oldVersion = plugin.getDescription().getVersion();
            String newVersion = new BufferedReader(new InputStreamReader(c.getInputStream())).readLine().replaceAll("[a-zA-Z ]", "");
            if (!newVersion.equals(oldVersion))
            {
                player.sendMessage(getPrefix() + color("&cYour server is running &7v" + oldVersion + "&c and the newest is &7v" + newVersion + "&c."));
            }
        }
        catch (Exception e)
        {
            return;
        }
    }

    public static Set<String> getEnchantments()
    {
        HashMap<String, String> enchants = new HashMap<String, String>();
        enchants.put("ARROW_DAMAGE", "Power");
        enchants.put("ARROW_FIRE", "Flame");
        enchants.put("ARROW_INFINITE", "Infinity");
        enchants.put("ARROW_KNOCKBACK", "Punch");
        enchants.put("DAMAGE_ALL", "Sharpness");
        enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchants.put("DAMAGE_UNDEAD", "Smite");
        enchants.put("DEPTH_STRIDER", "Depth_Strider");
        enchants.put("DIG_SPEED", "Efficiency");
        enchants.put("DURABILITY", "Unbreaking");
        enchants.put("FIRE_ASPECT", "Fire_Aspect");
        enchants.put("KNOCKBACK", "KnockBack");
        enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchants.put("LOOT_BONUS_MOBS", "Looting");
        enchants.put("LUCK", "Luck_Of_The_Sea");
        enchants.put("LURE", "Lure");
        enchants.put("OXYGEN", "Respiration");
        enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchants.put("PROTECTION_FALL", "Feather_Falling");
        enchants.put("PROTECTION_FIRE", "Fire_Protection");
        enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchants.put("SILK_TOUCH", "Silk_Touch");
        enchants.put("THORNS", "Thorns");
        enchants.put("WATER_WORKER", "Aqua_Affinity");
        enchants.put("BINDING_CURSE", "Curse_Of_Binding");
        enchants.put("MENDING", "Mending");
        enchants.put("FROST_WALKER", "Frost_Walker");
        enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        return enchants.keySet();
    }

    public static String getEnchantmentName(Enchantment en)
    {
        HashMap<String, String> enchants = new HashMap<String, String>();
        enchants.put("ARROW_DAMAGE", "Power");
        enchants.put("ARROW_FIRE", "Flame");
        enchants.put("ARROW_INFINITE", "Infinity");
        enchants.put("ARROW_KNOCKBACK", "Punch");
        enchants.put("DAMAGE_ALL", "Sharpness");
        enchants.put("DAMAGE_ARTHROPODS", "Bane_Of_Arthropods");
        enchants.put("DAMAGE_UNDEAD", "Smite");
        enchants.put("DEPTH_STRIDER", "Depth_Strider");
        enchants.put("DIG_SPEED", "Efficiency");
        enchants.put("DURABILITY", "Unbreaking");
        enchants.put("FIRE_ASPECT", "Fire_Aspect");
        enchants.put("KNOCKBACK", "KnockBack");
        enchants.put("LOOT_BONUS_BLOCKS", "Fortune");
        enchants.put("LOOT_BONUS_MOBS", "Looting");
        enchants.put("LUCK", "Luck_Of_The_Sea");
        enchants.put("LURE", "Lure");
        enchants.put("OXYGEN", "Respiration");
        enchants.put("PROTECTION_ENVIRONMENTAL", "Protection");
        enchants.put("PROTECTION_EXPLOSIONS", "Blast_Protection");
        enchants.put("PROTECTION_FALL", "Feather_Falling");
        enchants.put("PROTECTION_FIRE", "Fire_Protection");
        enchants.put("PROTECTION_PROJECTILE", "Projectile_Protection");
        enchants.put("SILK_TOUCH", "Silk_Touch");
        enchants.put("THORNS", "Thorns");
        enchants.put("WATER_WORKER", "Aqua_Affinity");
        enchants.put("BINDING_CURSE", "Curse_Of_Binding");
        enchants.put("MENDING", "Mending");
        enchants.put("FROST_WALKER", "Frost_Walker");
        enchants.put("VANISHING_CURSE", "Curse_Of_Vanishing");
        if (enchants.get(en.getName()) == null)
        {
            return "None Found";
        }
        return enchants.get(en.getName());
    }

}