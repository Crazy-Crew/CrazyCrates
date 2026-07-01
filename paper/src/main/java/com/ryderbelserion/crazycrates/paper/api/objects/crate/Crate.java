package com.ryderbelserion.crazycrates.paper.api.objects.crate;

import com.ryderbelserion.crazycrates.paper.CrazyCrates;
import com.ryderbelserion.crazycrates.paper.api.enums.DisplayType;
import com.ryderbelserion.crazycrates.paper.api.managers.ButtonManager;
import com.ryderbelserion.crazycrates.paper.api.managers.ItemManager;
import com.ryderbelserion.crazycrates.paper.api.objects.buttons.Button;
import com.ryderbelserion.crazycrates.paper.api.objects.items.DisplayItem;
import com.ryderbelserion.crazycrates.paper.api.objects.other.CratePrize;
import com.ryderbelserion.crazycrates.paper.api.objects.other.CrateSound;
import com.ryderbelserion.crazycrates.paper.api.CratePlatform;
import com.ryderbelserion.fusion.paper.FusionPaper;
import org.bukkit.Server;
import org.jspecify.annotations.NullMarked;
import org.spongepowered.configurate.CommentedConfigurationNode;
import us.crazycrew.crazycrates.api.enums.CrateType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NullMarked
public final class Crate {

    private final CrazyCrates plugin = CrazyCrates.getPlugin();

    private final CratePlatform platform = this.plugin.getPlatform();

    private final ButtonManager buttonManager = this.platform.getButtonManager();

    private final ItemManager itemManager = this.platform.getItemManager();

    private final FusionPaper fusion = this.platform.getFusion();

    private final Server server = this.plugin.getServer();

    private final Map<String, CrateSound> sounds = new HashMap<>();
    private final Map<String, CratePrize> prizes = new HashMap<>();
    private final Map<String, CrateButton> buttons = new HashMap<>();

    private final CommentedConfigurationNode configuration;

    private final DisplayItem displayItem;

    private final CrateType crateType;

    private final String crateName;

    private final CrateGui gui;

    public Crate(final CommentedConfigurationNode configuration, final String crateName) {
        this.configuration = configuration;

        this.crateType = CrateType.getFromName(this.configuration.node("type").getString("CSGO"));

        final CommentedConfigurationNode sound = this.configuration.node("sound");

        this.sounds.put("cycle", new CrateSound(sound.node("cycle-sound")));
        this.sounds.put("click", new CrateSound(sound.node("click-sound")));
        this.sounds.put("stop", new CrateSound(sound.node("stop-sound")));

        this.displayItem = new DisplayItem(this.configuration.node("display"), DisplayType.CRATE);

        final CommentedConfigurationNode buttons = this.configuration.node("buttons");

        for (final Object key : buttons.childrenMap().keySet()) {
            final CommentedConfigurationNode node = buttons.node(key);

            final String buttonKey = node.node("key").getString("");

            if (buttonKey.isBlank()) continue;

            final int slot = node.node("slot").getInt(-1);

            if (slot < 0) continue;

            final Optional<Button> index = this.buttonManager.getButton(buttonKey);

            final String asString = key.toString();

            if (index.isPresent()) {
                this.buttons.put(asString, new CrateButton(index.get(), slot));

                continue;
            }

            this.buttons.put(asString, new CrateButton(new Button(node), slot));
        }

        final CommentedConfigurationNode prizes = this.configuration.node("prizes");

        for (final Object key : prizes.childrenMap().keySet()) {
            final CommentedConfigurationNode prize = prizes.node(key);

            final String name = key.toString();

            this.prizes.put(name, new CratePrize(
                    name,
                    prize
            ));
        }

        this.gui = new CrateGui(this, this.configuration.node("gui"));

        this.crateName = crateName;
    }

    public Optional<CrateSound> getSound(final String type) {
        return Optional.ofNullable(this.sounds.get(type));
    }

    public final Optional<CratePrize> getPrize(final String type) {
        return Optional.ofNullable(this.prizes.get(type));
    }

    public final Optional<CrateButton> getButton(final String type) {
        return Optional.ofNullable(this.buttons.get(type));
    }

    public Map<String, CratePrize> getPrizes() {
        return Collections.unmodifiableMap(this.prizes);
    }

    public Map<String, CrateButton> getButtons() {
        return Collections.unmodifiableMap(this.buttons);
    }

    public CommentedConfigurationNode getConfiguration() {
        return this.configuration;
    }

    public DisplayItem getDisplayItem() {
        return this.displayItem;
    }

    public CrateType getCrateType() {
        return this.crateType;
    }

    public String getCrateName() {
        return this.crateName;
    }

    public CrateGui getGui() {
        return this.gui;
    }
}