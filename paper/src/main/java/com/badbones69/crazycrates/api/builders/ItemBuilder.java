package com.badbones69.crazycrates.api.builders;

public class ItemBuilder extends com.ryderbelserion.vital.items.ItemBuilder {

    /*private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    // Items
    private Material material = Material.STONE;
    private ItemStack itemStack = null;
    private int itemAmount = 1;

    // NBT Data
    private String itemData = "";

    // Display
    private String displayName = "";
    private List<String> displayLore = new ArrayList<>();
    private int itemDamage = 0;

    // Model Data
    private boolean hasCustomModelData = false;
    private int customModelData = 0;
    private String customMaterial = "";

    // Potions
    private boolean isPotion = false;
    private Color potionColor = null;
    private PotionEffectType potionType = null;
    private int potionDuration = -1;
    private int potionAmplifier = 1;

    // Player Heads
    private boolean isHash = false;
    private boolean isURL = false;
    private boolean isHead = false;

    private String player = "";

    // Arrows
    private boolean isTippedArrow = false;

    // Armor
    private boolean isLeatherArmor = false;
    private boolean isArmor = false;

    // Trims
    private TrimMaterial trimMaterial = null;
    private TrimPattern trimPattern = null;
    private Color armorColor = null;

    // Banners
    private boolean isBanner = false;
    private List<Pattern> patterns = new ArrayList<>();

    // Shields
    private boolean isShield = false;

    // Maps
    private boolean isMap = false;
    private Color mapColor = null;

    // Fireworks
    private boolean isFirework = false;
    private boolean isFireworkStar = false;
    private Color fireworkColor = null;
    private List<Color> fireworkColors = new ArrayList<>();
    private int fireworkPower = 1;

    // Enchantments or ItemFlags
    private boolean isUnbreakable = false;

    private boolean hideItemFlags = false;
    private List<ItemFlag> itemFlags = new ArrayList<>();

    private boolean isGlowing = false;

    private boolean isSpawner = false;
    private EntityType entityType = EntityType.BAT;

    // Crates
    private String crateName = "";
    private Player target = null;

    // Placeholders
    private Map<String, String> namePlaceholders = new HashMap<>();
    private Map<String, String> lorePlaceholders = new HashMap<>();

    private boolean isBook;

    public ItemBuilder(ItemBuilder itemBuilder) {
        if (!itemBuilder.displayName.isBlank()) {
            this.displayName = itemBuilder.displayName;
        } else {
            String material = WordUtils.capitalizeFully(this.material.getKey().getKey().replaceAll("_", " "));

            this.displayName = material;
        }

        this.namePlaceholders = new HashMap<>(itemBuilder.namePlaceholders);
        this.lorePlaceholders = new HashMap<>(itemBuilder.lorePlaceholders);

        this.crateName = itemBuilder.crateName;
    }

    public ItemBuilder(ItemStack itemStack) {

    }

    public ItemBuilder(ItemStack itemStack, Player target) {

    }

    public ItemBuilder() {}

    private Component parse(String message) {
        if (Support.placeholder_api.isEnabled() && this.target != null) {
            return MiscUtil.parse(PlaceholderAPI.setPlaceholders(this.target, message));
        }

        return MiscUtil.parse(message);
    }

    public ItemStack build() {
        return new ItemStack(Material.STONE);
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public ItemMeta getItemMeta() {
        return this.itemStack.getItemMeta();
    }

    public ItemBuilder setItemMeta(ItemMeta itemMeta) {
        this.itemStack.setItemMeta(itemMeta);

        return this;
    }

    public ItemBuilder setTarget(Player target) {
        this.target = target;

        return this;
    }

    public Component getUpdatedName() {
        String newName = this.displayName;

        for (String placeholder : this.namePlaceholders.keySet()) {
            newName = newName.replace(placeholder, this.namePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), this.namePlaceholders.get(placeholder));
        }

        return parse(newName);
    }

    public String getName() {
        return this.displayName;
    }

    public Material getMaterial() {
        return this.material;
    }

    public ItemBuilder setMaterial(Material material) {
        this.material = material;

        if (this.itemStack == null) {
            // If item stack is null, we create new item stack based on material.
            this.itemStack = new ItemStack(this.material);
        } else {
            // Get old item meta.
            ItemMeta itemMeta = this.itemStack.getItemMeta();

            // Create new itemstack.
            ItemStack newItemStack = new ItemStack(this.material);
            // Set old item meta to new itemstack.
            newItemStack.setItemMeta(itemMeta);

            // Overwrite old item stack with new item stack.
            this.itemStack = newItemStack;
        }

        this.isHead = material == Material.PLAYER_HEAD;

        return this;
    }

    public ItemBuilder setMaterial(String type) {
        return this;
    }

    public void setTrimMaterial(TrimMaterial trimMaterial) {
        this.trimMaterial = trimMaterial;
    }

    public void setTrimPattern(TrimPattern trimPattern) {
        this.trimPattern = trimPattern;
    }

    public ItemBuilder setCrateName(String crateName) {
        this.crateName = crateName;

        return this;
    }

    public void setDamage(int damage) {
        this.itemDamage = damage;
    }

    public int getDamage() {
        return this.itemDamage;
    }

    public ItemBuilder setName(String itemName) {
        return this;
    }

    public ItemBuilder setNamePlaceholders(Map<String, String> placeholders) {
        this.namePlaceholders = placeholders;

        return this;
    }

    public ItemBuilder addNamePlaceholder(String placeholder, String argument) {
        this.namePlaceholders.put(placeholder, argument);

        return this;
    }

    public ItemBuilder removeNamePlaceholder(String placeholder) {
        this.namePlaceholders.remove(placeholder);

        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        if (lore != null) {
            this.displayLore.clear();
            this.displayLore.addAll(lore);
        }

        return this;
    }

    public ItemBuilder setLore(Player player, List<String> lore) {
        if (lore != null) {
            this.displayLore.clear();

            for (String line : lore) {
                this.displayLore.add(PlaceholderAPI.setPlaceholders(player, line));
            }
        }

        return this;
    }

    public ItemBuilder addLore(String line) {
        if (line != null) this.displayLore.add(line);

        return this;
    }

    public ItemBuilder setLorePlaceholders(Map<String, String> placeholders) {
        this.lorePlaceholders = placeholders;

        return this;
    }

    public ItemBuilder addLorePlaceholder(String placeholder, String argument) {
        this.lorePlaceholders.put(placeholder, argument);

        return this;
    }

    public List<Component> getUpdatedLore() {
        List<Component> newLore = new ArrayList<>();

        for (String item : this.displayLore) {
            for (String placeholder : this.lorePlaceholders.keySet()) {
                item = item.replace(placeholder, this.lorePlaceholders.get(placeholder)).replace(placeholder.toLowerCase(), this.lorePlaceholders.get(placeholder));
            }

            newLore.add(parse(item));
        }

        return newLore;
    }

    public ItemBuilder removeLorePlaceholder(String placeholder) {
        this.lorePlaceholders.remove(placeholder);

        return this;
    }

    public ItemBuilder setEntityType(EntityType entityType) {
        this.entityType = entityType;

        return this;
    }

    public ItemBuilder addPatterns(List<String> patterns) {
        //patterns.forEach(this::addPatterns);

        return this;
    }

    public ItemBuilder addPattern(Pattern pattern) {
        this.patterns.add(pattern);

        return this;
    }

    public ItemBuilder setPattern(List<Pattern> patterns) {
        this.patterns = patterns;

        return this;
    }

    public ItemBuilder setAmount(int amount) {
        this.itemAmount = amount;

        return this;
    }

    public ItemBuilder setPlayerName(String playerName) {
        this.player = playerName;

        if (this.player != null && this.player.length() > 16) {
            this.isHash = true;
            this.isURL = this.player.startsWith("http");
        }

        return this;
    }

    public ItemBuilder addEnchantments(Map<Enchantment, Integer> enchantments, boolean unsafeEnchantments) {
        enchantments.forEach((enchantment, level) -> addEnchantment(enchantment, level, unsafeEnchantments));

        return this;
    }

    public ItemBuilder addEnchantment(Enchantment enchantment, int level, boolean unsafeEnchantments) {
        this.itemStack.editMeta(itemMeta -> {
            if (this.isBook) {
                EnchantmentStorageMeta storage = (EnchantmentStorageMeta) itemMeta;

                storage.addStoredEnchant(enchantment, level, unsafeEnchantments);

                return;
            }

            itemMeta.addEnchant(enchantment, level, unsafeEnchantments);
        });

        return this;
    }

    public ItemBuilder removeEnchantment(Enchantment enchantment) {
        getItemStack().editMeta(itemMeta -> {
            if (this.isBook) {
                EnchantmentStorageMeta storage = (EnchantmentStorageMeta) itemMeta;

                storage.removeStoredEnchant(enchantment);

                return;
            }

            itemMeta.removeEnchant(enchantment);
        });

        return this;
    }

    public ItemBuilder setFlagsFromStrings(List<String> flagStrings) {
        this.itemFlags.clear();

        for (String flagString : flagStrings) {
            //ItemFlag flag = getFlag(flagString);

            //if (flag != null) this.itemFlags.add(flag);
        }

        return this;
    }

    public ItemBuilder addItemFlags(List<String> flagStrings) {
        for (String flagString : flagStrings) {
            try {
                ItemFlag itemFlag = ItemFlag.valueOf(flagString.toUpperCase());

                addItemFlag(itemFlag);
            } catch (Exception ignored) {}
        }

        return this;
    }

    public ItemBuilder addFlags(String flagString) {
        //ItemFlag flag = getFlag(flagString);

        //if (flag != null) this.itemFlags.add(flag);

        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag itemFlag) {
        if (itemFlag != null) this.itemFlags.add(itemFlag);

        return this;
    }

    public ItemBuilder setItemFlags(List<ItemFlag> itemFlags) {
        this.itemFlags = itemFlags;

        return this;
    }

    public ItemBuilder hideItemFlags(boolean hideItemFlags) {
        this.hideItemFlags = hideItemFlags;

        return this;
    }

    public ItemBuilder setUnbreakable(boolean unbreakable) {
        this.isUnbreakable = unbreakable;

        return this;
    }

    public ItemBuilder setGlow(boolean glow) {
        this.isGlowing = glow;

        return this;
    }

    public static ItemBuilder convertItemStack(ItemStack item) {
        return new ItemBuilder(item).setAmount(item.getAmount()).addEnchantments(new HashMap<>(item.getEnchantments()), true);
    }

    public static ItemBuilder convertItemStack(ItemStack item, Player player) {
        return new ItemBuilder(item).setTarget(player).setAmount(item.getAmount()).addEnchantments(new HashMap<>(item.getEnchantments()), true);
    }

    public static ItemBuilder convertString(String itemString) {
        return convertString(itemString, null);
    }

    public static ItemBuilder convertString(String itemString, String placeHolder) {
        ItemBuilder itemBuilder = new ItemBuilder();

        try {
            for (String optionString : itemString.split(", ")) {
                String option = optionString.split(":")[0];
                String value = optionString.replace(option + ":", "").replace(option, "");

                switch (option.toLowerCase()) {
                    case "item" -> itemBuilder.setMaterial(value);
                    case "name" -> itemBuilder.setName(value);
                    case "amount" -> {
                        try {
                            itemBuilder.setAmount(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setAmount(1);
                        }
                    }
                    case "damage" -> {
                        try {
                            itemBuilder.setDamage(Integer.parseInt(value));
                        } catch (NumberFormatException e) {
                            itemBuilder.setDamage(0);
                        }
                    }
                    case "lore" -> itemBuilder.setLore(Arrays.asList(value.split(",")));
                    case "player" -> itemBuilder.setPlayerName(value);
                    case "unbreakable-item" -> {
                        if (value.isEmpty() || value.equalsIgnoreCase("true")) itemBuilder.setUnbreakable(true);
                    }
                    case "trim-pattern" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimPattern(Registry.TRIM_PATTERN.get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    case "trim-material" -> {
                        if (!value.isEmpty()) itemBuilder.setTrimMaterial(Registry.TRIM_MATERIAL.get(NamespacedKey.minecraft(value.toLowerCase())));
                    }
                    /*default -> {
                        Enchantment enchantment = getEnchantment(option);

                        if (enchantment != null) {
                            try {
                                itemBuilder.addEnchantment(enchantment, Integer.parseInt(value), true);
                            } catch (NumberFormatException e) {
                                itemBuilder.addEnchantment(enchantment, 1, true);
                            }

                            break;
                        }

                        for (ItemFlag itemFlag : ItemFlag.values()) {
                            if (itemFlag.name().equalsIgnoreCase(option)) {
                                itemBuilder.addItemFlag(itemFlag);
                                break;
                            }
                        }

                        try {
                            for (PatternType pattern : PatternType.values()) {
                                if (option.equalsIgnoreCase(pattern.name()) || value.equalsIgnoreCase(pattern.getIdentifier())) {
                                    DyeColor color = DyeUtil.getDyeColor(value);
                                    if (color != null) itemBuilder.addPattern(new Pattern(color, pattern));
                                    break;
                                }
                            }
                        } catch (Exception ignored) {}
                    }
                }
            }
        } catch (Exception exception) {
            itemBuilder.setMaterial(Material.RED_TERRACOTTA).setName("<bold><red>ERROR</bold>").setLore(Arrays.asList("<red>There is an error", "<red>For : <red>" + (placeHolder != null ? placeHolder : "")));

            CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);
            plugin.getLogger().log(Level.WARNING, "An error has occurred with the item builder: ", exception);
        }

        return itemBuilder;
    }

    public static List<ItemBuilder> convertStringList(List<String> itemStrings) {
        return convertStringList(itemStrings, null);
    }


    public static List<ItemBuilder> convertStringList(List<String> itemStrings, String placeholder) {
        return itemStrings.stream().map(itemString -> convertString(itemString, placeholder)).collect(Collectors.toList());
    }*/
}