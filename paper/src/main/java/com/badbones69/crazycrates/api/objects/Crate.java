package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.api.builders.types.CrateTierMenu;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.objects.other.ItemBuilder;
import com.badbones69.crazycrates.tasks.crates.effects.SoundEffect;
import org.bukkit.SoundCategory;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.FileManager;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.tasks.crates.other.AbstractCrateManager;
import org.jetbrains.annotations.NotNull;
import com.badbones69.crazycrates.common.crates.CrateHologram;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import com.badbones69.crazycrates.tasks.InventoryManager;
import com.badbones69.crazycrates.api.builders.types.CratePreviewMenu;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.api.utils.MsgUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Crate {
    
    private AbstractCrateManager manager;
    private final String name;
    private final String keyName;
    private final ItemBuilder keyBuilder;
    private final ItemStack keyNoNBT;
    private int maxPage = 1;
    private final int maxSlots;
    private final String previewName;
    private final boolean previewToggle;
    private final boolean borderToggle;
    private final ItemBuilder borderItem;

    private final boolean previewTierToggle;
    private final boolean previewTierBorderToggle;
    private final ItemBuilder previewTierBorderItem;
    private int previewTierCrateRows;
    private final int previewTierMaxSlots;

    private final CrateType crateType;
    private final FileConfiguration file;
    private List<Prize> prizes;
    private final String crateInventoryName;
    private final boolean giveNewPlayerKeys;
    private int previewChestLines;
    private final int newPlayerKeys;
    private List<ItemStack> preview;
    private final List<Tier> tiers;
    private final CrateHologram hologram;

    @NotNull
    private final CrazyCrates plugin = CrazyCrates.get();

    @NotNull
    private final InventoryManager inventoryManager = this.plugin.getCrazyHandler().getInventoryManager();

    @NotNull
    private final FileManager fileManager = this.plugin.getFileManager();

    private final int maxMassOpen;
    private final int requiredKeys;
    private final List<String> prizeMessage;

    /**
     * @param name The name of the crate.
     * @param crateType The crate type of the crate.
     * @param key The key as an item stack.
     * @param prizes The prizes that can be won.
     * @param file The crate file.
     */
    public Crate(String name, String previewName, CrateType crateType, ItemStack key, String keyName, List<Prize> prizes, FileConfiguration file, int newPlayerKeys, List<Tier> tiers, int maxMassOpen, int requiredKeys, List<String> prizeMessage, CrateHologram hologram) {
        this.keyBuilder = ItemBuilder.convertItemStack(key).setCrateName(name);
        this.keyNoNBT = this.keyBuilder.build();
        this.keyName = keyName;

        this.file = file;
        this.name = name;
        this.tiers = tiers != null ? tiers : new ArrayList<>();
        this.maxMassOpen = maxMassOpen;
        this.requiredKeys = requiredKeys;
        this.prizeMessage = prizeMessage;
        this.prizes = prizes;
        this.crateType = crateType;
        this.preview = getPreviewItems();
        this.previewToggle = file != null && file.getBoolean("Crate.Preview.Toggle", false);
        this.borderToggle = file != null && file.getBoolean("Crate.Preview.Glass.Toggle", false);

        this.previewTierToggle = file != null && file.getBoolean("Crate.tier-preview.toggle", false);
        this.previewTierBorderToggle = file != null && file.getBoolean("Crate.tier-preview.glass.toggle", false);

        setPreviewChestLines(file != null ? file.getInt("Crate.Preview.ChestLines", 6) : 6);
        this.previewName = MsgUtils.sanitizeColor(previewName);
        this.newPlayerKeys = newPlayerKeys;
        this.giveNewPlayerKeys = newPlayerKeys > 0;

        this.maxSlots = this.previewChestLines * 9;

        for (int amount = this.preview.size(); amount > this.maxSlots - (this.borderToggle ? 18 : this.maxSlots >= this.preview.size() ? 0 : this.maxSlots != 9 ? 9 : 0); amount -= this.maxSlots - (this.borderToggle ? 18 : this.maxSlots >= this.preview.size() ? 0 : this.maxSlots != 9 ? 9 : 0), this.maxPage++) ;

        this.crateInventoryName = file != null ? MsgUtils.sanitizeColor(file.getString("Crate.CrateName")) : "";

        String borderName = file != null && file.contains("Crate.Preview.Glass.Name") ? MsgUtils.color(file.getString("Crate.Preview.Glass.Name")) : " ";
        this.borderItem = file != null && file.contains("Crate.Preview.Glass.Item") ? new ItemBuilder().setMaterial(file.getString("Crate.Preview.Glass.Item", "GRAY_STAINED_GLASS_PANE")).setName(borderName) : new ItemBuilder().setMaterial(Material.AIR).setName(borderName);

        String previewTierBorderName = file != null ? MsgUtils.color(file.getString("Crate.tier-preview.glass.name", " ")) : " ";
        this.previewTierBorderItem = file != null ? new ItemBuilder().setMaterial(file.getString("Crate.tier-preview.glass.item", "GRAY_STAINED_GLASS_PANE")).setName(previewTierBorderName) : new ItemBuilder().setMaterial(Material.AIR).setName(previewTierBorderName);

        setTierPreviewRows(file != null ? file.getInt("Crate.tier-preview.rows", 5) : 5);
        this.previewTierMaxSlots = this.previewTierCrateRows * 9;

        this.hologram = hologram != null ? hologram : new CrateHologram();

        if (crateType == CrateType.cosmic) {
            if (this.file != null) this.manager = new CosmicCrateManager(this.file);
        }
    }

    /**
     * @return the key name.
     */
    public String getKeyName() {
        return this.keyName;
    }

    /**
     * @return true or false if the border for the preview tier is toggled.
     */
    public boolean isPreviewTierBorderToggle() {
        return this.previewTierBorderToggle;
    }

    /**
     * @return true or false if the border for the tier is toggled.
     */
    public boolean isPreviewTierToggle() {
        return this.previewTierToggle;
    }

    /**
     * @return item for the preview border.
     */
    public ItemBuilder getPreviewTierBorderItem() {
        return this.previewTierBorderItem;
    }

    /**
     * Get the crate manager which contains all the settings for that crate type.
     */
    public AbstractCrateManager getManager() {
        return this.manager;
    }
    
    /**
     * Set the preview lines for a Crate.
     *
     * @param amount the amount of lines the preview has.
     */
    public void setPreviewChestLines(int amount) {
        int finalAmount;

        if (amount < 3 && this.borderToggle) {
            finalAmount = 3;
        } else finalAmount = Math.min(amount, 6);

        this.previewChestLines = finalAmount;
    }

    /**
     * Set the preview lines for a Crate.
     *
     * @param amount the amount of lines the preview has.
     */
    public void setTierPreviewRows(int amount) {
        int finalAmount;

        if (amount < 3 && this.borderToggle) {
            finalAmount = 3;
        } else finalAmount = Math.min(amount, 6);

        this.previewTierCrateRows = finalAmount;
    }
    
    /**
     * Get the amount of lines the preview will show.
     *
     * @return the amount of lines it is set to show.
     */
    public int getPreviewChestLines() {
        return this.previewChestLines;
    }
    
    /**
     * Get the max amount of slots in the preview.
     *
     * @return the max number of slots in the preview.
     */
    public int getMaxSlots() {
        return this.maxSlots;
    }
    
    /**
     * Check to see if a player can win a prize from a crate.
     *
     * @param player the player you are checking.
     * @return true if they can win at least 1 prize and false if they can't win any.
     */
    public boolean canWinPrizes(Player player) {
        return pickPrize(player) != null;
    }

    public List<String> getPrizeMessage() {
        return this.prizeMessage;
    }

    /**
     * Picks a random prize based on BlackList Permissions and the Chance System.
     *
     * @param player the player that will be winning the prize.
     * @return the winning prize.
     */
    public Prize pickPrize(Player player) {
        List<Prize> prizes = new ArrayList<>();
        List<Prize> usablePrizes = new ArrayList<>();

        // ================= Blacklist Check ================= //
        if (player.isOp()) {
            usablePrizes.addAll(getPrizes());
        } else {
            for (Prize prize : getPrizes()) {
                if (prize.hasPermission(player)) {
                    if (prize.hasAlternativePrize()) continue;
                }

                usablePrizes.add(prize);
            }
        }

        // ================= Chance Check ================= //
        chanceCheck(prizes, usablePrizes);

        try {
            return prizes.get(MiscUtils.useOtherRandom() ? ThreadLocalRandom.current().nextInt(prizes.size()) : new Random().nextInt(prizes.size()));
        } catch (IllegalArgumentException exception) {
            this.plugin.getLogger().log(Level.WARNING, "Failed to find prize from the " + name + " crate for player " + player.getName() + ".", exception);
            return null;
        }
    }

    /**
     * Checks the chances and returns usable prizes.
     *
     * @param prizes The prizes to check
     * @param usablePrizes The usable prizes to check
     */
    private void chanceCheck(List<Prize> prizes, List<Prize> usablePrizes) {
        for (int stop = 0; prizes.isEmpty() && stop <= 2000; stop++) {
            for (Prize prize : usablePrizes) {
                int max = prize.getMaxRange();
                int chance = prize.getChance();
                int num;

                for (int counter = 1; counter <= 1; counter++) {
                    num = MiscUtils.useOtherRandom() ? 1 + ThreadLocalRandom.current().nextInt(max) : 1 + new Random().nextInt(max);

                    if (num <= chance) prizes.add(prize);
                }
            }
        }
    }

    /**
     * Overrides the current prize pool.
     *
     * @param prizes list of prizes
     */
    public void setPrize(List<Prize> prizes) {
        this.prizes = prizes;
    }

    /**
     * Purge prizes/previews
     */
    public void purge() {
        this.prizes.clear();
        this.preview.clear();
    }

    /**
     * Overrides the preview items.
     *
     * @param itemStacks list of items
     */
    public void setPreviewItems(List<ItemStack> itemStacks) {
        this.preview = itemStacks;
    }

    /**
     * Picks a random prize based on BlackList Permissions and the Chance System. Only used in the Cosmic Crate & Casino Type since it is the only one with tiers.
     *
     * @param player The player that will be winning the prize.
     * @param tier The tier you wish the prize to be from.
     * @return the winning prize based on the crate's tiers.
     */
    public Prize pickPrize(Player player, Tier tier) {
        List<Prize> prizes = new ArrayList<>();
        List<Prize> usablePrizes = new ArrayList<>();

        // ================= Blacklist Check ================= //
        if (player.isOp()) {
            for (Prize prize : getPrizes()) {
                if (prize.getTiers().contains(tier)) usablePrizes.add(prize);
            }
        } else {
            for (Prize prize : getPrizes()) {
                if (prize.hasPermission(player)) {
                    if (prize.hasAlternativePrize()) continue;
                }

                if (prize.getTiers().contains(tier)) usablePrizes.add(prize);
            }
        }

        // ================= Chance Check ================= //
        chanceCheck(prizes, usablePrizes);

        return prizes.get(MiscUtils.useOtherRandom() ? ThreadLocalRandom.current().nextInt(prizes.size()) : new Random().nextInt(prizes.size()));
    }
    
    /**
     * Picks a random prize based on BlackList Permissions and the Chance System. Spawns the display item at the location.
     *
     * @param player the player that will be winning the prize.
     * @param location the location the firework will spawn at.
     * @return the winning prize.
     */
    public Prize pickPrize(Player player, Location location) {
        Prize prize = pickPrize(player);

        if (prize.useFireworks()) MiscUtils.spawnFirework(location, null);

        return prize;
    }
    
    /**
     * @return name the name of the crate.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * @return the name of the crate's preview page.
     */
    public String getPreviewName() {
        return this.previewName;
    }
    
    /**
     * Get if the preview is toggled on.
     *
     * @return true if preview is on and false if not.
     */
    public boolean isPreviewEnabled() {
        return this.previewToggle;
    }
    
    /**
     * Get if the preview has an item border.
     *
     * @return true if it does and false if not.
     */
    public boolean isBorderToggle() {
        return this.borderToggle;
    }
    
    /**
     * Get the item that shows as the preview border if enabled.
     *
     * @return the ItemBuilder for the border item.
     */
    public ItemBuilder getBorderItem() {
        return this.borderItem;
    }
    
    /**
     * Get the name of the inventory the crate will have.
     *
     * @return the name of the inventory for GUI based crate types.
     */
    public String getCrateInventoryName() {
        return this.crateInventoryName;
    }
    
    /**
     * Gets the inventory of a preview of prizes for the crate.
     *
     * @return the preview as an Inventory object.
     */
    public Inventory getPreview(Player player) {
        return getPreview(player, this.plugin.getCrazyHandler().getInventoryManager().getPage(player), false, null);
    }
    
    /**
     * Gets the inventory of a preview of prizes for the crate.
     *
     * @return the preview as an Inventory object.
     */
    public Inventory getPreview(Player player, int page, boolean isTier, Tier tier) {
        CratePreviewMenu cratePreviewMenu = new CratePreviewMenu(this, player, !this.borderToggle && (this.inventoryManager.inCratePreview(player) || this.maxPage > 1) && this.maxSlots == 9 ? this.maxSlots + 9 : this.maxSlots, page, this.previewName, isTier, tier);

        return cratePreviewMenu.build().getInventory();
    }

    /**
     * Gets the inventory of a tier preview of prizes for the crate.
     *
     * @return the tier preview as an Inventory object.
     */
    public Inventory getTierPreview(Player player) {
        CrateTierMenu crateTierMenu = new CrateTierMenu(getTiers(), this, player, !this.previewTierBorderToggle && (this.inventoryManager.inCratePreview(player)) && this.previewTierMaxSlots == 9 ? this.previewTierMaxSlots + 9 : this.previewTierMaxSlots, this.previewName);

        return crateTierMenu.build().getInventory();
    }
    
    /**
     * @return the crate type of the crate.
     */
    public CrateType getCrateType() {
        return this.crateType;
    }
    
    /**
     * @return the key as an item stack.
     */
    public ItemStack getKey() {
        return this.keyBuilder.build();
    }

    /**
     * @param player The player getting the key.
     *
     * @return the key as an item stack.
     */
    public ItemStack getKey(Player player) {
        return this.keyBuilder.setTarget(player).build();
    }

    /**
     * @param amount The amount of keys you want.
     * @return the key as an item stack.
     */
    public ItemStack getKey(int amount) {
        ItemBuilder key = this.keyBuilder.setAmount(amount);

        return key.build();
    }
    
    /**
     * @param amount The amount of keys you want.
     * @param player The player getting the key.
     *
     * @return the key as an item stack.
     */
    public ItemStack getKey(int amount, Player player) {
        ItemBuilder key = this.keyBuilder.setTarget(player).setAmount(amount);

        return key.build();
    }
    
    /**
     * @return the key as an item stack with no nbt tags.
     */
    public ItemStack getKeyNoNBT() {
        return this.keyNoNBT.clone();
    }
    
    /**
     * @param amount the amount of keys you want.
     *
     * @return the key as an item stack with no nbt tags.
     */
    public ItemStack getKeyNoNBT(int amount) {
        ItemStack key = this.keyNoNBT.clone();

        key.setAmount(amount);

        return key;
    }
    
    /**
     * @return the crates file.
     */
    public FileConfiguration getFile() {
        return this.file;
    }
    
    /**
     * @return the prizes in the crate.
     */
    public List<Prize> getPrizes() {
        return this.prizes;
    }
    
    /**
     * @param name name of the prize you want.
     * @return the prize you asked for.
     */
    public Prize getPrize(String name) {
        for (Prize prize : this.prizes) {
            if (prize.getPrizeName().equalsIgnoreCase(name)) return prize;
        }

        return null;
    }
    
    public Prize getPrize(ItemStack item) {
        ItemMeta itemMeta = item.getItemMeta();

        PersistentDataContainer container = itemMeta.getPersistentDataContainer();

        return getPrize(container.get(PersistentKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING));
    }
    
    /**
     * @return true if new players get keys and false if they do not.
     */
    public boolean doNewPlayersGetKeys() {
        return this.giveNewPlayerKeys;
    }
    
    /**
     * @return the number of keys new players get.
     */
    public int getNewPlayerKeys() {
        return this.newPlayerKeys;
    }
    
    /**
     * Add a new editor item to a prize in the crate.
     *
     * @param prize the prize the item is being added to.
     * @param item the ItemStack that is being added.
     */
    public void addEditorItem(String prize, ItemStack item, int chance) {
        List<ItemStack> items = new ArrayList<>();
        items.add(item);

        String path = "Crate.Prizes." + prize;

        if (!this.file.contains(path)) {
            setItem(item, chance, path);
        } else {
            // Must be checked as getList will return null if nothing is found.
            if (this.file.contains(path + ".Editor-Items")) this.file.getList(path + ".Editor-Items").forEach(listItem -> items.add((ItemStack) listItem));
        }

        saveFile(items, path);
    }

    /**
     * Sets display item to config
     *
     * @param item the item to use
     * @param chance the chance to win the item
     * @param path the path in the config to set the item at.
     */
    private void setItem(ItemStack item, int chance, String path) {
        ItemMeta itemMeta = item.getItemMeta();

        if (itemMeta.hasDisplayName()) this.file.set(path + ".DisplayName", item.getItemMeta().getDisplayName());
        if (itemMeta.hasLore()) this.file.set(path + ".Lore", item.getItemMeta().getLore());

        this.file.set(path + ".Unbreakable", itemMeta.isUnbreakable());

        List<String> enchantments = new ArrayList<>();

        for (Enchantment enchantment : item.getEnchantments().keySet()) {
            enchantments.add((enchantment.getKey().getKey() + ":" + item.getEnchantmentLevel(enchantment)));
        }

        if (!enchantments.isEmpty()) this.file.set(path + ".DisplayEnchantments", enchantments);

        this.file.set(path + ".DisplayItem", item.getType().name());
        this.file.set(path + ".DisplayAmount", item.getAmount());
        this.file.set(path + ".MaxRange", 100);
        this.file.set(path + ".Chance", chance);
    }

    /**
     * Saves item stacks to editor-items
     *
     * @param items list of items
     * @param path the path in the config.
     */
    private void saveFile(List<ItemStack> items, String path) {
        this.file.set(path + ".Editor-Items", items);

        File crates = new File(this.plugin.getDataFolder(), "crates");

        File crateFile = new File(crates, this.name + ".yml");

        try {
            this.file.save(crateFile);
        } catch (IOException exception) {
            this.plugin.getLogger().log(Level.SEVERE, "Failed to save " + this.name + ".yml", exception);
        }

        this.fileManager.getFile(this.name).reloadFile();

        this.plugin.getCrateManager().reloadCrate(this.plugin.getCrateManager().getCrateFromName(this.name));
    }

    /**
     * Add a new editor item to a prize in the crate.
     *
     * @param prize the prize the item is being added to.
     * @param item the ItemStack that is being added.
     * @param tier the tier for the crate.
     */
    public void addEditorItem(String prize, ItemStack item, Tier tier, int chance) {
        List<ItemStack> items = new ArrayList<>();
        items.add(item);

        String path = "Crate.Prizes." + prize;

        if (!this.file.contains(path)) {
            setItem(item, chance, path);

            this.file.set(path + ".Tiers", new ArrayList<>() {{
                add(tier.getName());
            }});
        } else {
            // Must be checked as getList will return null if nothing is found.
            if (this.file.contains(path + ".Editor-Items")) this.file.getList(path + ".Editor-Items").forEach(listItem -> items.add((ItemStack) listItem));
        }

        saveFile(items, path);
    }
    
    /**
     * @return the max page for the preview.
     */
    public int getMaxPage() {
        return this.maxPage;
    }
    
    /**
     * @return a list of the tiers for the crate. Will be empty if there are none.
     */
    public List<Tier> getTiers() {
        return this.tiers;
    }

    /**
     * @param name name of the tier.
     * @return the tier object.
     */
    public Tier getTier(String name) {
        for (Tier tier : this.tiers) {
            if (tier.getName().equalsIgnoreCase(name)) {
                return tier;
            }
        }

        return null;
    }

    /**
     * @return returns the max amount that players can specify for crate mass open.
     */
    public int getMaxMassOpen() {
        return this.maxMassOpen;
    }

    /**
     * @return the amount of required keys.
     */
    public int getRequiredKeys() {
        return this.requiredKeys;
    }

    /**
     * @return a list of item stacks
     */
    public List<ItemStack> getPreview() {
        return this.preview;
    }

    /**
     * @return a CrateHologram which contains all the info about the hologram the crate uses.
     */
    public CrateHologram getHologram() {
        return this.hologram;
    }

    /**
     * @param baseSlot - default slot to use.
     * @return the finalized slot.
     */
    public int getAbsoluteItemPosition(int baseSlot) {
        return baseSlot + (this.previewChestLines > 1 ? this.previewChestLines - 1 : 1) * 9;
    }

    /**
     * @param baseSlot - default slot to use.
     * @return the finalized slot.
     */
    public int getAbsolutePreviewItemPosition(int baseSlot) {
        return baseSlot + (this.previewTierCrateRows > 1 ? this.previewTierCrateRows - 1 : 1) * 9;
    }

    /**
     * Loads all the preview items and puts them into a list.
     *
     * @return a list of all the preview items that were created.
     */
    public List<ItemStack> getPreviewItems() {
        List<ItemStack> items = new ArrayList<>();

        for (Prize prize : getPrizes()) {
            items.add(prize.getDisplayItem());
        }

        return items;
    }
    
    /**
     * Loads all the preview items and puts them into a list.
     *
     * @return a list of all the preview items that were created.
     */
    public List<ItemStack> getPreviewItems(Player player) {
        List<ItemStack> items = new ArrayList<>();

        for (Prize prize : getPrizes()) {
            items.add(prize.getDisplayItem(player));
        }

        return items;
    }

    /**
     * Get prizes for tier specific preview gui's
     *
     * @param tier The tier to check
     * @return list of prizes
     */
    public List<ItemStack> getPreviewItems(Tier tier, Player player) {
        List<ItemStack> prizes = new ArrayList<>();

        for (Prize prize : getPrizes()) {
            if (prize.getTiers().contains(tier)) {
                prizes.add(prize.getDisplayItem(player));
            }
        }

        return prizes;
    }

    /**
     * Plays a sound at different volume levels with fallbacks.
     *
     * @param type i.e. stop, cycle or click sound.
     * @param category sound category to respect client settings.
     * @param fallback fallback sound in case no sound is found.
     */
    public void playSound(Player player, Location location, String type, String fallback, SoundCategory category) {
        ConfigurationSection section = getFile().getConfigurationSection("Crate.sound");

        if (section != null) {
            SoundEffect sound = new SoundEffect(
                    section,
                    type,
                    fallback,
                    category
            );

            sound.play(player, location);
        }
    }
}