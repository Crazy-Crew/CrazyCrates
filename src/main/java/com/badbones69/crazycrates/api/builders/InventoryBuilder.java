package com.badbones69.crazycrates.api.builders;

import ch.jalu.configme.SettingsManager;
import com.badbones69.crazycrates.CrazyCrates;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Tier;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.tasks.BukkitUserManager;
import com.badbones69.crazycrates.tasks.PaginationManager;
import com.badbones69.crazycrates.tasks.crates.CrateManager;
import com.ryderbelserion.vital.paper.enums.Support;
import com.ryderbelserion.vital.paper.util.AdvUtil;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import org.bukkit.Server;
import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.craftbukkit.inventory.CraftContainer;
import org.bukkit.craftbukkit.util.CraftChatMessage;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.java.JavaPlugin;
import com.badbones69.crazycrates.config.ConfigManager;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import static java.util.regex.Matcher.quoteReplacement;

public abstract class InventoryBuilder implements InventoryHolder, Listener {

    protected @NotNull final CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    protected @NotNull final BukkitUserManager userManager = this.plugin.getUserManager();

    protected @NotNull final PaginationManager manager = this.plugin.getPaginationManager();

    protected @NotNull final CrateManager crateManager = this.plugin.getCrateManager();

    protected @NotNull final Server server = this.plugin.getServer();

    private Inventory inventory;
    private Player player;
    private String title;
    private Crate crate;
    private int size;
    private int page;
    private List<Tier> tiers;

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size) {
        this.player = player;
        this.title = title;
        this.size = size;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtil.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate) {
        this.player = player;
        this.title = title;
        this.size = size;

        this.crate = crate;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtil.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, final int page, @NotNull final Crate crate) {
        this.player = player;
        this.title = title;
        this.size = size;
        this.page = page;

        this.crate = crate;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtil.parse(inventoryTitle));
    }

    public InventoryBuilder(@NotNull final Player player, @NotNull final String title, final int size, @NotNull final Crate crate, @NotNull final List<Tier> tiers) {
        this.player = player;
        this.title = title;
        this.size = size;

        this.crate = crate;
        this.tiers = tiers;

        String inventoryTitle = Support.placeholder_api.isEnabled() ? PlaceholderAPI.setPlaceholders(getPlayer(), this.title) : this.title;

        this.inventory = this.server.createInventory(this, this.size, AdvUtil.parse(inventoryTitle));
    }

    public InventoryBuilder() {}

    public boolean overrideMenu() {
        SettingsManager config = ConfigManager.getConfig();

        if (config.getProperty(ConfigKeys.menu_button_override)) {
            List<String> commands = config.getProperty(ConfigKeys.menu_button_command_list);

            if (!commands.isEmpty()) {
                commands.forEach(value -> {
                    String command = value.replaceAll("%player%", quoteReplacement(this.player.getName())).replaceAll("%crate%", quoteReplacement(this.crate.getName()));

                    MiscUtils.sendCommand(command);
                });

                return true;
            }

            if (MiscUtils.isLogging()) this.plugin.getLogger().warning("The property " + ConfigKeys.menu_button_command_list.getPath() + " is empty so no commands were run.");

            return true;
        }

        return false;
    }

    public abstract InventoryBuilder build();

    public abstract void run(InventoryClickEvent event);

    @EventHandler
    public void onPlayerClick(InventoryClickEvent event) {
        run(event);
    }

    public void size(final int size) {
        this.size = size;
    }

    public final int getSize() {
        return this.size;
    }

    public void setPage(final int page) {
        this.page = page;
    }

    public final int getPage() {
        return this.page;
    }

    public @NotNull final Crate getCrate() {
        return this.crate;
    }

    public void title(@NotNull final String title) {
        this.title = title;
    }

    public final boolean contains(@NotNull final String message) {
        return this.title.contains(message);
    }

    public @NotNull final Player getPlayer() {
        return this.player;
    }

    public @NotNull final List<Tier> getTiers() {
        return this.tiers;
    }

    public @NotNull final InventoryView getView() {
        return getPlayer().getOpenInventory();
    }

    public void sendTitleChange() {
        ServerPlayer entityPlayer = (ServerPlayer) ((CraftHumanEntity) getView().getPlayer()).getHandle();
        int containerId = entityPlayer.containerMenu.containerId;
        MenuType<?> windowType = CraftContainer.getNotchInventoryType(getView().getTopInventory());
        entityPlayer.connection.send(new ClientboundOpenScreenPacket(containerId, windowType, CraftChatMessage.fromJSON(JSONComponentSerializer.json().serialize(AdvUtil.parse(this.title)))));
        getPlayer().updateInventory();
    }

    @Override
    public @NotNull final Inventory getInventory() {
        return this.inventory;
    }
}