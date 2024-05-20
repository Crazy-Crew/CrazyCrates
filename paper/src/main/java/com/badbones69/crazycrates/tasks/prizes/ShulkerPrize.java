package com.badbones69.crazycrates.tasks.prizes;

import com.ryderbelserion.vital.paper.builders.items.ItemBuilder;
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
    public void init(@NotNull final ConfigurationSection section) {
        final String material = section.getString("DisplayItem", "shulker_box");
        final String name = section.getString("DisplayName", material.replaceAll("_", " "));
        final int amount = section.getInt("DisplayAmount", 1);

        // The display item.
        this.builder.withType(material).setAmount(amount).setDisplayName(name);

        this.builder.getStack().editMeta(itemMeta -> {
            if (!(itemMeta instanceof BlockStateMeta block)) {
                return;
            }

            if (!(block.getBlockState() instanceof ShulkerBox shulker)) {
                return;
            }

            final List<String> content = section.getStringList("DisplayContent");

            final List<ItemStack> items = new ArrayList<>(27);

            for (final String line : content) {
                final ItemBuilder shulkerItem = new ItemBuilder();

                for (final String key : line.split(", ")) {
                    final String option = key.split(":")[0];
                    final String value = key.replace(option + ":", "").replace(option, "");

                    switch (option.toLowerCase()) {
                        case "item" -> {
                            final String[] item = value.split(";");

                            final String mat = item[0];
                            final int matAmount = Integer.parseInt(item[1]);

                            shulkerItem.withType(mat).setAmount(matAmount);
                        }

                        case "enchantments" -> {
                            final String[] enchantments = value.split("\\|")[0].split(";");

                            final String enchantment = enchantments[0];
                            final int level = Integer.parseInt(enchantments[1]);

                            shulkerItem.addEnchantment(enchantment, level, true);
                        }
                    }
                }

                items.add(shulkerItem.getStack());
            }

            final int size = items.size();

            final ItemStack[] itemStacks = new ItemStack[size];

            for (int count = 0; count < size; count++) {
                final ItemStack shulkerItem = items.get(count);

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
    public final int maxRange() {
        return this.maxRange;
    }

    @Override
    public final int chance() {
        return this.chance;
    }

    @Override
    public final ItemStack getItemStack(@NotNull final Player player) {
        return this.builder.setPlayer(player).getStack();
    }

    /**
     * @return a stack of items for inventories.
     */
    public final ItemStack[] getStorageContents() {
        return this.itemStacks;
    }
}