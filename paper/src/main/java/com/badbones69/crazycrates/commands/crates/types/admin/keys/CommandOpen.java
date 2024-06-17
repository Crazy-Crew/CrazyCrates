package com.badbones69.crazycrates.commands.crates.types.admin.keys;

import com.badbones69.crazycrates.api.PrizeManager;
import com.badbones69.crazycrates.api.enums.Messages;
import com.badbones69.crazycrates.api.events.PlayerPrizeEvent;
import com.badbones69.crazycrates.api.objects.Crate;
import com.badbones69.crazycrates.api.objects.Prize;
import com.badbones69.crazycrates.api.utils.MiscUtils;
import com.badbones69.crazycrates.commands.crates.types.BaseCommand;
import dev.triumphteam.cmd.bukkit.annotation.Permission;
import dev.triumphteam.cmd.core.annotations.Command;
import dev.triumphteam.cmd.core.annotations.Suggestion;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionDefault;
import us.crazycrew.crazycrates.api.enums.types.CrateType;
import us.crazycrew.crazycrates.api.enums.types.KeyType;
import com.badbones69.crazycrates.config.impl.ConfigKeys;
import java.util.HashMap;
import java.util.Map;

public class CommandOpen extends BaseCommand {

    private boolean isCancelled(Player player, String crateName) {
        if (crateName.isEmpty() || crateName.isBlank()) {
            player.sendRichMessage(Messages.cannot_be_empty.getMessage(player, "{value}", "crate name"));

            return true;
        }

        // Check if player is in opening list first.
        if (this.crateManager.isInOpeningList(player)) {
            player.sendRichMessage(Messages.already_opening_crate.getMessage(player, "{crate}", crateName));

            return true;
        }

        if (MiscUtils.isInventoryFull(player)) {
            player.sendRichMessage(Messages.inventory_not_empty.getMessage(player, "{crate}", crateName));

            return true;
        }

        return false;
    }

    @Command("open")
    @Permission(value = "crazycrates.open", def = PermissionDefault.OP)
    public void open(Player player, @Suggestion("crates") String crateName, @Suggestion("keys") String type) {
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null || crate.getCrateType() == CrateType.menu) {
            player.sendRichMessage(Messages.internal_error.getMessage(player));

            this.plugin.getLogger().severe("An error has occurred: The crate type is null or Menu for the crate named " + crate.getName());

            return;
        }

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

            return;
        }

        KeyType keyType = getKeyType(type);

        boolean hasKey = this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && keyType == KeyType.physical_key ? this.userManager.getTotalKeys(player.getUniqueId(), crate.getName()) >= 1 : this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1;

        // If no key, run this.
        if (!hasKey) {
            //todo() convert this to a bean property!
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                Sound sound = Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.PLAYER, 1f, 1f);

                player.playSound(sound);
            }

            Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{crate}", crate.getName());
            placeholders.put("{key}", crate.getKeyName());

            player.sendRichMessage(Messages.no_keys.getMessage(player, placeholders));

            return;
        }

        // They passed the check.
        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);
    }

    @Command("open-others")
    @Permission(value = "crazycrates.open-others", def = PermissionDefault.OP)
    public void others(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("players") Player player, @Suggestion("keys") String type) {
        // If the command is cancelled.
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null) {
            player.sendRichMessage(Messages.internal_error.getMessage(player));

            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());

            return;
        }

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

            return;
        }

        KeyType keyType = getKeyType(type);

        if (sender == player) {
            open(player, crate.getName(), type);

            return;
        }

        boolean hasKey = this.config.getProperty(ConfigKeys.virtual_accepts_physical_keys) && keyType == KeyType.physical_key ? this.userManager.getTotalKeys(player.getUniqueId(), crate.getName()) >= 1 : this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName()) >= 1;

        if (!hasKey) {
            //todo() convert this to a bean property!
            if (this.config.getProperty(ConfigKeys.need_key_sound_toggle)) {
                Sound sound = Sound.sound(Key.key(this.config.getProperty(ConfigKeys.need_key_sound)), Sound.Source.PLAYER, 1f, 1f);

                player.playSound(sound);
            }

            sender.sendRichMessage(Messages.no_keys.getMessage(sender, new HashMap<>() {{
                put("{crate}", crate.getName());
                put("{key}", crate.getKeyName());
            }}));

            return;
        }

        this.crateManager.openCrate(player, crate, keyType, player.getLocation(), true, false);

        final Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{player}", player.getName());

        player.sendRichMessage(Messages.opened_a_crate.getMessage(player, placeholders));
    }


    @Command("forceopen")
    @Permission(value = "crazycrates.forceopen", def = PermissionDefault.OP)
    public void forceopen(CommandSender sender, @Suggestion("crates") String crateName, @Suggestion("players") Player player) {
        // If the command is cancelled.
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null) {
            player.sendRichMessage(Messages.internal_error.getMessage(player));

            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());

            return;
        }

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

            return;
        }

        this.crateManager.openCrate(player, crate, KeyType.free_key, player.getLocation(), true, false);

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crate}", crate.getName());
        placeholders.put("{player}", player.getName());

        sender.sendRichMessage(Messages.opened_a_crate.getMessage(player, placeholders));
    }

    @Command("mass-open")
    @Permission(value = "crazycrates.massopen", def = PermissionDefault.OP)
    public void mass(Player player, @Suggestion("crates") String crateName, @Suggestion("keys") String type, @Suggestion("numbers") int amount) {
        // If the command is cancelled.
        if (isCancelled(player, crateName)) return;

        // Get the crate.
        Crate crate = getCrate(player, crateName, false);

        // If crate is null, return.
        if (crate == null) {
            player.sendRichMessage(Messages.not_a_crate.getMessage(player, "{crate}", crateName));

            return;
        }

        CrateType crateType = crate.getCrateType();

        // If crate type is null, we return.
        if (crateType == null) {
            player.sendRichMessage(Messages.internal_error.getMessage(player));

            this.plugin.getLogger().severe("An error has occurred: The crate type is null for the crate named " + crate.getName());

            return;
        }

        // Prevent it from working with these crate types.
        if (crateType == CrateType.crate_on_the_go || crateType == CrateType.quick_crate || crateType == CrateType.fire_cracker || crateType == CrateType.quad_crate) {
            final Map<String, String> placeholders = new HashMap<>();

            placeholders.put("{cratetype}", crate.getCrateType().getName());
            placeholders.put("{crate}", crate.getName());

            player.sendRichMessage(Messages.cant_be_a_virtual_crate.getMessage(player, placeholders));

            return;
        }

        final KeyType keyType = getKeyType(type);

        int keys = keyType == KeyType.physical_key ? this.userManager.getPhysicalKeys(player.getUniqueId(), crate.getName()) : this.userManager.getVirtualKeys(player.getUniqueId(), crate.getName());
        int used = 0;

        if (keys == 0) {
            player.sendRichMessage(Messages.no_keys.getMessage(player, new HashMap<>() {{
                put("{crate}", crate.getName());
                put("{key}", crate.getKeyName());
            }}));

            return;
        }

        this.crateManager.addPlayerToOpeningList(player, crate);

        for (;keys > 0; keys--) {
            if (MiscUtils.isInventoryFull(player)) break;
            if (used >= amount) break;
            if (used >= crate.getMaxMassOpen()) break;

            Prize prize = crate.pickPrize(player);

            PrizeManager.givePrize(player, prize, crate);

            this.plugin.getServer().getPluginManager().callEvent(new PlayerPrizeEvent(player, crate, crate.getName(), prize));

            if (prize.useFireworks()) MiscUtils.spawnFirework((player).getLocation().clone().add(.5, 1, .5), null);

            used++;
        }

        if (crateType != CrateType.cosmic) this.userManager.addOpenedCrate(player.getUniqueId(), crate.getName(), used);

        if (!this.userManager.takeKeys(player.getUniqueId(), crate.getName(), keyType, used, false)) {
            this.crateManager.removeCrateInUse(player);
            this.crateManager.removePlayerFromOpeningList(player);

            return;
        }

        this.crateManager.removePlayerFromOpeningList(player);
    }
}