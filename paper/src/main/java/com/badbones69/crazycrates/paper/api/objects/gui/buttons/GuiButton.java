package com.badbones69.crazycrates.paper.api.objects.gui.buttons;

import com.badbones69.crazycrates.paper.CrazyCrates;
import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.tasks.crates.effects.SoundEffect;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import com.badbones69.crazycrates.paper.utils.MsgUtils;
import com.ryderbelserion.fusion.paper.api.builder.gui.interfaces.GuiItem;
import net.kyori.adventure.sound.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;

public class GuiButton {

    protected final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final Map<String, String> placeholders;
    private final ConfigurationSection section;
    private final LegacyItemBuilder guiItem;

    private final List<String> commands;
    private final List<String> messages;

    public GuiButton(final ConfigurationSection section, final Map<String, String> placeholders) {
        this.guiItem = new LegacyItemBuilder()
                .withType(section.getString("material", "emerald_block"))
                .setDisplayName(section.getString("name", "No display name found."))
                .setDisplayLore(section.getStringList("lore"));

        this.commands = section.getStringList("commands");
        this.messages = section.getStringList("messages");
        this.placeholders = placeholders;
        this.section = section;
    }

    public @NotNull GuiItem getGuiItem() {
        return this.guiItem.asGuiItem(event -> {
            if (!(event.getWhoClicked() instanceof Player player)) return;

            player.closeInventory(InventoryCloseEvent.Reason.OPEN_NEW);

            this.commands.forEach(command -> MiscUtils.sendCommand(command, this.placeholders));
            this.messages.forEach(message -> MsgUtils.sendMessage(player, MiscUtils.populatePlaceholders(player, message, this.placeholders), false));

            final ConfigurationSection sound = this.section.getConfigurationSection("sound");

            if (sound != null) {
                SoundEffect effect = new SoundEffect(
                        this.section,
                        "sound",
                        "entity.villager.yes",
                        Sound.Source.MASTER
                );

                effect.play(player, player.getLocation());
            }
        });
    }

    public final ConfigurationSection getSection() {
        return this.section;
    }
}