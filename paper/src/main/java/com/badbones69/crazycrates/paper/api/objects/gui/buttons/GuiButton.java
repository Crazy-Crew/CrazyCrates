package com.badbones69.crazycrates.paper.api.objects.gui.buttons;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.tasks.crates.effects.SoundEffect;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.ryderbelserion.fusion.paper.FusionPaper;
import com.ryderbelserion.fusion.paper.builders.gui.objects.GuiItem;
import com.ryderbelserion.fusion.paper.builders.items.ItemBuilder;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;

public class GuiButton {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    protected final FusionPaper fusion = this.plugin.getFusion();

    private final Map<String, String> placeholders;
    private final ConfigurationSection section;
    private final ItemBuilder guiItem;

    private final List<String> commands;
    private final List<String> messages;

    public GuiButton(@NotNull final ConfigurationSection section, @NotNull final Map<String, String> placeholders) {
        this.guiItem = ItemBuilder.from(section.getString("material", "emerald_block"))
                .withDisplayName(section.getString("name", "No display name found."))
                .withDisplayLore(section.getStringList("lore"));

        this.commands = section.getStringList("commands");
        this.messages = section.getStringList("messages");
        this.placeholders = placeholders;
        this.section = section;
    }

    public @NotNull GuiItem getGuiItem(@NotNull final Player player) {
        return this.guiItem.asGuiItem(player, event -> {
            if (!(event.getWhoClicked() instanceof Player clicker)) return;

            clicker.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

            this.commands.forEach(command -> MiscUtils.sendCommand(command, this.placeholders));
            this.messages.forEach(message -> player.sendMessage(this.fusion.asComponent(clicker, message, this.placeholders)));

            final ConfigurationSection sound = this.section.getConfigurationSection("sound");

            if (sound != null) {
                SoundEffect effect = new SoundEffect(
                        this.section,
                        "sound",
                        "entity.villager.yes",
                        Sound.Source.MASTER
                );

                effect.play(clicker, clicker.getLocation());
            }
        });
    }

    public @NotNull final ConfigurationSection getSection() {
        return this.section;
    }
}