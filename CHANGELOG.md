### Big Changes:
#### If you DO not change this, all your previews will look weird.
- config.yml has had a chance that cannot be automatically done.
  - the customizer specifically the `slot` option, due to recent inventory changes.
  - you must subtract 1 from each option, and start from `slot:0` instead of `slot:1`
- Similar tweaks have to be made with subtracting `1` from each option in the `crate` files
  - Specifically, related to the position of the crates in `/crates` and the position of the tiers in the tier previews
- Why did this change? Inventories naturally start from `0`, and the gui framework I switched to does start from `0`
  - This change allows for much easier inventory management and future features.

### Added:
- Added a new feature to prizes, The ability to set a limit on prizes.
  - If a prize has Max-Pulls set to any number i.e. 1 or higher. 
  - That is limit globally for any player to claim it.
  - Once that limit is reached, It will no longer be winnable.
    - %pulls%, %maxpulls% are 2 new placeholders which can be used in `DisplayLore`, `DisplayName`, `Messages`, `Commands` and the global/per prize broadcast.
  - A lore will be added to any prize that meets the criteria.
    - This message can be edited in messages.yml, and set to empty if you don't want it appended.

An example of what a prize would look like with the `Max-Pulls` option
```yml
    '6':
      # The name of the item to display in the gui.
      DisplayName: "<green>Fancy Shield <gray>| <red>%pulls%<gray>/<red>%maxpulls%"
      # The enchants to display in the gui.
      DisplayItem: "shield"
      # A list of patterns: https://jd.papermc.io/paper/1.21/org/bukkit/block/banner/PatternType.html
      # The patterns don't need to be uppercased. you can type them lowercased along with the colors.
      # Patterns have to be laid out in a specific order, otherwise it won't look right.
      # This also applies to the Items section.
      DisplayPatterns:
        - "base:white"
        - "gradient_up:light_gray"
        - "straight_cross:light_blue"
        - "flower:light_blue"
      # Prize settings
      Settings:
        # The custom model data of the item, -1 is disabled.
        Custom-Model-Data: -1
        # The amount of times this item can be pulled.
        Max-Pulls: 10
```

- Added the ability to have per prize broadcasts and global broadcast for prizes.
  - The permissions used to filter out who can see the broadcast are registered as proper permissions, so they show up in LuckPerms
  - They do get removed when you turn off the per prize broadcast or the global broadcast.

### Removed:
- All legacy color codes are removed, I do not want to maintain it anymore as I've figured out ways around needing it.
  - You can run /crazycrates migrate LegacyColorAll which should migrate all values in `config.yml`, `messages.yml` and all `crate` files.

### Fixed:
- CSGO Crate animation was delayed by 1 tick, for some reason.
- Fixed https://github.com/Crazy-Crew/CrazyCrates/issues/788
- Fixed spacing in migrate command usage.

### Changes:
- Slots in `config.yml`, and slots in each crate file that define the position of the tiers in the tier preview or the buttons in /crates have been changed, so existing gui's will look very weird.
  - All initial configs, You must subtract 1 from each slot. 14 would become 13 as an example. It's a very easy change (there is no real way to migrate your configurations with this)
- Improved /crazycrates migrate internally.
  - ExcellentCrates Migrator has changed significantly, report any bugs you might find. It will convert legacy color codes to MiniMessage.
  - Properly warn the player/sender if the inputted migration type is not valid.
- Removed sections of code related to giving a prize if `Editor-Items`, `Commands` or `Items` were all not found.
  - This would use the `DisplayItem`, `DisplayName`, `DisplayLore`, `DisplayEnchantments` and `DisplayAmount` as the prize.
- Updated the /crazycrates additem command
  - CrazyCrates additem command now supports MiniMessage, regardless of the item format used.
  - This is only happening, as legacy colors have been removed and I did some research to improve things.
- Updated the config option `use-old-editor`, It is now migrated to `use-new-editor`
  - `true` uses the new editor, `false` uses the old one which is more readable. The option should be migrated on startup.
- Updated Vital API.