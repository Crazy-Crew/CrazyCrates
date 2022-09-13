package com.badbones69.crazycrates.api.utilities.handlers.objects.crates;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.Methods;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.api.utilities.LoggerUtils;
import com.badbones69.crazycrates.api.utilities.handlers.objects.ItemBuilder;
import com.badbones69.crazycrates.api.utilities.handlers.objects.Prize;
import com.badbones69.crazycrates.api.utilities.handlers.objects.Tier;
import com.badbones69.crazycrates.common.enums.crates.CrateType;
import com.badbones69.crazycrates.api.managers.CosmicCrateManager;
import com.badbones69.crazycrates.api.managers.CrateManager;
import com.badbones69.crazycrates.common.configuration.files.Config;
import com.badbones69.crazycrates.common.configuration.objects.CrateHologram;
import com.google.common.collect.Lists;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Crate {

    // Global Methods.
    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final LoggerUtils loggerUtils = plugin.getStarter().getLoggerUtils();

    private final Methods methods = plugin.getStarter().getMethods();

    private final FileManager fileManager = plugin.getStarter().getFileManager();

    // Class Internals.
    private CrateManager manager;
    private final String name;
    private final ItemStack key;
    private final ItemStack adminKey;
    private int maxPage = 1;
    private final int maxSlots;
    private final String previewName;
    private final boolean previewToggle;
    private final boolean borderToggle;
    private final ItemBuilder boarderItem;
    private final CrateType crateType;
    private final FileConfiguration file;
    private final ArrayList<Prize> prizes;
    private final String crateInventoryName;
    private final boolean giveNewPlayerKeys;
    private int previewChestLines;
    private final int newPlayerKeys;
    private final ArrayList<ItemStack> preview;
    private final ArrayList<Tier> tiers;
    private final CrateHologram hologram;

    /**
     * Builds the entire crate.
     * @param name - The name of the crate.
     * @param crateType - The crate type of the crate.
     * @param key - The key as an item stack.
     * @param prizes - The prizes that can be won.
     * @param file - The crate file.
     */
    public Crate(String name, String previewName, CrateType crateType, ItemStack key, ArrayList<Prize> prizes, FileConfiguration file, int newPlayerKeys, ArrayList<Tier> tiers, CrateHologram hologram) {
        ItemBuilder itemBuilder = ItemBuilder.convertItemStack(key);
        this.key = itemBuilder.setCrateName(name).build();
        this.adminKey = itemBuilder
        .addLore("")
        .addLore("&7&l(&6&l!&7&l) Left click for Physical Key")
        .addLore("&7&l(&6&l!&7&l) Right click for Virtual Key")
        .setCrateName(name).build();
        this.file = file;
        this.name = name;
        this.tiers = tiers != null ? tiers : new ArrayList<>();
        this.prizes = prizes;
        this.crateType = crateType;
        this.preview = loadPreview();
        this.previewToggle = file != null && (!file.contains("Crate.Preview.Toggle") || file.getBoolean("Crate.Preview.Toggle"));
        this.borderToggle = file != null && file.getBoolean("Crate.Preview.Glass.Toggle");
        setPreviewChestLines(file != null ? file.getInt("Crate.Preview.ChestLines", 6) : 6);
        this.previewName = previewName;
        this.newPlayerKeys = newPlayerKeys;
        this.giveNewPlayerKeys = newPlayerKeys > 0;
        this.maxSlots = previewChestLines * 9;

        for (int amount = preview.size(); amount > maxSlots - (borderToggle ? 18 : maxSlots == preview.size() ? 0 : maxSlots != 9 ? 9 : 0); amount -= maxSlots - (borderToggle ? 18 : maxSlots == preview.size() ? 0 : maxSlots != 9 ? 9 : 0), maxPage++) ;

        this.crateInventoryName = file != null ? file.getString("Crate.CrateName") : "";
        this.boarderItem = file != null && file.contains("Crate.Preview.Glass.Item") ? new ItemBuilder().setMaterial(file.getString("Crate.Preview.Glass.Item")).setName(" ") : new ItemBuilder().setMaterial(Material.AIR);
        this.hologram = hologram != null ? hologram : new CrateHologram();

        if (crateType == CrateType.COSMIC && file != null) {
            this.manager = new CosmicCrateManager(file);
        }

        //this.previewListener = previewListener;
    }
    
    /**
     * Get the crate manager which contains all the settings for that crate type.
     */
    public CrateManager getManager() {
        return manager;
    }
    
    /**
     * Set the preview lines for a Crate.
     * @param amount - The amount of lines the preview has.
     */
    public void setPreviewChestLines(int amount) {
        int finalAmount;

        if (amount < 3 && borderToggle) {
            finalAmount = 3;
        } else finalAmount = Math.min(amount, 6);

        this.previewChestLines = finalAmount;
    }
    
    /**
     * Get the amount of lines the preview will show.
     * @return The amount of lines it is set to show.
     */
    public int getPreviewChestLines() {
        return this.previewChestLines;
    }
    
    /**
     * Get the max amount of slots in the preview.
     * @return The max number of slots in the preview.
     */
    public int getMaxSlots() {
        return maxSlots;
    }
    
    /**
     * Check to see if a player can win a prize from a crate.
     * @param player - The player you are checking.
     * @return True if they can win at least 1 prize and false if they can't win any.
     */
    public boolean canWinPrizes(Player player) {
        return pickPrize(player) != null;
    }
    
    /**
     * Picks a random prize based on BlackList Permissions and the Chance System.
     * @param player - The player that will be winning the prize.
     * @return The winning prize.
     */
    public Prize pickPrize(Player player) {
        ArrayList<Prize> prizes = new ArrayList<>();
        ArrayList<Prize> usablePrizes = new ArrayList<>();

        // ================= Blacklist Check ================= //
        if (player.isOp()) {
            usablePrizes.addAll(getPrizes());
        } else {
            for (Prize prize : getPrizes()) {
                if (prize.hasBlacklistPermission(player)) {
                    if (!prize.hasAltPrize()) continue;
                }

                usablePrizes.add(prize);
            }
        }

        // ================= Chance Check ================= //
        checkChance(prizes, usablePrizes);

        try {
            return prizes.get(new Random().nextInt(prizes.size()));
        } catch (IllegalArgumentException e) {
            if (Config.TOGGLE_VERBOSE) {
                loggerUtils.debug("<red>Failed to find prize from the</red> <gold>" + name + "</gold> <red>crate for player</red> <gold>" + player.getName() + ".</gold>");

                e.printStackTrace();
            }

            return null;
        }
    }

    /**
     * Checks the chance of each prize in the arraylists.
     * @param prizes - All the prizes.
     * @param usablePrizes - All the prizes that can be used i.e. they passed the check.
     */
    private void checkChance(ArrayList<Prize> prizes, ArrayList<Prize> usablePrizes) {
        for (int stop = 0; prizes.size() == 0 && stop <= 2000; stop++) {
            for (Prize prize : usablePrizes) {
                int max = prize.getMaxRange();
                int chance = prize.getChance();
                int num;

                for (int counter = 1; counter <= 1; counter++) {
                    num = 1 + new Random().nextInt(max);

                    if (num <= chance) prizes.add(prize);
                }
            }
        }
    }

    /**
     * Picks a random prize based on BlackList Permissions and the Chance System. Only used in the Cosmic Crate Type since it is the only one with tiers.
     * @param player - The player that will be winning the prize.
     * @param tier - The tier you wish the prize to be from.
     * @return The winning prize based on the crate's tiers.
     */
    public Prize pickPrize(Player player, Tier tier) {
        ArrayList<Prize> prizes = new ArrayList<>();
        ArrayList<Prize> usablePrizes = new ArrayList<>();

        // ================= Blacklist Check ================= //
        if (player.isOp()) {
            for (Prize prize : getPrizes()) {
                if (prize.getTiers().contains(tier)) usablePrizes.add(prize);
            }
        } else {
            for (Prize prize : getPrizes()) {
                if (prize.hasBlacklistPermission(player)) {
                    if (!prize.hasAltPrize()) continue;
                }

                if (prize.getTiers().contains(tier)) usablePrizes.add(prize);
            }
        }

        // ================= Chance Check ================= //
        checkChance(prizes, usablePrizes);

        return prizes.get(new Random().nextInt(prizes.size()));
    }
    
    /**
     * Picks a random prize based on BlackList Permissions and the Chance System. Spawns the display item at the location.
     * @param player - The player that will be winning the prize.
     * @param location - The location the firework will spawn at.
     * @return The winning prize.
     */
    public Prize pickPrize(Player player, Location location) {
        Prize prize = pickPrize(player);

        if (prize.useFireworks()) methods.firework(location);

        return prize;
    }
    
    /**
     * @return The name of the crate.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return The name of the crate's preview page.
     */
    public String getPreviewName() {
        return previewName;
    }
    
    /**
     * Check if the inventory the player is in is the preview menu.
     * @param view - The inventory view of the inventory.
     * @return true if it is the preview menu and false if not.
     */
    public boolean isPreview(InventoryView view) {
        return view != null && isPreview(view.getTitle());
    }
    
    /**
     * Check if the inventory the player is in is the preview menu.
     * @param inventoryName - The name of the inventory.
     * @return True if it is the preview menu and false if not.
     */
    public boolean isPreview(String inventoryName) {
        return inventoryName != null && (isInventoryNameSimilar(inventoryName, previewName) || isInventoryNameSimilar(inventoryName, crateInventoryName));
    }
    
    /**
     * Get if the preview is toggled on.
     * @return True if preview is on and false if not.
     */
    public boolean isPreviewEnabled() {
        return previewToggle;
    }
    
    /**
     * Get if the preview has an item boarder.
     * @return True if it does and false if not.
     */
    public boolean isItemBorderEnabled() {
        return borderToggle;
    }
    
    /**
     * Get the item that shows as the preview border if enabled.
     * @return The ItemBuilder for the border item.
     */
    public ItemBuilder getBoarderItem() {
        return boarderItem;
    }
    
    /**
     * Get the name of the inventory the crate will have.
     * @return The name of the inventory for GUI based crate types.
     */
    public String getCrateInventoryName() {
        return crateInventoryName;
    }
    
    /**
     * Check if the inventory the player is in is the crate menu.
     * @param view - The inventory view of the inventory.
     * @return True if it is the crate menu and false if not.
     */
    public boolean isCrateMenu(InventoryView view) {
        return view != null && isCrateMenu(view.getTitle());
    }
    
    /**
     * Check if the inventory the player is in is the crate menu.
     * @param inventoryName - The name of the inventory.
     * @return True if it is the crate menu and false if not.
     */
    public boolean isCrateMenu(String inventoryName) {
        return inventoryName != null && isInventoryNameSimilar(inventoryName, crateInventoryName);
    }
    
    /**
     * Gets the inventory of a preview of prizes for the crate.
     * @return The preview as an Inventory object.
     */
    public Inventory getPreview(Player player) {
        return null;
        //return getPreview(player, previewListener.getPage(player));
    }
    
    /**
     * Gets the inventory of a preview of prizes for the crate.
     * @return The preview as an Inventory object.
     */
    public Inventory getPreview(Player player, int page) {
        //Inventory inventory = player.getServer().createInventory(null, !borderToggle && (previewListener.playerInMenu(player) || maxPage > 1) && maxSlots == 9 ? maxSlots + 9 : maxSlots, previewName);
        //setDefaultItems(inventory, player);

        for (ItemStack item : getPageItems(page)) {
            //int nextSlot = inventory.firstEmpty();

            //if (nextSlot >= 0) {
                //inventory.setItem(nextSlot, item);
            //} else {
            //    break;
            //}
        }

        return null;
    }
    
    /**
     * Gets all the preview items.
     * @return A list of all the preview items.
     */
    @SuppressWarnings("unchecked")
    public ArrayList<ItemStack> getPreviewItems() {
        return (ArrayList<ItemStack>) preview.clone();
    }
    
    /**
     * @return The crate type of the crate.
     */
    public CrateType getCrateType() {
        return this.crateType;
    }
    
    /**
     * @return The key as an item stack.
     */
    public ItemStack getKey() {
        return this.key.clone();
    }
    
    /**
     * @param amount - The amount of keys you want.
     * @return The key as an item stack.
     */
    public ItemStack getKey(int amount) {
        ItemStack key = this.key.clone();
        key.setAmount(amount);

        return key;
    }
    
    /**
     * Get the key that shows in the /crates admin menu.
     * @return The itemstack of the key shown in the /crates admin menu.
     */
    public ItemStack getAdminKey() {
        return adminKey;
    }
    
    /**
     * @return The crates file.
     */
    public FileConfiguration getFile() {
        return this.file;
    }
    
    /**
     * @return The prizes in the crate.
     */
    public ArrayList<Prize> getPrizes() {
        return this.prizes;
    }
    
    /**
     * @param name - Name of the prize you want.
     * @return The prize you asked for.
     */
    public Prize getPrize(String name) {
        for (Prize prize : prizes) {
            if (prize.getName().equalsIgnoreCase(name)) return prize;
        }

        return null;
    }

    /**
     * @param item - Item of the prize you want.
     * @return The prize you asked for.
     */
    public Prize getPrize(ItemStack item) {
        try {
            //NBTItem nbt = new NBTItem(item);

            //if (nbt.hasKey("crazycrate-prize")) return getPrize(nbt.getString("crazycrate-prize"));
        } catch (Exception ignored) {}
        
        for (Prize prize : prizes) {
            if (item.isSimilar(prize.getDisplayItem())) return prize;
        }

        return null;
    }
    
    /**
     * @return True if new players get keys and false if they do not.
     */
    public boolean doNewPlayersGetKeys() {
        return giveNewPlayerKeys;
    }
    
    /**
     * @return The number of keys new players get.
     */
    public int getNewPlayerKeys() {
        return newPlayerKeys;
    }
    
    /**
     * Add a new editor item to a prize in the Crate.
     * @param prize - The prize the item is being added to.
     * @param item - The ItemStack that is being added.
     */
    public void addEditorItem(String prize, ItemStack item) {
        ArrayList<ItemStack> items = new ArrayList<>();
        items.add(item);
        String path = "Crate.Prizes." + prize;

        if (!file.contains(path)) {

            if (item.hasItemMeta()) {
                if (item.getItemMeta().hasDisplayName()) file.set(path + ".DisplayName", item.getItemMeta().getDisplayName());
                if (item.getItemMeta().hasLore()) file.set(path + ".Lore", item.getItemMeta().getLore());
            }

            //NBTItem nbtItem = new NBTItem(item);

            //if (nbtItem.hasNBTData()) {
            //if (nbtItem.hasKey("Unbreakable") && nbtItem.getBoolean("Unbreakable")) file.set(path + ".Unbreakable", true);
            //}

            List<String> enchantments = new ArrayList<>();

            item.getEnchantments().keySet().forEach(enchantment -> enchantments.add((enchantment.getKey().getKey() + ":" + item.getEnchantmentLevel(enchantment))));

            if (!enchantments.isEmpty()) file.set(path + ".DisplayEnchantments", enchantments);

            file.set(path + ".DisplayItem", item.getType().name());
            file.set(path + ".DisplayAmount", item.getAmount());
            file.set(path + ".MaxRange", 100);
            file.set(path + ".Chance", 50);
        } else {
            // Must be checked as getList will return null if nothing is found.
            if (file.contains(path + ".Editor-Items")) file.getList(path + ".Editor-Items").forEach(listItem -> items.add((ItemStack) listItem));
        }

        file.set(path + ".Editor-Items", items);
        fileManager.saveFile(fileManager.getFile(name));
    }
    
    /**
     * @return The max page for the preview.
     */
    public int getMaxPage() {
        return maxPage;
    }
    
    /**
     * @return A list of the tiers for the crate. Will be empty if there are none.
     */
    public ArrayList<Tier> getTiers() {
        return tiers;
    }
    
    /**
     * @return A CrateHologram which contains all the info about the hologram the crate uses.
     */
    public CrateHologram getHologram() {
        return hologram;
    }
    
    public int getAbsoluteItemPosition(int baseSlot) {
        return baseSlot + (previewChestLines > 1 ? previewChestLines - 1 : 1) * 9;
    }
    
    private boolean isInventoryNameSimilar(String inventory1, String inventory2) {
        return methods.removeColor(inventory1).equalsIgnoreCase(methods.removeColor(inventory2));
    }
    
    /**
     * Loads all the preview items and puts them into a list.
     * @return A list of all the preview items that were created.
     */
    private ArrayList<ItemStack> loadPreview() {
        ArrayList<ItemStack> items = new ArrayList<>();

        getPrizes().forEach(prize -> items.add(prize.getDisplayItem()));

        return items;
    }

    /**
     * @param page - The page number.
     * @return The array list of items on the pages.
     */
    private List<ItemStack> getPageItems(int page) {
        List<ItemStack> list = preview;
        List<ItemStack> items = new ArrayList<>();

        if (page <= 0) page = 1;

        int max = maxSlots - (borderToggle ? 18 : maxSlots == preview.size() ? 0 : maxSlots != 9 ? 9 : 0);
        int index = page * max - max;
        int endIndex = index >= list.size() ? list.size() - 1 : index + max;

        for (; index < endIndex; index++) {
            if (index < list.size()) items.add(list.get(index));
        }

        for (; items.isEmpty(); page--) {
            if (page <= 0) break;
            index = page * max - max;
            endIndex = index >= list.size() ? list.size() - 1 : index + max;

            for (; index < endIndex; index++) {
                if (index < list.size()) items.add(list.get(index));
            }
        }
        return items;
    }

    /**
     * Set default items in the Preview Menu.
     * @param inventory - The inventory the player is opening.
     * @param player - The player.
     */
    private void setDefaultItems(Inventory inventory, Player player) {
        if (borderToggle) {
            List<Integer> borderItems = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8);

            borderItems.forEach(item -> inventory.setItem(item, boarderItem.build()));

            borderItems.replaceAll(this::getAbsoluteItemPosition);

            borderItems.forEach(item -> inventory.setItem(item, boarderItem.build()));
        }

        //int page = previewListener.getPage(player);

        //if (previewListener.playerInMenu(player)) inventory.setItem(getAbsoluteItemPosition(4), previewListener.getMenuButton());

        //if (page == 1) {
        //    if (borderToggle) inventory.setItem(getAbsoluteItemPosition(3), boarderItem.build());
        //} else {
        //    //inventory.setItem(getAbsoluteItemPosition(3), previewListener.getBackButton(player));
       // }

        //if (page == maxPage) {
        //    if (borderToggle) inventory.setItem(getAbsoluteItemPosition(5), boarderItem.build());
        //} else {
        //    inventory.setItem(getAbsoluteItemPosition(5), previewListener.getNextButton(player));
        //}
    }
}