package com.badbones69.crazycrates.api.objects;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.tasks.menus.CratePreviewMenu;
import com.badbones69.crazycrates.tasks.menus.CrateTierMenu;
import com.badbones69.crazycrates.api.objects.crates.CrateHologram;
import com.badbones69.crazycrates.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.managers.BukkitUserManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.badbones69.crazycrates.tasks.crates.effects.SoundEffect;
import com.badbones69.crazycrates.api.builders.ItemBuilder;
import com.ryderbelserion.api.enums.FileType;
import com.ryderbelserion.paper.files.CustomFile;
import com.ryderbelserion.paper.util.PaperMethods;
import com.ryderbelserion.util.Methods;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.tasks.crates.other.CosmicCrateManager;
import com.badbones69.crazycrates.tasks.crates.other.AbstractCrateManager;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import com.badbones69.crazycrates.utils.MiscUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Crate {

    private ItemBuilder previewTierBorderItem;
    private ItemBuilder borderItem;
    private ItemBuilder keyBuilder;

    private AbstractCrateManager manager;
    private final String name;
    private String keyName;
    private int maxSlots;
    private String previewName;
    private boolean previewToggle;
    private boolean borderToggle;

    private boolean previewTierToggle;
    private boolean previewTierBorderToggle;
    private int previewTierCrateRows;

    private Color color;
    private Particle particle;

    private final CrateType crateType;
    private YamlConfiguration file;
    private ArrayList<Prize> prizes;
    private String crateName;
    private boolean giveNewPlayerKeys;
    private int previewChestLines;
    private int newPlayerKeys;

    private List<Tier> tiers;
    private CrateHologram hologram;

    private int maxMassOpen;
    private int requiredKeys;

    private boolean cyclePrize;

    private boolean cyclePersistRestart;
    private boolean cyclePermissionToggle;
    private int cyclePermissionCap;

    private List<String> prizeMessage = new ArrayList<>();

    private List<String> prizeCommands = new ArrayList<>();

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    private final SettingsManager config = ConfigManager.getConfig();

    private boolean glassBorderToggle = true;

    private boolean broadcastToggle = false;
    private List<String> broadcastMessages = new ArrayList<>();
    private String broadcastPermission = "";

    private double sum = 0;
    private double tierSum = 0;

    /**
     * @param name The name of the crate.
     * @param crateType The crate type of the crate.
     * @param key The key as an item stack.
     * @param prizes The prizes that can be won.
     * @param file The crate file.
     */
    public Crate(@NotNull final String name,
                 @NotNull final String previewName,
                 @NotNull final CrateType crateType,
                 @NotNull final ItemBuilder key,
                 @NotNull final String keyName,
                 @NotNull final ArrayList<Prize> prizes,
                 @NotNull final YamlConfiguration file,
                 final int newPlayerKeys,
                 @NotNull final List<Tier> tiers,
                 final int maxMassOpen,
                 final int requiredKeys,
                 @NotNull final List<String> prizeMessage,
                 @NotNull final List<String> prizeCommands,
                 @NotNull final CrateHologram hologram) {
        this.keyBuilder = key.setDisplayName(keyName).setPersistentString(ItemKeys.crate_key.getNamespacedKey(), name);
        this.keyName = keyName;

        this.file = file;
        this.name = name;
        this.tiers = tiers;
        this.maxMassOpen = maxMassOpen;
        this.requiredKeys = requiredKeys;
        this.prizeMessage = prizeMessage;
        this.prizeCommands = prizeCommands;

        this.glassBorderToggle = this.file.getBoolean("Crate.Settings.Border.Glass-Border.Toggle", this.glassBorderToggle);

        this.broadcastToggle = this.file.getBoolean("Crate.Settings.Broadcast.Toggle", false);
        this.broadcastMessages = this.file.getStringList("Crate.Settings.Broadcast.Messages");
        this.broadcastPermission = this.file.getString("Crate.Settings.Broadcast.Permission", "");

        this.cyclePrize = this.file.getBoolean("Crate.Settings.Rewards.Re-Roll-Spin", false);

        this.cyclePermissionToggle = this.file.getBoolean("Crate.Settings.Rewards.Permission.Toggle", false);
        this.cyclePersistRestart = this.file.getBoolean("Crate.Settings.Rewards.Permission.Persist", false);
        this.cyclePermissionCap = this.file.getInt("Crate.Settings.Rewards.Permission.Max-Cap", 20);

        for (int node = 1; node <= this.cyclePermissionCap; node++) {
            if (this.cyclePermissionToggle) {
                MiscUtils.registerPermission("crazycrates.respin." + this.name + "." + node, "Allows you to open the crate " + this.name + node + " amount of times.", false);
            } else {
                MiscUtils.unregisterPermission("crazycrates.respin." + this.name + "." + node);
            }
        }

        if (this.broadcastToggle && !this.broadcastPermission.isEmpty()) {
            MiscUtils.registerPermission(this.broadcastPermission, "Hides the broadcast message if a player has this permission", false);
        } else if (!this.broadcastToggle && !this.broadcastPermission.isEmpty()) {
            MiscUtils.unregisterPermission(this.broadcastPermission);
        }

        this.prizes = prizes;

        this.sum = this.prizes.stream().filter(prize -> prize.getWeight() != -1).mapToDouble(Prize::getWeight).sum();

        this.crateType = crateType;
        this.previewToggle = file.getBoolean("Crate.Preview.Toggle", false);
        this.borderToggle = file.getBoolean("Crate.Preview.Glass.Toggle", false);

        this.previewTierToggle = file.getBoolean("Crate.tier-preview.toggle", false);
        this.previewTierBorderToggle = file.getBoolean("Crate.tier-preview.glass.toggle", false);

        this.previewName = previewName;
        this.newPlayerKeys = newPlayerKeys;
        this.giveNewPlayerKeys = newPlayerKeys > 0;

        setPreviewChestLines(file.getInt("Crate.Preview.ChestLines", 6));
        this.maxSlots = this.previewChestLines * 9;

        this.crateName = file.contains("Crate.CrateName") ? file.getString("Crate.CrateName", " ") : file.getString("Crate.Name", " ");

        @NotNull final String borderName = file.getString("Crate.Preview.Glass.Name", " ");

        this.borderItem = new ItemBuilder()
                .withType(file.getString("Crate.Preview.Glass.Item", "gray_stained_glass_pane").toLowerCase())
                .setCustomModelData(file.getInt("Crate.Preview.Glass.Custom-Model-Data", -1))
                .setHidingItemFlags(file.getBoolean("Crate.Preview.Glass.HideItemFlags", false))
                .setDisplayName(borderName);

        @NotNull final String previewTierBorderName = file.getString("Crate.tier-preview.glass.name", " ");

        this.previewTierBorderItem = new ItemBuilder()
                .withType(file.getString("Crate.tier-preview.glass.item", "gray_stained_glass_pane").toLowerCase())
                .setCustomModelData(file.getInt("Crate.tier-preview.glass.custom-model-data", -1))
                .setHidingItemFlags(file.getBoolean("Crate.tier-preview.glass.hideitemflags", false))
                .setDisplayName(previewTierBorderName);

        setTierPreviewRows(file.getInt("Crate.tier-preview.rows", 5));

        if (this.crateType == CrateType.quad_crate) {
            this.particle = PaperMethods.getParticleType(file.getString("Crate.particles.type", "dust"));

            this.color = PaperMethods.getColor(file.getString("Crate.particles.color", "235,64,52"));
        }

        this.hologram = hologram;

        switch (this.crateType) {
            case cosmic -> {
                if (this.file != null) this.manager = new CosmicCrateManager(this.file);

                this.tierSum = this.tiers.stream().filter(tier -> tier.getWeight() != -1).mapToDouble(Tier::getWeight).sum();
            }

            case casino -> {
                this.tierSum = this.tiers.stream().filter(tier -> tier.getWeight() != -1).mapToDouble(Tier::getWeight).sum();
            }
        }
    }

    public Crate(@NotNull final String name) {
        this.crateType = CrateType.menu;
        this.name = name;
    }

    public Color getColor() {
        return this.color;
    }

    public Particle getParticle() {
        return this.particle;
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
    public final boolean isPreviewTierBorderToggle() {
        return this.previewTierBorderToggle;
    }

    /**
     * @return true or false if the border for the tier is toggled.
     */
    public final boolean isPreviewTierToggle() {
        return this.previewTierToggle;
    }

    /**
     * @return item for the preview border.
     */
    public @NotNull final ItemBuilder getPreviewTierBorderItem() {
        return this.previewTierBorderItem;
    }

    /**
     * Get the crate manager which contains all the settings for that crate type.
     */
    public @NotNull final AbstractCrateManager getManager() {
        return this.manager;
    }

    public final boolean isGlassBorderToggled() {
        return this.glassBorderToggle;
    }

    public final boolean isBroadcastToggled() {
        return this.broadcastToggle;
    }

    @Deprecated(forRemoval = true, since = "4.2.0")
    public final boolean isBroadcastToggle() {
        return isBroadcastToggled();
    }

    public final String getBroadcastPermission() {
        return this.broadcastPermission;
    }

    public final List<String> getBroadcastMessages() {
        return this.broadcastMessages;
    }

    /**
     * Set the preview lines for a Crate.
     *
     * @param amount the amount of lines the preview has.
     */
    public void setPreviewChestLines(final int amount) {
        int finalAmount;

        if (this.borderToggle && amount < 3) {
            finalAmount = 3;
        } else finalAmount = Math.min(amount, 6);

        this.previewChestLines = finalAmount;
    }

    /**
     * Set the preview lines for a Crate.
     *
     * @param amount the amount of lines the preview has.
     */
    public void setTierPreviewRows(final int amount) {
        this.previewTierCrateRows = amount;
    }

    public final int getPreviewTierCrateRows() {
        return this.previewTierCrateRows;
    }

    /**
     * Get the amount of lines the preview will show.
     *
     * @return the amount of lines it is set to show.
     */
    public final int getPreviewChestLines() {
        return this.previewChestLines;
    }
    
    /**
     * Get the max amount of slots in the preview.
     *
     * @return the max number of slots in the preview.
     */
    public final int getMaxSlots() {
        return this.maxSlots;
    }
    
    /**
     * Check to see if a player can win a prize from a crate.
     *
     * @param player the player you are checking.
     * @return true if they can win at least 1 prize and false if they can't win any.
     */
    public final boolean canWinPrizes(@NotNull final Player player) {
        return pickPrize(player) != null;
    }

    public @NotNull final List<String> getPrizeMessage() {
        return this.prizeMessage;
    }

    public @NotNull final List<String> getPrizeCommands() {
        return this.prizeCommands;
    }

    /**
     * Picks a random prize based on BlackList Permissions and the Chance System.
     *
     * @param player the player that will be winning the prize.
     * @return {@link Prize}
     */
    public Prize pickPrize(@NotNull final Player player) {
        final List<Prize> prizes = new ArrayList<>();

        for (Prize prize : getPrizes()) {
            if (prize.getWeight() == -1) continue;
          
            final int pulls = PrizeManager.getCurrentPulls(prize, this);

             if (pulls != 0) {
                if (pulls >= prize.getMaxPulls()) continue;
            }

            if (prize.hasPermission(player) && !player.isOp()) {
                if (prize.hasAlternativePrize()) continue;
            }

            prizes.add(prize);
        }

        return getPrize(prizes);
    }

    /**
     * Picks a random prize based on BlackList Permissions and the Chance System. Only used in the Cosmic Crate and Casino Type since it is the only one with tiers.
     *
     * @param player The player that will be winning the prize.
     * @param tier The tier you wish the prize to be from.
     * @return the winning prize based on the crate's tiers.
     */
    public final Prize pickPrize(@NotNull final Player player, @NotNull final Tier tier) {
        final List<Prize> prizes = new ArrayList<>();

        for (final Prize prize : getPrizes()) {
            if (prize.hasPermission(player) && !player.isOp()) {
                if (prize.hasAlternativePrize()) continue;
            }

            if (prize.getTiers().contains(tier)) prizes.add(prize);
        }

        return getPrize(prizes);
    }

    /**
     * Checks the chances and returns usable prizes.
     *
     * @param prizes The prizes to check
     * @return {@link Prize}
     */
    private Prize getPrize(@NotNull final List<Prize> prizes) {
        double totalWeight = this.crateType == CrateType.casino || this.crateType == CrateType.cosmic ? prizes.stream().mapToDouble(Prize::getWeight).sum() : this.sum;

        int index = 0;

        for (double value = MiscUtils.getRandom().nextDouble() * totalWeight; index < prizes.size() - 1; index++) {
            value -= prizes.get(index).getWeight();

            if (value < 0.0) break;
        }

        return prizes.get(index);
    }

    /**
     * Gets the chance of the prize.
     *
     * @param weight the weight out of the sum
     * @return the chance
     */
    public double getChance(final double weight) {
        return (weight / this.sum) * 100D;
    }

    /**
     * Gets the chance of the tier.
     *
     * @param weight the weight out of the sum
     * @return the chance
     */
    public double getTierChance(final double weight) {
        return (weight / this.tierSum) * 100D;
    }

    /**
     * Overrides the current prize pool.
     *
     * @param prizes list of prizes
     */
    public void setPrize(@NotNull final List<Prize> prizes) {
        // Purge everything for this crate.
        purge();

        // Add new prizes.
        this.prizes.addAll(prizes.stream().filter(prize -> prize.getWeight() != -1).toList());
    }

    /**
     * Purge prizes/previews
     */
    public void purge() {
        this.prizes.clear();
    }

    public final boolean isCyclePermissionToggle() {
        return this.cyclePermissionToggle;
    }

    public final int getCyclePermissionCap() {
        return this.cyclePermissionCap;
    }

    /**
     * @return name file name.
     */
    public @NotNull final String getFileName() {
        return this.name;
    }
    
    /**
     * @return the name of the crate's preview page.
     */
    public @NotNull final String getPreviewName() {
        return this.previewName;
    }
    
    /**
     * Get if the preview is toggled on.
     *
     * @return true if preview is on and false if not.
     */
    public final boolean isPreviewEnabled() {
        return this.previewToggle;
    }
    
    /**
     * Get if the preview has an item border.
     *
     * @return true if it does and false if not.
     */
    public final boolean isBorderToggle() {
        return this.borderToggle;
    }
    
    /**
     * Get the item that shows as the preview border if enabled.
     *
     * @return the ItemBuilder for the border item.
     */
    public @NotNull final ItemBuilder getBorderItem() {
        return this.borderItem;
    }
    
    /**
     * Get the name of the inventory the crate will have.
     *
     * @return the name of the inventory for GUI based crate types.
     */
    public @NotNull final String getCrateName() {
        return this.crateName;
    }
    
    /**
     * Gets the inventory of a preview of prizes for the crate.
     *
     * @return the preview as an Inventory object.
     */
    public final CratePreviewMenu getPreview(final Player player) {
        return getPreview(player, null);
    }
    
    /**
     * Gets the inventory of a preview of prizes for the crate.
     *
     * @return the preview as an Inventory object.
     */
    public final CratePreviewMenu getPreview(final Player player, final @Nullable Tier tier) {
        return new CratePreviewMenu(player, this, tier);
    }

    /**
     * Gets the inventory of a tier preview of prizes for the crate.
     *
     * @return the tier preview as an Inventory object.
     */
    public final CrateTierMenu getTierPreview(final Player player) {
        return new CrateTierMenu(player, this);
    }
    
    /**
     * @return the crate type of the crate.
     */
    public final CrateType getCrateType() {
        return this.crateType;
    }
    
    /**
     * @return the key as an item stack.
     */
    public @NotNull final ItemStack getKey() {
        return this.keyBuilder.asItemStack();
    }

    /**
     * @param player The player getting the key.
     * @return the key as an item stack.
     */
    public @NotNull final ItemStack getKey(Player player) {
        return this.userManager.addPlaceholders(this.keyBuilder.setPlayer(player), this).asItemStack();
    }

    /**
     * @param amount The amount of keys you want.
     * @return the key as an item stack.
     */
    public @NotNull final ItemStack getKey(int amount) {
        return this.keyBuilder.setAmount(amount).asItemStack();
    }
    
    /**
     * @param amount The amount of keys you want.
     * @param player The player getting the key.
     * @return the key as an item stack.
     */
    public @NotNull final ItemStack getKey(int amount, Player player) {
        return this.userManager.addPlaceholders(this.keyBuilder.setPlayer(player), this).setAmount(amount).asItemStack();
    }

    /**
     * @return the crates file.
     */
    public @NotNull final YamlConfiguration getFile() {
        return this.file;
    }
    
    /**
     * @return the prizes in the crate.
     */
    public @NotNull final List<Prize> getPrizes() {
        return this.prizes;
    }

    /**
     * Gets the total chance of the prizes weights added up
     *
     * @return the total chance added up
     */
    public final double getSum() {
        return this.sum;
    }

    /**
     * @param name name of the prize you want.
     * @return the prize you asked for.
     */
    public final @Nullable Prize getPrize(@NotNull final String name) {
        if (name.isEmpty()) return null;

        Prize prize = null;

        for (final Prize key : this.prizes) {
            if (!key.getSectionName().equalsIgnoreCase(name)) continue;

            prize = key;

            break;
        }

        return prize;
    }
    
    public final @Nullable Prize getPrize(@NotNull final ItemStack item) {
        return getPrize(item.getPersistentDataContainer().getOrDefault(ItemKeys.crate_prize.getNamespacedKey(), PersistentDataType.STRING, ""));
    }
    
    /**
     * @return true if new players get keys and false if they do not.
     */
    public final boolean doNewPlayersGetKeys() {
        return this.giveNewPlayerKeys;
    }
    
    /**
     * @return the number of keys new players get.
     */
    public final int getNewPlayerKeys() {
        return this.newPlayerKeys;
    }

    /**
     * Add a new editor item to a prize in the crate.
     *
     * @param itemStack the itemstack to add.
     * @param prizeName the name of the prize.
     * @param weight the chance to add.
     */
    public void addEditorItem(@Nullable final ItemStack itemStack, @NotNull final String prizeName, final double weight) {
        if (itemStack == null || prizeName.isEmpty() || weight <= 0) return;

        ConfigurationSection section = getPrizeSection();

        if (section == null) return;

        setItem(itemStack, prizeName, section, weight, "");
    }

    /**
     * Add a new editor item to a prize in the crate.
     *
     * @param itemStack the itemstack to add.
     * @param prizeName the name of the prize.
     * @param tier the tier to add.
     * @param weight the chance to add.
     */
    public void addEditorItem(@Nullable final ItemStack itemStack, @NotNull final String prizeName, @NotNull final String tier, final double weight) {
        if (tier.isEmpty() || prizeName.isEmpty() || weight <= 0 || itemStack == null) return;

        final ConfigurationSection section = getPrizeSection();

        if (section == null) return;

        setItem(itemStack, prizeName, section, weight, tier);
    }

    /**
     * @return the configuration section.
     */
    public @Nullable final ConfigurationSection getPrizeSection() {
        final ConfigurationSection section = this.file.getConfigurationSection("Crate");

        if (section == null) return null;

        return section.getConfigurationSection("Prizes");
    }

    /**
     * Adds an item to the config to display in the crate.
     *
     * @param itemStack the itemstack to set.
     * @param prizeName the prize name.
     * @param section the prizes section.
     * @param weight the chance of the prize.
     */
    private void setItem(@Nullable final ItemStack itemStack, @NotNull final String prizeName, @Nullable final ConfigurationSection section, final double weight, final String tier) {
        if (prizeName.isEmpty() || section == null || weight <= 0 || itemStack == null) return;

        final String tiers = getPath(prizeName, "Tiers");

        if (itemStack.hasItemMeta()) {
            final ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta.hasDisplayName()) {
                final Component displayName = itemMeta.displayName();

                if (displayName != null) {
                    section.set(getPath(prizeName, "DisplayName"), Methods.fromComponent(displayName));
                }
            }

            if (itemMeta.hasLore()) {
                final List<Component> lore = itemMeta.lore();

                if (lore != null) {
                    section.set(getPath(prizeName, "DisplayLore"), Methods.fromComponent(lore));
                }
            }
        }

        if (this.config.getProperty(ConfigKeys.use_new_item_editor)) {
            String toBase64 = PaperMethods.toBase64(itemStack);

            section.set(getPath(prizeName, "DisplayData"), toBase64);

            final String items = getPath(prizeName, "Items");

            if (section.contains(items)) {
                final List<String> list = section.getStringList(items);

                list.add("Data:" + toBase64);

                section.set(items, list);
            } else {
                section.set(items, new ArrayList<>() {{
                    add("Data:" + toBase64);
                }});
            }
        } else {
            final List<ItemStack> editorItems = new ArrayList<>();

            if (section.contains(prizeName + ".Editor-Items")) {
                final List<?> editors = section.getList(prizeName + ".Editor-Items");

                if (editors != null) {
                    editors.forEach(item -> editorItems.add((ItemStack) item));
                }
            }

            editorItems.add(itemStack);

            section.set(getPath(prizeName, "Editor-Items"), editorItems);
        }

        section.set(getPath(prizeName, "DisplayItem"), itemStack.getType().getKey().getKey());

        section.set(getPath(prizeName, "DisplayAmount"), itemStack.getAmount());

        List<String> enchantments = new ArrayList<>();

        for (Map.Entry<Enchantment, Integer> enchantment : itemStack.getEnchantments().entrySet()) {
            enchantments.add(enchantment.getKey().getKey().getKey() + ":" + enchantment.getValue());
        }

        if (!enchantments.isEmpty()) section.set(getPath(prizeName, "DisplayEnchantments"), enchantments);

        section.set(getPath(prizeName, "Weight"), weight);

        // The section already contains a prize name, so we update the tiers.
        if (!tier.isEmpty()) {
            if (section.contains(tiers)) {
                final List<String> list = section.getStringList(tiers);
                list.add(tier);

                section.set(tiers, list);
            } else {
                section.set(tiers, new ArrayList<>() {{
                    add(tier);
                }});
            }
        }

        saveFile();
    }

    private @NotNull String getPath(final String section, final String path) {
        if (section.isEmpty() || path.isEmpty()) return "";

        return section + "." + path;
    }

    /**
     * Saves item stacks to editor-items
     */
    private void saveFile() {
        if (this.name.isEmpty()) return;

        final CustomFile customFile = this.plugin.getFileManager().getFile(this.name, FileType.YAML);

        if (customFile != null) customFile.save();

        this.crateManager.reloadCrate(this.crateManager.getCrateFromName(this.name));
    }
    
    /**
     * @return a list of the tiers for the crate. Will be empty if there are none.
     */
    public @NotNull final List<Tier> getTiers() {
        return this.tiers;
    }

    /**
     * @param name name of the tier.
     * @return the tier object.
     */
    public @Nullable final Tier getTier(@Nullable final String name) {
        if (name == null || name.isEmpty()) return null;

        Tier tier = null;

        for (final Tier key : this.tiers) {
            if (!key.getName().equalsIgnoreCase(name)) continue;

            tier = key;

            break;
        }

        return tier;
    }

    /**
     * @return returns the max amount that players can specify for crate mass open.
     */
    public final int getMaxMassOpen() {
        return this.maxMassOpen;
    }

    /**
     * @return the amount of required keys.
     */
    public final int getRequiredKeys() {
        return this.requiredKeys;
    }

    /**
     * @return a CrateHologram which contains all the info about the hologram the crate uses.
     */
    public @NotNull final CrateHologram getHologram() {
        return this.hologram;
    }

    /**
     * Get prizes for tier specific preview gui's
     *
     * @param tier The tier to check
     * @return list of prizes
     */
    public @NotNull final List<ItemStack> getPreviewItems(@Nullable final Player player, @Nullable final Tier tier) {
        List<ItemStack> prizes = new ArrayList<>();

        for (final Prize prize : getPrizes()) {
            // if (prize.getWeight() == -1) continue;

            if (tier == null) {
                prizes.add(player == null ? prize.getDisplayItem(this) : prize.getDisplayItem(player, this));
            } else {
                if (prize.getTiers().contains(tier)) {
                    prizes.add(player == null ? prize.getDisplayItem(this) : prize.getDisplayItem(player, this));
                }
            }
        }

        return prizes;
    }

    public final boolean isCyclePersistRestart() {
        return this.cyclePersistRestart;
    }

    public final boolean isCyclePrize() {
        return this.cyclePrize;
    }

    public final boolean useRequiredKeys() {
        return ConfigManager.getConfig().getProperty(ConfigKeys.crate_use_required_keys) && this.requiredKeys > 0;
    }

    /**
     * Plays a sound at different volume levels with fallbacks
     *
     * @param type i.e. stop, cycle or click sound
     * @param source sound category to respect client settings
     * @param fallback fallback sound in case no sound is found
     */
    public void playSound(@NotNull final Player player, @NotNull final Location location, @NotNull final String type, @NotNull final String fallback, @NotNull final Sound.Source source) {
        if (type.isEmpty() && fallback.isEmpty()) return;

        ConfigurationSection section = getFile().getConfigurationSection("Crate.sound");

        if (section != null) {
            SoundEffect sound = new SoundEffect(
                    section,
                    type,
                    fallback,
                    source
            );

            sound.play(player, location);
        }
    }
}