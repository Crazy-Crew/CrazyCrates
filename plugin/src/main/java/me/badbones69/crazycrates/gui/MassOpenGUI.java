package me.badbones69.crazycrates.gui;

import me.badbones69.crazycrates.Methods;
import me.badbones69.crazycrates.api.CrazyCrates;
import me.badbones69.crazycrates.api.FileManager;
import me.badbones69.crazycrates.api.enums.KeyType;
import me.badbones69.crazycrates.api.objects.Crate;
import me.badbones69.crazycrates.api.objects.CrateLocation;
import me.badbones69.crazycrates.api.objects.ItemBuilder;
import me.badbones69.crazycrates.cratetypes.QuickCrate;
import me.lucko.helper.menu.Gui;
import me.lucko.helper.menu.Item;
import me.lucko.helper.menu.scheme.MenuPopulator;
import me.lucko.helper.menu.scheme.MenuScheme;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class MassOpenGUI extends Gui {
    private static final CrazyCrates cc = CrazyCrates.getInstance();

    private static final boolean DEFAULT_AUTOCLOSE = true;

    private static ConfigurationSection guiConfig;

    private static int MENU_LINES;

    private static MenuScheme INFO_TEMPLATE;
    private static MenuScheme TOGGLE_TEMPLATE;
    private static MenuScheme ITEMS_TEMPLATE;

    private static List<Integer> OPEN_ITEM_COUNTS;

    static {
        reloadConfig();
    }

    public final Crate crate;

    public final CrateLocation crateLocation;

    private final Item infoItem;

    public MassOpenGUI(Player player, Crate crate, CrateLocation crateLocation) {
        super(player, MENU_LINES, Methods.sanitizeColor(guiConfig.getString("Title")));
        this.crate = crate;
        this.crateLocation = crateLocation;

        this.infoItem = Item.builder(new ItemBuilder()
                    .setMaterial(guiConfig.getString("Information.Item"))
                    .setDamage(guiConfig.getInt("Information.Damage", 0))
                    .setName(guiConfig.getString("Information.Name"))
                    .setLore(guiConfig.getStringList("Information.Lore"))
                    .build()
                ).build();
    }

    @Override
    public void redraw() {
        final int keyCount = getKeyCount();

        INFO_TEMPLATE.newPopulator(this).accept(infoItem);
        TOGGLE_TEMPLATE.newPopulator(this).accept(Item.builder(makeAutoClose(shouldAutoClose()))
                .bind(ClickType.LEFT, this::toggleAutoClose)
                .build());

        final MenuPopulator populator = ITEMS_TEMPLATE.newPopulator(this);
        for (Integer count : OPEN_ITEM_COUNTS) {
            populator.acceptIfSpace(Item.builder(makeOpenItem(count, keyCount >= count))
                    .bind(ClickType.LEFT, () -> openKeys(count))
                    .build());
        }
    }

    private void openKeys(int openCount) {
        if (getKeyCount() < openCount) {
            redraw();
            return;
        }

        final Player player = getPlayer();
        final int physicalKeyCount = Math.min(cc.getPhysicalKeys(player, crate), openCount);
        if (physicalKeyCount > 0) {
            QuickCrate.openCrate(player, crateLocation.getLocation(), crate, KeyType.PHYSICAL_KEY, physicalKeyCount);
        }
        if (openCount > physicalKeyCount) {
            final int remainingKeyCount = Math.min(cc.getVirtualKeys(player, crate), openCount - physicalKeyCount);
            QuickCrate.openCrate(player, crateLocation.getLocation(), crate, KeyType.VIRTUAL_KEY, remainingKeyCount);
        }

        if (shouldAutoClose()) {
            player.closeInventory();
        } else {
            redraw();
        }
    }

    private void toggleAutoClose() {
        final boolean newState = !shouldAutoClose();
        final String key = "AutoClose." + getPlayer().getUniqueId();
        if (newState != DEFAULT_AUTOCLOSE) {
            FileManager.Files.DATA.getFile().set(key, newState);
        } else {
            // don't let the file grow unnecessarily if player goes back to default
            FileManager.Files.DATA.getFile().set(key, null);
        }
        FileManager.Files.DATA.saveFile();
        redraw();
    }

    public boolean shouldAutoClose() {
        return FileManager.Files.DATA.getFile().getBoolean("AutoClose." + getPlayer().getUniqueId(), DEFAULT_AUTOCLOSE);
    }

    public int getKeyCount() {
        return cc.getPhysicalKeys(getPlayer(), crate) + cc.getVirtualKeys(getPlayer(), crate);
    }

    private static ItemStack makeOpenItem(int count, boolean enough) {
        final String path = "OpenItem." + (enough ? "Enough" : "NotEnough") + ".";
        return new ItemBuilder()
                .setMaterial(guiConfig.getString(path + "Item", "INK_SACK"))
                .setDamage(guiConfig.getInt(path + "Damage", 0))
                .setName(guiConfig.getString(path + "Name"))
                .setLore(guiConfig.getStringList(path + "Lore"))
                .setAmount(count)
                .addNamePlaceholder("%Count%", Integer.toString(count))
                .addLorePlaceholder("%Count%", Integer.toString(count))
                .build();
    }

    private static ItemStack makeAutoClose(boolean enabled) {
        final String path = "AutoCloseToggle." + (enabled ? "Enabled" : "Disabled") + ".";
        return new ItemBuilder()
                .setMaterial(guiConfig.getString(path + "Item", "STONE"))
                .setDamage(guiConfig.getInt(path + "Damage", 0))
                .setName(guiConfig.getString(path + "Name"))
                .setLore(guiConfig.getStringList(path + "Lore"))
                .build();
    }

    public static void reloadConfig() {
        guiConfig = FileManager.Files.CONFIG.getFile().getConfigurationSection("Settings")
                .getConfigurationSection("MassOpenGUI");

        final ConfigurationSection layout = guiConfig.getConfigurationSection("Layout");;
        final List<String> template = layout.getStringList("Scheme");
        MENU_LINES = template.size();
        INFO_TEMPLATE = loadMenuScheme(template, layout.getString("Information").charAt(0));
        TOGGLE_TEMPLATE = loadMenuScheme(template, layout.getString("AutoCloseToggle").charAt(0));
        ITEMS_TEMPLATE = loadMenuScheme(template, layout.getString("OpenItems").charAt(0));

        OPEN_ITEM_COUNTS = guiConfig.getIntegerList("OpenItemCounts");
    }

    protected static MenuScheme loadMenuScheme(List<String> template, char character) {
        MenuScheme menuScheme = new MenuScheme();
        for(String s : template) {
            StringBuilder mask = new StringBuilder();
            for(int i = 0; i < s.length(); ++i) {
                if(s.charAt(i) == character) {
                    mask.append('1');
                } else {
                    mask.append('0');
                }
            }
            menuScheme = menuScheme.mask(mask.toString());
        }
        return menuScheme;
    }
}
