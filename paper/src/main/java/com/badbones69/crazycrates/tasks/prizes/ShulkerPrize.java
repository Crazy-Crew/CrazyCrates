package com.badbones69.crazycrates.tasks.prizes;

import com.badbones69.crazycrates.api.builders.ItemBuilder;
import org.bukkit.block.ShulkerBox;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;

public class ShulkerPrize extends PrizeBuilder {

    private final ItemBuilder builder = new ItemBuilder();

    private ItemStack[] itemStacks = null;

    private int maxRange;
    private int chance;

    @Override
    public void init(ConfigurationSection section) {
        String material = section.getString("DisplayItem", "shulker_box");
        String name = section.getString("DisplayName", material.replaceAll("_", " "));
        int amount = section.getInt("DisplayAmount", 1);
    public void init(@NotNull final ConfigurationSection section) {

        // The display item.
        this.builder.setMaterial(material).setDisplayName(name).setAmount(amount);

        ItemStack itemStack = this.builder.getItemStack();

        itemStack.editMeta(itemMeta -> {
            if (!(itemMeta instanceof BlockStateMeta block)) {
                return;
            }

            if (!(block.getBlockState() instanceof ShulkerBox shulker)) {
                return;
            }

            List<String> content = section.getStringList("DisplayContent");

            List<ItemStack> items = new ArrayList<>(27);

            for (String line : content) {
                ItemBuilder shulkerItem = new ItemBuilder();

                for (String key : line.split(", ")) {
                    String option = key.split(":")[0];
                    String value = key.replace(option + ":", "").replace(option, "");

                    switch (option.toLowerCase()) {
                        case "item" -> {
                            String[] item = value.split(";");

                            String mat = item[0];
                            int matAmount = Integer.parseInt(item[1]);

                            shulkerItem.setMaterial(mat).setAmount(matAmount);
                        }

                        case "enchantments" -> {
                            String[] enchantments = value.split("\\|")[0].split(";");

                            String enchantment = enchantments[0];
                            int level = Integer.parseInt(enchantments[1]);

                            shulkerItem.addEnchantment(enchantment, level, true);
                        }
                    }
                }

                items.add(shulkerItem.build());
            }

            int size = items.size();

            ItemStack[] itemStacks = new ItemStack[size];

            for (int count = 0; count < size; count++) {
                ItemStack shulkerItem = items.get(count);

                itemStacks[count] = (shulkerItem.isEmpty()) ? null : shulkerItem;
            }

            shulker.getInventory().setStorageContents(itemStacks);
            block.setBlockState(shulker);
            shulker.update(true);

            this.itemStacks = itemStacks;
        });

        this.maxRange = section.getInt("MaxRange", 100);
        this.chance = section.getInt("Chance", 10);
    }

    @Override
    public int maxRange() {
        return this.maxRange;
    }

    @Override
    public int chance() {
        return this.chance;
    }

    @Override
    public final ItemStack getItemStack(@NotNull final Player player) {
        return this.builder.setTarget(player).build();
    }

    /**
     * @return a stack of items for inventories.
     */
    public final ItemStack[] getStorageContents() {
        return this.itemStacks;
    }
}