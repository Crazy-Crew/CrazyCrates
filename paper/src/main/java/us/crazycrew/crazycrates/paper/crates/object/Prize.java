package us.crazycrew.crazycrates.paper.crates.object;

import org.bukkit.inventory.ItemStack;
import java.util.Collections;
import java.util.List;

public class Prize {

    private final ItemStack displayItem;
    private final String displayPlayer;

    private final int maxRange;
    private final int chance;

    private final int slot;

    private final List<String> items;
    private final List<String> commands;
    private final List<String> messages;
    private final List<String> blacklistedPermissions;

    private final Prize alternativePrize;

    public Prize(List<String> items, List<String> commands, List<String> messages) {
        this.displayItem = null;
        this.displayPlayer = "";

        this.maxRange = 100;
        this.chance = 0;

        this.items = items != null ? items : Collections.emptyList();
        this.commands = commands != null ? commands : Collections.emptyList();
        this.messages = messages != null ? messages : Collections.emptyList();
        this.blacklistedPermissions = Collections.emptyList();

        this.alternativePrize = null;
    }

    public Prize(ItemStack displayItem, int maxRange, int chance, int slot, List<String> items, List<String> commands, List<String> messages, String displayPlayer, List<String> blacklistedPermissions, Prize alternativePrize) {
        this.displayItem = displayItem;
        this.displayPlayer = displayPlayer;

        this.maxRange = maxRange;
        this.chance = chance;

        this.slot = slot;

        this.items = items != null ? items : Collections.emptyList();
        this.commands = commands != null ? commands : Collections.emptyList();
        this.messages = messages != null ? messages : Collections.emptyList();

        this.blacklistedPermissions = blacklistedPermissions != null ? blacklistedPermissions : Collections.emptyList();

        this.alternativePrize = alternativePrize;
    }

    public ItemStack getDisplayItem() {
        return this.displayItem;
    }

    public String getDisplayPlayer() {
        return this.displayPlayer;
    }

    public int getMaxRange() {
        return this.maxRange;
    }

    public int getChance() {
        return this.chance;
    }

    public int getSlot() {
        return this.slot;
    }

    public List<String> getItems() {
        return this.items;
    }

    public List<String> getCommands() {
        return this.commands;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public List<String> getBlacklistedPermissions() {
        return this.blacklistedPermissions;
    }

    public Prize getAlternativePrize() {
        return this.alternativePrize;
    }
}