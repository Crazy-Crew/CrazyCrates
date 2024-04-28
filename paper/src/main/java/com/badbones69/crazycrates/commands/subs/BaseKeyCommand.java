package com.badbones69.crazycrates.commands.subs;


/*@Command(value = "keys", alias = {"key"})
@Description("Views the amount of keys you/others have.")
public class BaseKeyCommand extends BaseCommand {

    private final @NotNull CrazyCrates plugin = JavaPlugin.getPlugin(CrazyCrates.class);

    private final @NotNull BukkitUserManager userManager = this.plugin.getUserManager();

    @Default
    @Permission("crazycrates.command.player.key")
    public void viewPersonal(Player player) {
        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(player.getUniqueId())));

        getKeys(player, player, Messages.virtual_keys_header.getMessage(player, placeholders), Messages.no_virtual_keys.getMessage(player));
    }

    @SubCommand("view")
    @Permission("crazycrates.command.player.key.others")
    public void viewOthers(CommandSender sender, @Suggestion ("online-players") Player target) {
        if (target == sender) {
            viewPersonal(target);

            return;
        }

        Map<String, String> placeholders = new HashMap<>();

        placeholders.put("{player}", target.getName());
        placeholders.put("{crates_opened}", String.valueOf(this.userManager.getTotalCratesOpened(target.getUniqueId())));

        String header = Messages.other_player_no_keys_header.getMessage(null, placeholders);

        String content = Messages.other_player_no_keys.getMessage(null, "{player}", target.getName());

        getKeys(target, sender, header, content);
    }

}*/