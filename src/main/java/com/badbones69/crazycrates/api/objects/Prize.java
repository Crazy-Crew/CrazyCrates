package com.badbones69.crazycrates.api.objects;

import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.enums.PersistentKeys;
import com.badbones69.crazycrates.api.utils.ItemUtils;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import com.ryderbelserion.vital.paper.api.builders.items.ItemBuilder;
import com.ryderbelserion.vital.paper.api.enums.Support;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import com.ryderbelserion.vital.paper.util.ItemUtil;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Prize {

    private final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final ConfigurationSection section;
    private final List<ItemBuilder> builders;
    private final List<String> commands;
    private final List<String> messages;
    private final String sectionName;
    private final String prizeName;

    private List<String> permissions = new ArrayList<>();
    private ItemBuilder displayItem = new ItemBuilder();
    private ItemBuilder prizeItem = new ItemBuilder();
    private boolean firework = false;
    private String crateName = "";
    private int maxRange = 100;
    private int chance = 0;

    private List<Tier> tiers = new ArrayList<>();
    private Prize alternativePrize;

    private boolean broadcast = false;
    private List<String> broadcastMessages = new ArrayList<>();
    private String broadcastPermission = "";

    private List<ItemStack> editorItems = new ArrayList<>();

    public Prize(@NotNull final ConfigurationSection section, List<ItemStack> editorItems, @NotNull final List<Tier> tierPrizes, @NotNull final String crateName, @Nullable final Prize alternativePrize) {
        this.section = section;

        this.sectionName = section.getName();

        this.crateName = crateName;

        this.builders = ItemUtils.convertStringList(this.section.getStringList("Items"), this.sectionName);

        this.tiers = tierPrizes;

        this.alternativePrize = alternativePrize;

        this.prizeName = section.getString("DisplayName", "");
        this.maxRange = section.getInt("MaxRange", 100);
        this.chance = section.getInt("Chance", 50);
        this.firework = section.getBoolean("Firework", false);

        this.messages = section.getStringList("Messages"); // this returns an empty list if not found anyway.
        this.commands = section.getStringList("Commands"); // this returns an empty list if not found anyway.

        this.permissions = section.getStringList("BlackListed-Permissions"); // this returns an empty list if not found anyway.

        if (!this.permissions.isEmpty()) {
            this.permissions.replaceAll(String::toLowerCase);
        }

        this.broadcast = section.getBoolean("Settings.Broadcast.Toggle", false);
        this.broadcastMessages = section.getStringList("Settings.Broadcast.Messages");
        this.broadcastPermission = section.getString("Settings.Broadcast.Permission", "");

        this.prizeItem = display();
        this.displayItem = new ItemBuilder(this.prizeItem, true);

        this.editorItems = editorItems;
    }

    /**
     * Create a new prize.
     * This option is used only for Alternative Prizes.
     *
     * @param section the configuration section.
     */
    public Prize(@NotNull final String prizeName, @NotNull final String sectionName, @NotNull final ConfigurationSection section) {
        this.prizeName = prizeName;

        this.messages = section.getStringList("Messages"); // this returns an empty list if not found anyway.
        this.commands = section.getStringList("Commands"); // this returns an empty list if not found anyway.

        this.sectionName = sectionName;

        this.section = section;

        this.builders = ItemUtils.convertStringList(this.section.getStringList("Items"), this.sectionName);
    }

    /**
     * @return the name of the prize.
     */
    public @NotNull final String getPrizeName() {
        if (!ConfigManager.getConfig().getProperty(ConfigKeys.minimessage_toggle)) {
            return this.prizeName.isEmpty() ? "" : this.prizeName;
        }

        return this.prizeName.isEmpty() ? "<lang:" + this.displayItem.getType().getItemTranslationKey() + ">" : this.prizeName;
    }

    public @NotNull final String getStrippedName() {
        if (ConfigManager.getConfig().getProperty(ConfigKeys.minimessage_toggle)) {
            return PlainTextComponentSerializer.plainText().serialize(AdvUtil.parse(getPrizeName()));
        }

        return ChatColor.stripColor(getPrizeName());
    }

    /**
     * @return the section name.
     */
    public @NotNull final String getSectionName() {
        return this.sectionName;
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public @NotNull final ItemStack getDisplayItem() {
        return this.displayItem.setPersistentString(PersistentKeys.crate_prize.getNamespacedKey(), this.sectionName).getStack();
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public @NotNull final ItemStack getDisplayItem(@NotNull final Player player) {
        if (Support.placeholder_api.isEnabled()) {
            final String displayName = this.displayItem.getDisplayName();

            this.displayItem.setDisplayName(PlaceholderAPI.setPlaceholders(player, displayName));

            List<String> lore = new ArrayList<>();

            if (this.section.contains("DisplayLore") && !this.section.contains("Lore")) {
                this.section.getStringList("DisplayLore").forEach(line -> lore.add(PlaceholderAPI.setPlaceholders(player, line)));
            }

            if (this.section.contains("Lore")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Lore in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Lore to DisplayLore",
                            "Lore will be removed in the next major version of Minecraft in favor of DisplayLore",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.plugin.getComponentLogger()::warn);
                }

                this.section.getStringList("Lore").forEach(line -> lore.add(PlaceholderAPI.setPlaceholders(player, line)));
            }

            this.displayItem.setDisplayLore(lore);
        }

        return this.displayItem.setPlayer(player).setPersistentString(PersistentKeys.crate_prize.getNamespacedKey(), this.sectionName).getStack();
    }

    /**
     * @return the ItemBuilder of the display item.
     */
    public @NotNull final ItemBuilder getPrizeItem() {
        return this.prizeItem;
    }
    
    /**
     * @return the list of tiers the prize is in.
     */
    public @NotNull final List<Tier> getTiers() {
        return this.tiers;
    }
    
    /**
     * @return the messages sent to the player.
     */
    public @NotNull final List<String> getMessages() {
        return this.messages;
    }
    
    /**
     * @return the commands that are run when the player wins.
     */
    public @NotNull final List<String> getCommands() {
        return this.commands;
    }
    
    /**
     * @return the ItemBuilders for all the custom items made from the Items: option.
     */
    public @NotNull final List<ItemBuilder> getItemBuilders() {
        return this.builders;
    }
    
    /**
     * @return the name of the crate the prize is in.
     */
    public @NotNull final String getCrateName() {
        return this.crateName;
    }
    
    /**
     * @return the chance the prize has of being picked.
     */
    public final int getChance() {
        return this.chance;
    }
    
    /**
     * @return the max range of the prize.
     */
    public final int getMaxRange() {
        return this.maxRange;
    }
    
    /**
     * @return true if a firework explosion is played and false if not.
     */
    public final boolean useFireworks() {
        return this.firework;
    }
    
    /**
     * @return the alternative prize the player wins if they have a blacklist permission.
     */
    public @NotNull final Prize getAlternativePrize() {
        return this.alternativePrize;
    }
    
    /**
     * @return true if the prize doesn't have an alternative prize and false if it does.
     */
    public final boolean hasAlternativePrize() {
        return this.alternativePrize == null;
    }
    
    /**
     * @return true if they prize has blacklist permissions and false if not.
     */
    public final boolean hasPermission(@NotNull final Player player) {
        if (player.isOp()) return false;

        for (String permission : this.permissions) {
            if (player.hasPermission(permission)) return true;
        }

        return false;
    }

    public void broadcast(final Crate crate) {
        if (!this.broadcast) return;

        final boolean isAdventure = ConfigManager.getConfig().getProperty(ConfigKeys.minimessage_toggle);

        final String fancyName = crate.getCrateName();
        final String prizeName = getPrizeName();
        final String strippedName = getStrippedName();

        if (isAdventure) {
            this.plugin.getServer().getOnlinePlayers().forEach(player -> {
                if (!this.broadcastPermission.isEmpty() && player.hasPermission(this.broadcastPermission)) return;

                this.broadcastMessages.forEach(message -> player.sendMessage(AdvUtil.parse(message, new HashMap<>() {{
                    put("%player%", player.getName());
                    put("%crate%", fancyName);
                    put("%reward%", prizeName);
                    put("%reward_stripped%", strippedName);
                }}, player)));
            });

            return;
        }

        this.plugin.getServer().getOnlinePlayers().forEach(player -> {
            if (!this.broadcastPermission.isEmpty() && player.hasPermission(this.broadcastPermission)) return;

            this.broadcastMessages.forEach(message -> player.sendMessage(ItemUtil.color(message, new HashMap<>() {{
                put("%player%", player.getName());
                put("%crate%", fancyName);
                put("%reward%", prizeName);
                put("%reward_stripped%", strippedName);
            }}, player)));
        });
    }

    private @NotNull ItemBuilder display() {
        ItemBuilder builder = new ItemBuilder();

        try {
            if (this.section.contains("DisplayData")) {
                builder = builder.fromBase64(this.section.getString("DisplayData"));
            }

            if (this.section.contains("DisplayName")) {
                builder.setDisplayName(this.prizeName);
            }

            if (this.section.contains("DisplayItem")) {
                builder.withType(this.section.getString("DisplayItem", "red_terracotta").toLowerCase());
            }

            if (this.section.contains("DisplayAmount")) {
                builder.setAmount(this.section.getInt("DisplayAmount", 1));
            }

            if (this.section.contains("DisplayLore") && !this.section.contains("Lore")) {
                builder.setDisplayLore(this.section.getStringList("DisplayLore"));
            }

            if (this.section.contains("Lore")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Lore in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Lore to DisplayLore",
                            "Lore will be removed in the next major version of Minecraft in favor of DisplayLore",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.plugin.getComponentLogger()::warn);
                }

                builder.setDisplayLore(this.section.getStringList("Lore"));
            }

            builder.setGlowing(this.section.contains("Glowing") ? section.getBoolean("Glowing") : null);

            builder.setDamage(this.section.getInt("DisplayDamage", 0));

            if (this.section.contains("Patterns")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Patterns in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Patterns to DisplayPatterns",
                            "Patterns will be removed in the next major version of Minecraft in favor of DisplayPatterns",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.plugin.getComponentLogger()::warn);
                }

                for (final String pattern : this.section.getStringList("Patterns")) {
                    builder.addPattern(pattern.toLowerCase());
                }
            }

            if (this.section.contains("DisplayPatterns")) {
                for (final String pattern : this.section.getStringList("DisplayPatterns")) {
                    builder.addPattern(pattern.toLowerCase());
                }
            }

            builder.setItemFlags(this.section.getStringList("Flags"));

            builder.setHidingItemFlags(this.section.getBoolean("HideItemFlags", false));

            builder.setUnbreakable(section.getBoolean("Unbreakable", false));

            builder.setCustomModelData(this.section.getInt("Settings.Custom-Model-Data", -1));

            if (this.section.contains("Settings.Mob-Type")) {
                final EntityType type = ItemUtil.getEntity(this.section.getString("Settings.Mob-Type", "cow"));

                if (type != null) {
                    builder.setEntityType(type);
                }
            }

            if (this.section.contains("Skull") && this.plugin.getApi() != null) {
                builder.setSkull(section.getString("Skull", ""), this.plugin.getApi());
            }

            if (this.section.contains("Player")) {
                builder.setPlayer(this.section.getString("Player", ""));
            }

            if (this.section.contains("DisplayTrim.Pattern") && builder.isArmor()) {
                builder.applyTrimPattern(this.section.getString("DisplayTrim.Pattern", "sentry"));
            }

            if (this.section.contains("DisplayTrim.Material") && builder.isArmor()) {
                builder.applyTrimMaterial(this.section.getString("DisplayTrim.Material", "quartz"));
            }

            if (this.section.contains("DisplayEnchantments")) {
                for (String ench : this.section.getStringList("DisplayEnchantments")) {
                    String[] value = ench.split(":");

                    builder.addEnchantment(value[0], Integer.parseInt(value[1]), true);
                }
            }

            return builder;
        } catch (Exception exception) {
            return new ItemBuilder(Material.RED_TERRACOTTA).setDisplayName("&l&cERROR").setDisplayLore(new ArrayList<>() {{
                add("&cThere was an error with one of your prizes!");
                add("&cThe reward in question is labeled: &e" + section.getName() + " &cin crate: &e" + crateName);
                add("&cName of the reward is " + section.getString("DisplayName"));
                add("&cIf you are confused, Stop by our discord for support!");
            }});
        }
    }

    public final List<ItemStack> getEditorItems() {
        return this.editorItems;
    }
}