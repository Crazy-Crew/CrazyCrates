package me.badbones69.crazycrates.multisupport.converters;

import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import plus.crates.Crates.Crate;
import plus.crates.CratesPlus;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class CratesPlusConverter {
    
    private static final CrazyCrates cc = CrazyCrates.getInstance();
    private static final CratesPlus cratesPlus = CratesPlus.getOpenHandler().getCratesPlus();
    private static final List<CrateConvertInfo> convertedCrates = new ArrayList<>();
    
    public static void convert() {
        int slot = 1;
        FileConfiguration config = cratesPlus.getConfig();
        for (Crate crate : cratesPlus.getConfigHandler().getCrates().values()) {
            String crateName = crate.getName();
            String path = "Crates." + crateName + ".";
            if (config.contains(path + "Winnings")) {
                convertedCrates.add(new CrateConvertInfo()
                .setCrateName(crateName)
                .setGUIName(config.getString(path + "GUI Title", "&7" + crateName))
                .setInGUI(true)
                .setOpeningBrodcastEnabled(crate.isBroadcast())
                .setBroadcast("%Prefix%&6&l%Player% &7is opening a &7&l" + crateName + " Chest&7.")
                .setDisplayItem(new ItemBuilder()
                .setMaterial(crate.getBlock())
                .setDamage(crate.getBlockData())
                .setName(config.getString(path + "Item Title", crateName))
                .setLore(config.getStringList(path + "Lore")))
                .setPreviewEnabled(config.getBoolean(path + "Preview"))
                .setPreviewName("&7" + crateName + " Preview")
                .setSlot(slot)
                .setPreviewLines(6)
                .setBoarderEnabled(true)
                .setBoarderItem(new ItemBuilder().setMaterial("GRAY_STAINED_GLASS_PANE", "STAINED_GLASS_PANE:7"))
                .setPhysicalKey(new ItemBuilder()
                .setMaterial(config.getString(path + "Key.Item", "TRIPWIRE_HOOK"))
                .setName(config.getString(path + "Key.Name", crateName).replace("%type%", crateName))
                .setLore(config.getStringList(path + "Key.Lore"))
                .setGlowing(config.getBoolean(path + "Key.Enchanted")))
                .setPrizes(convertPrizes(path, crateName, crate)));
                slot++;
            }
        }
        for (CrateConvertInfo crate : convertedCrates) {
            File newFile = new File(cc.getPlugin().getDataFolder(), "Crates/" + crate.getCrateName() + ".yml");
            if (!newFile.exists()) {
                try {
                    newFile.createNewFile();
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            FileConfiguration crateFile = YamlConfiguration.loadConfiguration(newFile);
            String path = "Crate.";
            crateFile.set(path + "CrateType", crate.getCrateType().getName());
            crateFile.set(path + "CrateName", crate.getGUIName());
            crateFile.set(path + "Preview-Name", crate.getPreviewName());
            crateFile.set(path + "StartingKeys", crate.getStartingKeys());
            crateFile.set(path + "InGUI", crate.isInGUI());
            crateFile.set(path + "Slot", crate.getSlot());
            crateFile.set(path + "OpeningBroadCast", crate.isOpeningBrodcastEnabled());
            crateFile.set(path + "BroadCast", crate.getBroadcast());
            crateFile.set(path + "Item", crate.getDisplayItem().getMaterial().name() + (crate.getDisplayItem().getDamage() > 0 ? ":" + crate.getDisplayItem().getDamage() : ""));
            crateFile.set(path + "Glowing", crate.getDisplayItem().isGlowing());
            crateFile.set(path + "Name", crate.getDisplayItem().getName());
            crateFile.set(path + "Lore", crate.getDisplayItem().getLore());
            crateFile.set(path + "Preview.Toggle", crate.isPreviewEnabled());
            crateFile.set(path + "Preview.ChestLines", crate.getPreviewLines());
            crateFile.set(path + "Preview.Glass.Toggle", crate.isBoarderEnabled());
            crateFile.set(path + "Preview.Glass.Item", crate.getBoarderItem().getMaterial().name() + (crate.getBoarderItem().getDamage() > 0 ? ":" + crate.getBoarderItem().getDamage() : ""));
            crateFile.set(path + "PhysicalKey.Name", crate.getPhysicalKey().getName());
            crateFile.set(path + "PhysicalKey.Lore", crate.getPhysicalKey().getLore());
            crateFile.set(path + "PhysicalKey.Item", crate.getPhysicalKey().getMaterial().name());
            crateFile.set(path + "PhysicalKey.Glowing", crate.getPhysicalKey().isGlowing());
            for (PrizeConvertInfo prize : crate.getPrizes()) {
                String prizePath = path + "Prizes." + prize.getName() + ".";
                crateFile.set(prizePath + "DisplayName", prize.getDisplayItem().getName().isEmpty() || prize.getDisplayItem().getName().equalsIgnoreCase("none") ? "" : prize.getDisplayItem().getName());
                String displayItem = prize.getDisplayItem().getMaterial().name();
                if (prize.getDisplayItem().isMobEgg()) {
                    displayItem += (prize.getDisplayItem().getEntityType() != null ? ":" + prize.getDisplayItem().getEntityType().name() : "");
                } else {
                    displayItem += (prize.getDisplayItem().getDamage() > 0 ? ":" + prize.getDisplayItem().getDamage() : "");
                }
                crateFile.set(prizePath + "DisplayItem", displayItem);
                crateFile.set(prizePath + "DisplayAmount", prize.getDisplayItem().getAmount());
                crateFile.set(prizePath + "Lore", prize.getDisplayItem().getLore());
                if (!prize.getDisplayEnchantments().isEmpty()) {
                    crateFile.set(prizePath + "DisplayEnchantments", prize.getDisplayEnchantments());
                }
                crateFile.set(prizePath + "MaxRange", prize.getMaxRange());
                crateFile.set(prizePath + "Chance", prize.getChance());
                if (prize.isFirework()) {
                    crateFile.set(prizePath + "Firework", true);
                }
                if (prize.getDisplayItem().isGlowing()) {
                    crateFile.set(prizePath + "Glowing", true);
                }
                if (!prize.getItems().isEmpty()) {
                    crateFile.set(prizePath + "Items", prize.getItems());
                }
                if (!prize.getCommands().isEmpty()) {
                    crateFile.set(prizePath + "Commands", prize.getCommands());
                }
                if (!prize.getMessages().isEmpty()) {
                    crateFile.set(prizePath + "Messages", prize.getMessages());
                }
            }
            try {
                crateFile.save(new File(cc.getPlugin().getDataFolder(), "Crates/" + crate.getCrateName() + ".yml"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public List<CrateConvertInfo> getConvertedCrates() {
        return convertedCrates;
    }
    
    private static List<PrizeConvertInfo> convertPrizes(String path, String crateName, Crate crate) {
        List<PrizeConvertInfo> prizes = new ArrayList<>();
        FileConfiguration config = cratesPlus.getConfig();
        for (String id : config.getConfigurationSection(path + "Winnings").getKeys(false)) {
            String prizePath = path + "Winnings." + id + ".";
            PrizeConvertInfo convertedPrize = new PrizeConvertInfo()
            .setCrate(crateName)
            .setName(id)
            .setDisplayItem(new ItemBuilder()
            .setMaterial(config.contains(prizePath + "Item Type") ? config.getString(prizePath + "Item Type") : config.getString(prizePath + "Block Type", "Stone"))
            .setDamage(config.contains(prizePath + "Item Data") ? config.getInt(prizePath + "Item Data") : config.getInt(prizePath + "Metadata", 0))
            .setEntityType(EntityType.valueOf(config.getString(prizePath + "Entity Type", "CREEPER")))
            .setAmount(config.getInt(prizePath + "Amount", 1))
            .setName(config.getString(prizePath + "Name", ""))
            .setLore(config.getStringList(prizePath + "Lore")));
            config.getStringList(prizePath + "Enchantments").forEach(enchantment -> convertedPrize.addDisplayEnchantments(enchantment.replace("-", ":")));
            convertedPrize.setMaxRange(100)
            .setChance(config.getInt(prizePath + "Percentage", 50))
            .setFirework(crate.isFirework());
            if (config.getString(prizePath + "Type").equalsIgnoreCase("item") || config.getString(prizePath + "Type").equalsIgnoreCase("block")) {
                ItemBuilder displayItem = convertedPrize.getDisplayItem();
                String displayItemString = displayItem.getMaterial().name();
                if (displayItem.isMobEgg()) {
                    displayItemString += (displayItem.getEntityType() != null ? ":" + displayItem.getEntityType().name() : "");
                } else {
                    displayItemString += (displayItem.getDamage() > 0 ? ":" + displayItem.getDamage() : "");
                }
                String item =
                "Item:" + displayItemString + ", " +
                (displayItem.getAmount() == 1 ? "" : "Amount:" + displayItem.getAmount() + ", ") +
                (displayItem.getName().isEmpty() || displayItem.getName().equalsIgnoreCase("none") ? "" : "Name:" + displayItem.getName() + ", ");
                if (!displayItem.getLore().isEmpty()) {
                    item += "Lore:";
                    StringBuilder itemBuilder = new StringBuilder(item);
                    for (String lore : displayItem.getLore()) {
                        itemBuilder.append(lore).append(",");
                    }
                    item = itemBuilder.toString();
                    item = item.substring(0, item.length() - 1) + ", ";
                    
                }
                if (!convertedPrize.getDisplayEnchantments().isEmpty()) {
                    StringBuilder itemBuilder = new StringBuilder(item);
                    for (String enchantment : convertedPrize.getDisplayEnchantments()) {
                        itemBuilder.append(enchantment).append(", ");
                    }
                    item = itemBuilder.toString();
                }
                item = item.substring(0, item.length() - 2);
                convertedPrize.addItem(item);
            } else {
                List<String> commands = new ArrayList<>();
                for (String command : config.getStringList(prizePath + (config.contains(prizePath + "Commands") ? "Commands" : "commands"))) {
                    commands.add(command.replaceAll("%name%", "%player%"));
                }
                convertedPrize.setCommands(commands);
            }
            prizes.add(convertedPrize);
        }
        return prizes;
    }
    
}