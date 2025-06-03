package com.badbones69.crazycrates.paper.tasks.crates.types;

import com.badbones69.crazycrates.paper.api.builders.LegacyItemBuilder;
import com.badbones69.crazycrates.paper.api.builders.types.features.CrateSpinMenu;
import com.badbones69.crazycrates.paper.api.enums.other.keys.FileKeys;
import com.badbones69.crazycrates.paper.api.objects.Crate;
import com.badbones69.crazycrates.paper.api.objects.Prize;
import com.badbones69.crazycrates.paper.api.PrizeManager;
import com.badbones69.crazycrates.paper.api.objects.gui.GuiSettings;
import com.badbones69.crazycrates.core.config.impl.ConfigKeys;
import com.badbones69.crazycrates.paper.managers.events.enums.EventType;
import com.badbones69.crazycrates.paper.managers.BukkitUserManager;
import com.badbones69.crazycrates.paper.tasks.crates.CrateManager;
import com.ryderbelserion.fusion.paper.api.scheduler.FoliaScheduler;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.paper.api.builders.CrateBuilder;
import com.badbones69.crazycrates.paper.utils.MiscUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class CsgoCrate extends CrateBuilder {

    private final CrateManager crateManager = this.plugin.getCrateManager();

    private final BukkitUserManager userManager = this.plugin.getUserManager();

    public CsgoCrate(@NotNull final Crate crate, @NotNull final Player player, final int size) {
        super(crate, player, size);
    }

    private final Inventory inventory = getInventory();
    private final Player player = getPlayer();
    private final UUID uuid = this.player.getUniqueId();
    private final Crate crate = getCrate();

    @Override
    public void open(@NotNull final KeyType type, final boolean checkHand, final boolean isSilent, @NotNull final EventType eventType) {
        // Crate event failed, so we return.
        if (isCrateEventValid(type, checkHand, isSilent, eventType)) {
            return;
        }

        final String fileName = this.crate.getFileName();

        final boolean keyCheck = this.userManager.takeKeys(this.uuid, fileName, type, this.crate.useRequiredKeys() ? this.crate.getRequiredKeys() : 1, checkHand);

        if (!keyCheck) {
            // Remove from an opening list.
            this.crateManager.removePlayerFromOpeningList(this.player);

            return;
        }

        // Set the glass/display items to the inventory.
        populate();

        // Open the inventory.
        this.player.openInventory(this.inventory);

        addCrateTask(new FoliaScheduler(this.plugin, null, this.player) {
            int time = 1;

            int full = 0;

            int open = 0;

            @Override
            public void run() {
                if (this.full <= 50) { // When Spinning
                    moveItemsAndSetGlass();

                    playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");
                }

                this.open++;

                if (this.open >= 5) {
                    player.openInventory(inventory);

                    this.open = 0;
                }

                this.full++;

                if (this.full > 51) {
                    if (MiscUtils.slowSpin(120, 15).contains(this.time)) { // When Slowing Down
                        moveItemsAndSetGlass();

                        playSound("cycle-sound", Sound.Source.MASTER, "block.note_block.xylophone");
                    }

                    this.time++;

                    if (this.time == 60) { // When done
                        playSound("stop-sound", Sound.Source.MASTER, "entity.player.levelup");

                        crateManager.endCrate(player);

                        final String material = config.getProperty(ConfigKeys.crate_csgo_finished_material);

                        final ItemStack itemStack = new LegacyItemBuilder(plugin).withType(material.isEmpty() ? Material.GRAY_STAINED_GLASS.getKey().getKey() : material).setDisplayName(" ").asItemStack();

                        setItem(4, itemStack);
                        setItem(22, itemStack);

                        final ItemStack item = inventory.getItem(13);

                        if (item != null) {
                            final Prize prize = crate.getPrize(item);

                            if (crate.isCyclePrize() && !PrizeManager.isCapped(crate, player)) { // re-open this menu
                                new CrateSpinMenu(player, new GuiSettings(crate, prize, FileKeys.respin_gui.getConfiguration())).open();

                                return;
                            } else {
                                userManager.removeRespinPrize(uuid, fileName);

                                if (!crate.isCyclePersistRestart()) {
                                    userManager.removeRespinCrate(uuid, fileName, userManager.getCrateRespin(uuid, fileName));
                                }
                            }

                            PrizeManager.givePrize(player, crate, prize);
                        }

                        crateManager.removePlayerFromOpeningList(player);

                        new FoliaScheduler(plugin, null, player) {
                            @Override
                            public void run() { //todo() use inventory holders
                                if (player.getOpenInventory().getTopInventory().equals(inventory)) player.closeInventory();
                            }
                        }.runDelayed(40);

                        cancel();
                    } else if (this.time > 60) { // Added this due reports of the prizes spamming when low tps.
                        cancel();
                    }
                }
            }
        }.runAtFixedRate(0, 1));
    }

    private void populate() {
        if (this.crate.isGlassBorderToggled()) {
            getBorder().forEach(this::setCustomGlassPane);
        }

        final String material = this.config.getProperty(ConfigKeys.crate_csgo_cycling_material);

        if (!material.isEmpty()) {
            final ItemStack itemStack = new LegacyItemBuilder(this.plugin).withType(material).setDisplayName(" ").asItemStack();

            setItem(4, itemStack);
            setItem(22, itemStack);
        }

        // Set display items.
        for (int index = 9; index > 8 && index < 18; index++) {
            setItem(index, this.crate.pickPrize(this.player).getDisplayItem(this.player, this.crate));
        }
    }

    private void moveItemsAndSetGlass() {
        final List<ItemStack> items = new ArrayList<>();

        for (int i = 9; i > 8 && i < 17; i++) {
            items.add(this.inventory.getItem(i));
        }

        setItem(9, this.crate.pickPrize(this.player).getDisplayItem(this.player, this.crate));

        for (int i = 0; i < 8; i++) {
            setItem(i + 10, items.get(i));
        }

        if (this.crate.isGlassBorderToggled()) {
            getBorder().forEach(this::setCustomGlassPane);
        }
    }

    private List<Integer> getBorder() {
        final String material = this.config.getProperty(ConfigKeys.crate_csgo_cycling_material);

        if (!material.isEmpty()) {
            return Arrays.asList(0, 1, 2, 3, 5, 6, 7, 8, 18, 19, 20, 21, 23, 24, 25, 26);
        }

        return Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 18, 19, 20, 21, 22, 23, 24, 25, 26);
    }
}