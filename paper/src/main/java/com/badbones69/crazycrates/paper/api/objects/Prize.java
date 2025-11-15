package com.badbones69.crazycrates.paper.api.objects;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.enums.Messages;
import com.badbones69.crazycrates.paper.api.enums.other.keys.ItemKeys;
import com.badbones69.crazycrates.paper.utils.ItemUtils;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.core.config.ConfigManager;
import com.badbones69.crazycrates.core.config.impl.messages.CrateKeys;
import com.ryderbelserion.fusion.core.utils.StringUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.ItemBuilder;
import com.ryderbelserion.fusion.paper.builders.types.PatternBuilder;
import com.ryderbelserion.fusion.paper.builders.types.PotionBuilder;
import com.ryderbelserion.fusion.paper.builders.types.custom.CustomBuilder;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.bukkit.configuration.ConfigurationSection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Prize {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final ComponentLogger logger = this.plugin.getComponentLogger();

    private final SettingsManager config = ConfigManager.getConfig();

    private final FusionPaper fusion = this.plugin.getFusion();

    private final ConfigurationSection section;
    private final List<ItemBuilder> items;
    private final List<String> commands;
    private final List<String> messages;
    private final String sectionName;
    private final String prizeName;

    private List<String> permissions = new ArrayList<>();
    private ItemBuilder displayItem = ItemBuilder.from(ItemType.STONE, 1);
    private boolean firework = false;
    private String crateName = "";
    private double weight = -1;

    private int maxPulls;

    private List<Tier> tiers = new ArrayList<>();
    private Prize alternativePrize;

    private boolean broadcastToggle = false;
    private List<String> broadcastMessages = new ArrayList<>();
    private String broadcastPermission = "";

    private List<ItemStack> editorItems = new ArrayList<>();

    public Prize(@NotNull final ConfigurationSection section, @NotNull final List<ItemStack> editorItems, @NotNull final List<Tier> tierPrizes, @NotNull final String crateName, @Nullable final Prize alternativePrize) {
        this.section = section;

        this.sectionName = section.getName();

        this.crateName = crateName;

        if (this.config.getProperty(ConfigKeys.use_different_items_layout) && !this.section.isList("Items")) {
            this.items = ItemUtils.convertConfigurationSection(this.section.getConfigurationSection("Items"));
        } else {
            this.items = ItemUtils.convertStringList(this.section.getStringList("Items"), this.sectionName);
        }

        this.maxPulls = section.getInt("Settings.Max-Pulls", -1);

        this.tiers = tierPrizes;

        this.alternativePrize = alternativePrize;

        this.prizeName = section.getString("DisplayName", "");
        this.weight = section.getDouble("Weight", -1);
        this.firework = section.getBoolean("Firework", false);

        this.messages = section.getStringList("Messages"); // this returns an empty list if not found anyway.
        this.commands = section.getStringList("Commands"); // this returns an empty list if not found anyway.

        this.permissions = section.getStringList("BlackListed-Permissions"); // this returns an empty list if not found anyway.

        if (!this.permissions.isEmpty()) {
            this.permissions.replaceAll(String::toLowerCase);
        }

        this.broadcastToggle = section.getBoolean("Settings.Broadcast.Toggle", false);
        this.broadcastMessages = section.getStringList("Settings.Broadcast.Messages");
        this.broadcastPermission = section.getString("Settings.Broadcast.Permission", "");

        if (this.broadcastToggle && !this.broadcastPermission.isEmpty()) {
            MiscUtils.registerPermission(this.broadcastPermission, "Hides the broadcast message for prize: " + this.prizeName + " if a player has this permission", false);
        } else if (!this.broadcastToggle && !this.broadcastPermission.isEmpty()) {
            MiscUtils.unregisterPermission(this.broadcastPermission);
        }

        display();

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

        if (this.config.getProperty(ConfigKeys.use_different_items_layout) && !this.section.isList("Items")) {
            this.items = ItemUtils.convertConfigurationSection(this.section.getConfigurationSection("Items"));
        } else {
            this.items = ItemUtils.convertStringList(this.section.getStringList("Items"), this.sectionName);
        }
    }

    /**
     * @return the name of the prize.
     */
    public @NotNull final String getPrizeName() {
        return this.prizeName.isEmpty() ? "<lang:" + this.displayItem.getTranslationKey() + ">" : this.prizeName;
    }

    public @NotNull final String getStrippedName() {
        return PlainTextComponentSerializer.plainText().serialize(this.fusion.parse(getPrizeName()));
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
    public @NotNull final ItemStack getDisplayItem(@NotNull final Crate crate) {
        return getDisplayItem(null, crate);
    }

    /**
     * @return the display item that is shown for the preview and the winning prize.
     */
    public @NotNull final ItemStack getDisplayItem(@Nullable final Player player, @NotNull final Crate crate) {
        final int pulls = PrizeManager.getCurrentPulls(this, crate);
        final int maxPulls = getMaxPulls();
        final String amount = String.valueOf(pulls);

        final List<String> lore = new ArrayList<>();

        if (this.section.contains("DisplayLore") && !this.section.contains("Lore")) {
            lore.addAll(this.section.getStringList("DisplayLore"));
        }

        if (this.section.contains("Lore")) {
            if (MiscUtils.isLogging()) {
                List.of(
                        "Deprecated usage of Lore in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Lore to DisplayLore",
                        "Lore will be removed in the next major version of Minecraft in favor of DisplayLore",
                        "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                ).forEach(this.logger::warn);
            }

            lore.addAll(this.section.getStringList("Lore"));
        }

        if (maxPulls != 0 && pulls != 0 && pulls >= maxPulls) {
            final String line = player != null ? Messages.crate_prize_max_pulls.getString(player) : ConfigManager.getMessages().getProperty(CrateKeys.crate_prize_max_pulls);

            if (!line.isEmpty()) {
                lore.add(line);
            }
        }

        this.displayItem.withDisplayLore(lore);

        final String weight = StringUtils.format(crate.getChance(getWeight()));

        this.displayItem.addPlaceholder("{chance}", weight)
                .addPlaceholder("{maxpulls}", String.valueOf(maxPulls))
                .addPlaceholder("{pulls}", amount)
                .addPlaceholder("%chance%", weight)
                .addPlaceholder("%maxpulls%", String.valueOf(maxPulls))
                .addPlaceholder("%pulls%", amount);

        return this.displayItem.setPersistentString(ItemKeys.crate_prize.getNamespacedKey(), this.sectionName).asItemStack(player);
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
     * @return the ItemBuilders for all custom items made by the Items configuration section.
     */
    public @NotNull final List<ItemBuilder> getItems() {
        return this.items;
    }

    /**
     * Checks if there is any valid items.
     *
     * @return true or false
     */
    public final boolean isItemsEmpty() {
        return getItems().isEmpty();
    }

    /**
     * @return the name of the crate the prize is in.
     */
    public @NotNull final String getCrateName() {
        return this.crateName;
    }

    /**
     * Gets the weight
     *
     * @return the weight
     */
    public final double getWeight() {
        if (this.weight == -1) {
            this.fusion.log("warn", "Cannot fetch the weight as the option is not present for this prize: {} in the crate: {}", this.prizeName, this.crateName);
        }

        return this.weight;
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
     * @return true if the prize has blacklist permissions and false if not.
     */
    public final boolean hasPermission(@NotNull final Player player) {
        if (player.isOp()) return false;

        for (final String permission : this.permissions) {
            if (player.hasPermission(permission)) return true;
        }

        return false;
    }

    public void broadcast(@NotNull final Player target, @NotNull final Crate crate) {
        if (this.broadcastToggle) {
            send(target, crate);
        } else if (crate.isBroadcastToggled()) {
            send(target, crate);
        }
    }

    private void send(@NotNull final Player target, @NotNull final Crate crate) {
        final Server server = this.plugin.getServer();

        final List<String> messages = this.broadcastToggle ? this.broadcastMessages : crate.getBroadcastMessages();
        final String permission = this.broadcastToggle ? this.broadcastPermission : crate.getBroadcastPermission();

        final String current_pulls = String.valueOf(PrizeManager.getCurrentPulls(this, crate));
        final String max_pulls = String.valueOf(getMaxPulls());

        final String message = StringUtils.toString(messages);

        final Map<String, String> placeholders = new HashMap<>() {{
            put("%player%", target.getName());
            put("%crate%", crate.getCrateName());
            put("%reward%", fusion.replacePlaceholder(getPrizeName(), new HashMap<>() {{
                put("%maxpulls%", max_pulls);
                put("%pulls%", current_pulls);
            }}));
            put("%maxpulls%", max_pulls);
            put("%pulls%", current_pulls);
            put("%reward_stripped%", getStrippedName());
        final Map<String, String> placeholders = Map.of(
            "%player%", target.getName(),
            "%crate%", crate.getCrateName(),
            "%reward%", getPrizeName().replaceAll("%maxpulls%", max_pulls).replaceAll("%pulls%", current_pulls),
            "%maxpulls%", max_pulls,
            "%pulls%", current_pulls,
            "%reward_stripped%", getStrippedName()
        );

            put("{player}", target.getName());
            put("{crate}", crate.getCrateName());
            put("{reward}", fusion.replacePlaceholder(getPrizeName(), new HashMap<>() {{
                put("{maxpulls}", max_pulls);
                put("{pulls}", current_pulls);
            }}));
            put("{maxpulls}", max_pulls);
            put("{pulls}", current_pulls);
            put("{reward_stripped}", getStrippedName());
        }};

        if (permission.isEmpty()) {
            server.broadcast(this.fusion.parse(target, message, placeholders));

            return;
        }

        server.broadcast(this.fusion.parse(target, message, placeholders), permission);
    }

    private void display() {
        try {
            if (this.section.contains("DisplayData")) {
                this.displayItem.withBase64(this.section.getString("DisplayData", ""));
            }

            if (this.section.contains("DisplayName")) {
                this.displayItem.withDisplayName(this.prizeName);
            }

            if (this.section.contains("DisplayItem")) {
                this.displayItem.withCustomItem(this.section.getString("DisplayItem", "red_terracotta").toLowerCase());
            }

            if (this.section.contains("DisplayAmount")) {
                this.displayItem.setAmount(this.section.getInt("DisplayAmount", 1));
            }

            if (this.section.contains("DisplayLore") && !this.section.contains("Lore")) {
                this.displayItem.withDisplayLore(this.section.getStringList("DisplayLore"));
            }

            if (this.section.contains("Lore")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Lore in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Lore to DisplayLore",
                            "Lore will be removed in the next major version of Minecraft in favor of DisplayLore",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.logger::warn);
                }

                this.displayItem.withDisplayLore(this.section.getStringList("Lore"));
            }

            ItemUtils.updateEnchantGlintState(this.displayItem, this.section.getString("Glowing", "add_glow"));

            this.displayItem.setItemDamage(this.section.getInt("DisplayDamage", 0));

            if (this.section.contains("Patterns")) {
                if (MiscUtils.isLogging()) {
                    List.of(
                            "Deprecated usage of Patterns in your Prize " + this.sectionName + " in " + this.crateName + ".yml, please change Patterns to DisplayPatterns",
                            "Patterns will be removed in the next major version of Minecraft in favor of DisplayPatterns",
                            "You can turn my nagging off in config.yml, verbose_logging: true -> false"
                    ).forEach(this.logger::warn);
                }

                final PatternBuilder patternBuilder = this.displayItem.asPatternBuilder();

                for (final String pattern : this.section.getStringList("Patterns")) {
                    final String[] split = pattern.split(":");
                    final String type = split[0];
                    final String color = split[1];

                    patternBuilder.addPattern(type, color);
                }

                patternBuilder.build();
            }

            if (this.section.contains("DisplayPatterns")) {
                final PatternBuilder patternBuilder = this.displayItem.asPatternBuilder();

                for (final String pattern : this.section.getStringList("DisplayPatterns")) {
                    final String[] split = pattern.split(":");
                    final String type = split[0];
                    final String color = split[1];

                    patternBuilder.addPattern(type, color);
                }

                patternBuilder.build();
            }

            this.displayItem.setUnbreakable(this.section.getBoolean("Unbreakable", false));

            final CustomBuilder customBuilder = this.displayItem.asCustomBuilder();

            customBuilder.setCustomModelData(this.section.getString("Settings.Custom-Model-Data", ""));

            customBuilder.setItemModel(this.section.getString("Settings.Model.Namespace", ""), this.section.getString("Settings.Model.Id", ""));

            customBuilder.build();

            if (this.section.contains("Settings.RGB")) {
                this.displayItem.setColor(this.section.getString("Settings.RGB", ""));
            } else if (this.section.contains("Settings.Color")) {
                this.displayItem.setColor(this.section.getString("Settings.Color", "RED"));
            }

            if (this.section.contains("Skull")) {
                this.displayItem.withSkull(section.getString("Skull", ""));
            }

            if (this.section.contains("Player")) {
                this.displayItem.asSkullBuilder().withName(this.section.getString("Player", "")).build();
            }

            this.displayItem.setTrim(this.section.getString("DisplayTrim.Pattern", ""), this.section.getString("DisplayTrim.Material", ""));

            if (this.section.contains("DisplayEnchantments")) {
                for (final String ench : this.section.getStringList("DisplayEnchantments")) {
                    String[] value = ench.split(":");

                    this.displayItem.addEnchantment(value[0], Integer.parseInt(value[1]));
                }
            }

            if (this.section.contains("DisplayPotions")) {
                final ConfigurationSection potions = this.section.getConfigurationSection("DisplayPotions");

                if (potions != null) {
                    final PotionBuilder potionBuilder = this.displayItem.asPotionBuilder();

                    for (final String potion : potions.getKeys(false)) {
                        final PotionEffectType type = com.ryderbelserion.fusion.paper.utils.ItemUtils.getPotionEffect(potion);

                        if (type != null) {
                            final ConfigurationSection data = potions.getConfigurationSection(potion);

                            if (data != null) {
                                final int duration = data.getInt("duration", 10) * 20;
                                final int level = data.getInt("level", 1);

                                potionBuilder.withPotionEffect(type, duration, level);
                            }
                        }
                    }

                    potionBuilder.build();
                }
            }
        } catch (final Exception exception) {
            return new LegacyItemBuilder(this.plugin, ItemType.RED_TERRACOTTA).setDisplayName("<red><bold>ERROR").setDisplayLore(List.of(
                "<red>There was an error with one of your prizes!",
                "<red>The reward in question is labeled: <yellow>" + section.getName() + " <red>in crate: <yellow>" + crateName,
                "<red>Name of the reward is " + section.getString("DisplayName"),
                "<red>If you are confused, Stop by our discord for support!"
            ));
        }
    }

    public final List<ItemStack> getEditorItems() {
        return this.editorItems;
    }

    public final int getMaxPulls() {
        return this.maxPulls == -1 ? 0 : this.maxPulls;
    }
}