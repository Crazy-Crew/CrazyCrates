## Update History
Date format: (YYYY-MM-DD)

<details>
  <summary>v4.0.2 (1.21.1) - 2024-10-12</summary>

### Changes:
- Updated `use-different-random` comment in `config.yml`.
- Add more verbose logging, and an extra safety net to Casino Crate.
  - This requires `is_verbose` set to true in `vital.yml`

### Fixed:
- Properly including calculate the total weight of the tiers for casino crate.
- Properly calculate the per tier prize pools total weight, for things like Casino or Cosmic Crate.

</details>

<details>
  <summary>v4.0.1 (1.21.1) - 2024-10-10</summary>

### Fixed:
- Fixed an issue with infinite re-spins on join due to using temporary cache.
- Fixed case-sensitive issue with /crazycrates claim

</details>

<details>
  <summary>v4.0 (1.21.1) - 2024-10-07</summary>

### Breaking Changes:
- The weight system has been merged in, which effectively replaces the Chance/Max Range system.
  - You must run the command /crazycrates migrate WeightMigration, which will convert your configurations.
  - https://docs.crazycrew.us/docs/plugins/crazycrates/guides/crates/weight-system

### Additions:
- Added the ability to log the plugin actions to file i.e. crates.log and keys.log... A list of events currently tracked
  - event_key_given
  - event_key_sent
  - event_key_received
  - event_key_transferred
  - event_key_removed
  - event_key_taken
  - event_key_taken_multiple
  - event_crate_opened
  - event_crate_force_opened
  - event_crate_mass_opened
- If you notice anything not tracking right, or lacking tracking. Create an issue.
- The files will zip on /crazycrates reload, plugin shutdown, plugin startup.
  - This avoids the files getting large, and leading to issues with crashing the server.
  - It mimics how Paper handles their .log files...
- Added /crazycrates claim <prize> which claims prizes they didn't get from respins

### Changes:
- Added config options, which allow you to configure slots 4 and 22 above the prize in `csgo` crate
- Added the ability to run commands with the gui customizer.
- Added the ability to respin prizes to each crate file, if the option is enabled.
```yml
Crate:
  # Global Settings
  Settings:
    # Settings related to rewards.
    Rewards:
      # Should a yes/no popup be made, to ask if they want to keep the prize?
      Re-Roll-Spin: false
      # Should there be a limit to how many times they can re-roll?
      Permission:
        # Should this be enabled?
        Toggle: false
        # Should this persist restarts? i.e. writes to disk the amount of respins, and reads the amount of respins.
        Persist: false
        # This will define how many permissions will be registered to the server per crate.
        # i.e. crazycrates.respin.<crate_name>.1-20
        # It will simply register multiple permissions, so it shows up in things like LuckPerms.
        Max-Cap: 20
```
- You can view an example of this in `examples/crates/CrateExample.yml`
- If `Permission.Persist` is set to true, we will write to disk, and the re-spins will be considered global
  - If the option is set to false, it will be cached... and allow re-spins for X amount every time they open a crate.
- You must have `Re-Roll-Spin` set to true to allow re-rolls.
- You must have `Permission.Toggle` set to true, for it to be permission based.
  - The higher the permission, the more spins they have.
  - Internally, we loop through a player's permissions.... and find the highest matching one with `crazycrates.respin.<crate_name>.<amount>`
  - The permissions will be registered on startup, and on /crates reload if not found.
    - We also unregister on /crates reload, if you set `Permissions.Toggle` to false.
  - `<amount>` is the `Max-Cap`, It will not go any higher... The higher that number is, the heavier the permission checks.
- Crate Types such as Cosmic Crate, Casino Crate, QuadCrate, and WarCrate do not have support for re-spins
  - Casino Crate has 3 prizes, the gui currently only supports 1 prize.
  - Cosmic Crate has 4 prizes that you pick, the gui currently only supports 1 prize.
  - QuadCrate has 4 prizes, the gui currently only supports 1 prize.
  - WarCrate is in a similar situation, where it's picked prizes.
- The complexity for these crate types above will take some time to add it in while it not being a train wreck.

</details>

<details>
  <summary>v3.8.3 (1.21.1) - 2024-09-24</summary>

### Fixed:
- Fixed an issue, where if the border was toggled off. and you didn't have a second page, a glass pane would be there.
  - It will simply be air now until the border is on.

</details>

<details>
  <summary>v3.8.2 (1.21.1) - 2024-09-18</summary>

### Fixed:
- Fixed an issue with pagination on prizes.
- Fixed an issue with the next/main/back buttons being static not dynamic.
  - They would not re-size with the gui if you changed the row size.

</details>

<details>
  <summary>v3.8.1 (1.21.1) - 2024-09-17</summary>

### Changes:
- No longer do hefty checks on PlayerMoveEvent if the player is not in the session for QuadCrate

</details>

<details>
  <summary>v3.8 (1.21.1) - 2024-09-14</summary>

### Big Changes: ( only for existing configurations )
#### If you DO not change this, all your previews will look weird.
- config.yml has had a change that cannot be automatically done.
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
      # Prize display preview settings
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

</details>

<details>
  <summary>v3.7.4 (1.21.1) - 2024-07-30</summary>

### Fixed:
- Fixed an issue with some commands not properly validating virtual keys

### Deprecation
- Deprecated `PlayerPrizeEvent(player, crate, crateName, prize)`
  - Please use `PlayerPrizeEvent(player, crate, prize)` as the crateName is already passed through via the `crate` object.

</details>

<details>
  <summary>v3.7.3 (1.21.1) - 2024-08-25</summary>

### Fixed:
- Fixed a class cast exception with the ItemBuilder when using `Data:` or `DisplayData:`

</details>

<details>
  <summary>v3.7.2 (1.21.1) - 2024-08-24</summary>

### Fixed:
- Fixed an issue with /crazycrates migrate ExcellentCrates
  - Added a missing configuration option, that is required in our crate configs.

</details>

<details>
  <summary>v3.7.1 (1.21.1) - 2024-08-23</summary>

### Changes:
- Removed all getItemMeta/hasItemMeta calls for checking PersistentDataContainer
  - We now check ItemStack#PersistentDataContainerView which no longer relies on ItemStack#getItemMeta
  - TLDR: stonks

### Added:
- Added a toggle, which allows you to revert the chance made previously.
  - `crazycrates.open.<crate_name>` -> `crazycrates.deny.open.<crate_name>`
- The configuration option can be found in the `config.yml` i.e. `root.use-new-permission-system` which defaults to false
  - This option is subject for removal however toggled like this for now, `false` means the old system i.e. `crazycrates.open.<crate_name>` is back.
  - The new way i.e. `crazycrates.deny.open.<crate_name>` will be removed in the next version of Minecraft!
  - All further experiments will be behind toggles like this one going forward.

</details>

<details>
  <summary>v3.7 (1.21.1) - 2024-08-22</summary>

### Added:
- Ability to migrate crate configurations from ExcellentCrates.
  - active locations from ExcellentCrates are also migrated!
- Added a new feature where the `RequiredKeys` can also take that amount of keys [#755](https://github.com/Crazy-Crew/CrazyCrates/issues/755)
  - `use-required-keys` in `config.yml` has to be true for that to take effect.
- Added %chance% placeholders to prizes and tiers

#### Crate Config Changes:
- `Crate.CrateName` is deprecated, and has been replaced by `Crate.Name`
  - The options were duplicate, and one wasn't used which annoyed me.
  - `Crate.CrateName` will be removed in the next version of Minecraft!
  - You can run /crazycrates migrate CratesDeprecated to migrate deprecated options.
```yml
Crate:
  # https://docs.crazycrew.us/docs/plugins/crazycrates/misc/crate-types

  # Make sure to check out the wiki for anything not explained here.
  # https://docs.crazycrew.us/docs/category/crazycrates

  # See CosmicCrate.yml to see how the Cosmic CrateType works.
  CrateType: Casino
  # Name of the Inventory if a GUI crate.
  CrateName: "<dark_blue>Casino Crate" # Deprecated, but will still work
  # Name of the item in the GUI.
  Name: "<bold><dark_blue>Casino Crate</bold>" # This is what is used now if CrateName isn't found
  # The lore of the item in the GUI.
  Lore:
  - "<gray>This crate contains strange objects."
  - "<gray>You have <gold>%keys% keys <gray>to open this crate with."
  - "<gray>You have opened this crate: <gold>%crate_opened% times"
  - "<gray>(<yellow>!<gray>) Right click to view rewards."
```
- `Crate.Preview-Name` is deprecated, and has been replaced by `Crate.Preview.Name`
  - The option was meant to always be under `Crate.Preview`
  - `Crate.Preview-Name` will be removed in the next version of Minecraft!
  - You can run /crazycrates migrate CratesDeprecated to migrate deprecated options.
```yml
  Preview:
    # The name of the inventory for the preview menu.
    Name: "<green>Basic Crate Preview" # moved it under Preview
    # Turn on and off the preview for this crate.
    Toggle: true
    # How many lines should the preview be? You can use 1-6.
    ChestLines: 6
    Glass:
      # Turn the glass border in the preview on and off.
      Toggle: true
      # The name of the border item.
      Name: " "
      # The item that shows in the border. Can be glass or any other item.
      Item: "gray_stained_glass_pane"
      # The custom model data of the item, -1 is disabled.
      Custom-Model-Data: -1 
```

### Fixes:
- Fixed a rare issue where you weren't able to open QuickCrate
- Fixed a duplication issue with CosmicCrate
- Fixed an issue where CMI likely wouldn't be detected

### Changes:
- Right click now opens the crate menu as well.
- Checked location strings instead of object ids
- Simplified multiple location getters, don't need to get the same location 3 times if we aren't changing it.
- `{crate}` in messages will now return `Crate.Name` instead of the file name
  - Cleaned up internals related to sometimes, the file name being used along with bad naming schemes.
  - `Crate#getName()` is now `Crate#getFileName()` while `Crate#getCrateInventoryName()` is `Crate#getCrateName()`
- Check if the prizes section is empty before opening a crate, prevents unneeded chance calculation.
- Overhauled the `/crazycrates migrate` command, sends a more detailed message of what was migrated.
  - files that show up red failed to migrate while files that are green succeeded,
  - it also tells you the migration type you picked when running the command, while also reloading the plugin!
  - Only saving to file, if we find anything that needs to be migrated with `CratesDeprecated` option
  - Only migrate `Editor-Items`, if `use-old-editor` in the `config.yml` is set to `false`
- A lot of other changes were internal clean up, I was merely being a Janitor. functionality should not change.
- Deprecated `use-minimessage` in `config.yml`, it will be removed in the next major version of minecraft
  - The library (made by me), now has its own directory much like bStats. Each plugin using it will get a config generated inside it.
  - You simply after `use-minimessage` is removed will have to edit that file instead which is `Vital/crazycrates-config.yml`
  - Once the option `use-minimessage` is removed, setting `is-legacy` to false will allow MiniMessage

</details>

<details>
  <summary>v3.6.1 (1.21.1) - 2024-08-10</summary>

### Added:
- Bumped minecraft version to 1.21.1
  - You should update to 1.21.1 as soon as possible, there is no changes to prevent any plugins from being used on 1.21.1
  - This version is a minor version fixing bugs, it's plug and play.

### Changes:
- Properly relocate the command framework
- CsgoCrate now when the task is finished, sets an item below and above the prize you won.
  - In the future, this will be configurable. I just don't know what to call it.
- Updated permission description
- Updated exclude.give-all to default to FALSE, [#774](https://github.com/Crazy-Crew/CrazyCrates/pull/774)
- Bumped CMI dependencies

### Fixed:
- Typo in config options
- Adventure api issue [#770](https://github.com/Crazy-Crew/CrazyCrates/pull/770)
- CsgoCrate was missing 2 glass panes [#772](https://github.com/Crazy-Crew/CrazyCrates/pull/772)
- CsgoCrate was not animating the glass panes

</details>

<details>
  <summary>v3.6 (1.21) - 2024-07-30</summary>

### Added:
- Added missing configurable messages to places around the plugin, all messages should now be configurable.
- Added a new config option where you can decide to send messages in chat or in the actionbar.
  - Messages that send a list to chat will by default never be sent to actionbar as it would not look pretty.
- Added a new placeholder, `{required_amount}` to `crates.requirements.not-enough-keys`
- Added another new placeholder, `{key}` to `crates.requirements.not-enough-keys`, [#756](https://github.com/Crazy-Crew/CrazyCrates/issues/756)
  - This placeholder returns the name of the key.
- Added a new migration type which converts deprecated fields in the crate files.
- Added 2 new toggles to the `config.yml` which you can find at the top of the file.
  - The `use-old-editor` requires `use-minimessage` to be false as it's uses legacy color codes.
- Added per prize broadcast, this will send a message to every player on the server.
```yml
    '5':
      # The display name of the item.
      DisplayName: "<yellow>$1,000"
      # The item to display in the gui.
      # The enchanted book will function with the enchants properly in an anvil.
      DisplayItem: "sunflower"
      # Prize display preview settings
      Settings:
        # The custom model data of the item, -1 is disabled.
        Custom-Model-Data: -1
        # Broadcast a message to the server
        Broadcast:
          # If the messages should be sent.
          Toggle: false
          # The messages to broadcast.
          Messages:
            - '<red>%player% won the prize <yellow>%reward%.'
          # If the player has this permission, they don't get the broadcast.
          Permission: 'your_permission' 
```
- Added optional arg for `Player` with crazycrates debug, so you can use it in console.
- Added missing message notifying an item was added using /crates additem

### Changes:
- The permission check for whether a player can open a crate has been changed.
  - `crazycrates.open.<crate_name>` is now `crazycrates.deny.open.<crate_name>`
  - The crate name is case-sensitive, so it must match exactly the crate name in the `crates` folder
  - If the file name is CrateBeans.yml, it must be `crazycrates.deny.open.CrateBeans`
- If a message in the `messages.yml` is blank, it will not send the message.
- Update default message for `crates.crate-no-permission`
- Update some comments because of grammar.
- Update logger message when the `CrateOpenEvent` is cancelled to be more verbose.

### Fixed:
- Wheel Crate animation now spins properly. [#764](https://github.com/Crazy-Crew/CrazyCrates/pull/764)
- Roulette Crate inventory size is now normal. [#765](https://github.com/Crazy-Crew/CrazyCrates/pull/765)
- Don't give 2 prizes if the editor items isn't empty.
- Casino/Cosmic crate tier previews would share total items causing pagination to appear despite the inventory not being full.

### Deprecations:
- Deprecated `{key_amount}` and replaced it with `{required_amount}` in `crates.requirements.not-enough-keys`
  - `{key_amount}` will stop working in the next major version of Minecraft.

</details>

<details>
  <summary>v3.5.9 (1.21) - 2024-07-23</summary>

### Added:
- Ability the ability to make items glow in `Items`
```yml
Items:
  - 'Item:spawner, Glowing:true'
```

### Fixed:
- The config option for `verbose-logging` was not applied to some parts of the plugin.

### Changes:
- No longer add the contents of `DisplayData` to the `Items` section on `/crazycrates reload`
- Lowercase shield pattern types and colors which also fixed a display issue, so previous shield pattern/color configs work. They no longer need to be typed like GRADIENT_UP:LIGHT_GRAY, you can simply type gradient_up:light_gray
- Deprecated `Patterns` in favor of `DisplayPatterns`, it will be removed in the next major version of Minecraft.
```yml
    '6':
      # The name of the item to display in the gui.
      DisplayName: "<green>Fancy Shield"
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
```

</details>

<details>
  <summary>v3.5.8 (1.21) - 2024-07-22</summary>

### Added:
- Ability to define spawner type in prizes
```yml
    '1':
      # The name of the item to display in the gui.
      DisplayName: "<green>Creeper Spawner"
      # The enchants to display in the gui.
      DisplayItem: "spawner"
      # Prize display preview settings
      Settings:
        # The custom model data of the item, -1 is disabled.
        Custom-Model-Data: -1
        # The type of mob for the spawner.
        Mob-Type: creeper
      # The amount to display in the gui.
      DisplayAmount: 1
      # The max range i.e. 15/100 = 15% chance to win.
      MaxRange: 100
      # The chance to win i.e. 15%
      Chance: 23
      Items:
        - 'Item:spawner, Mob:creeper'
```

### Changes:
- Play knockback/sounds if they don't have the required keys.
- Removed runtime dependency loader.

### Cosmic Crate:
- Cosmic Crate was changed back in March for the calculation of tiers to be handled when you open the inventory, so that when picking a mystery crate. The choice would actually matter!
- It has been brought to my attention of being able to skimp it using client side mods. this has been addressed, the mods will no longer be able to skimp it by seeing item differences.
- The calculation still happens but, the picks are stored internally to the player's uuid in a cache which clears when the player quits, the inventory closes or the crate task ends for X reason.

### Fixed:
- Issue with file not found on first install.
- Issue where examples folder wasn't being created properly.
- Issue with shields not getting color or patterns.
- `Wonder` crate type never playing the cycle sound.
- `Wheel` crate type playing the stop sound twice.
    - `Wheel` crate type not playing the cycle sound as most people have the client music muted.
- `War` crate type played the cycle sound a bit early.

</details>

<details>
  <summary>v3.5.7 (1.21) - 2024-07-18</summary>

### Fixed:
- Issue with /crazycrates migrate.
- Multiple null checks in commands.
- Issue with invalid argument output not outputting the proper syntax.

### Changes:
- Updated migrator command format.
- Updated invalid argument output to just describe it better.

</details>

<details>
  <summary>v3.5.6 (1.21) - 2024-07-18</summary>

### Fixed:
- Re-worked how display names are handled for items/previews, This format now works properly and stacks with vanilla items.
```yml
    '1':
      # The item to display/give in the gui.
      DisplayItem: "diamond"
      # The amount to display/give
      DisplayAmount: 3
      # The max range i.e. 25/100 = 15% chance to win.
      MaxRange: 100
      # The chance to win i.e. 25%
      Chance: 25
```
- Applied a bandaid to quadcrates
- Use correct crate name for {crate} when a crate location already exists in `/crazycrates set <crate>`
- Fixed npe with /keys view, player name wasn't supplied, so it freaked out.
- Fixed npe with placeholder parsing in messages.

### Changes:
- Optimize display reward above quad/quick crate

</details>

<details>
  <summary>v3.5.5 (1.21) - 2024-07-15</summary>

### Fixed:
- Issue with ItemBuilder not allowing further modification of the Oraxen items.

</details>

<details>
  <summary>v3.5.4 (1.21) - 2024-07-15</summary>

### Changes:
- When interacting with a crate previously, it would check both virtual and physical keys, but it was a coin flip on what it would use, the interaction functions the same now as `/crates` in terms of hierarchy... physical keys are checked first then virtual keys.
- If you hold a key that can't open the crate you are looking at, it will instead default to checking your virtual keys if the config option is enabled, if a key is found... it will open the crate using virtual keys. as always, please report any bugs.

### Fixed:
- Cancel the key check event if the checks find a player does not have enough keys.
- Simplify key checks so virtual keys can work, this removes an unneeded physical key check as we were checking it twice? why...
- Config comments for `physical-crate-accepts-physical-keys` and `virtual-crate-accepts-physical-keys` in `config.yml` were incorrect.

</details>

<details>
  <summary>v3.5.3 (1.21) - 2024-07-15</summary>

### Changes:
- The fix below required preview to be only opened through left click so right click can function as for only opening the crate.

### Fixed:
- The key check on right-clicking a crate was working, however we didn't inform the player they had no key.

</details>

<details>
  <summary>v3.5.2 (1.21) - 2024-07-12</summary>

### Fixed:
- Issue with file manager not properly loading/reloading files.

</details>

<details>
  <summary>v3.5.1 (1.21) - 2024-07-11</summary>

### Fixed:
- Apply `MaxStackSize` to the player's inventory when using Player#addItem, so now instead of 99 items popping up in the inventory if giving 99 keys or any items, it'll split 64/35
  - Spigot for some odd reason made Player#addItem use the max stack size for the inventories. #hardforkwhen

### Changes:
- Simplified parsing messages internally with placeholders

</details>

<details>
  <summary>v3.5 (1.21) - 2024-07-11</summary>

### Changes:
- All file read/writes operations are actually moved off the main thread.

</details>

<details>
  <summary>v3.4.9 (1.21) - 2024-07-10</summary>

### Removed:
- `console_prefix` config option no longer needed.

### Changes:
- Use component logger for startup dependencies.

### Fixed:
- Issue with PlaceholderAPI.

</details>

<details>
  <summary>v3.4.8 (1.21) - 2024-07-7</summary>

### Changed:
- Updated how interaction with crates has been handled.

### Fixed:
- Prevents placeable blocks from being placed on blocks if a key like tripwire hook or candle.

</details>

<details>
  <summary>v3.4.7 (1.21) - 2024-07-6</summary>

### Added:
- A different way to apply `CustomModelData` to an item, this addition avoids an error that isn't an error from appearing. `-1` means no custom model data will be applied!
  - Our documentation will be updated with the new format sometime this weekend however you can keep using the old format.
  - The old format `tripwire_hook#custom_model_data` will continue to work however consider this the deprecation notice.
  - Try and make an effort to use the new format please, you can look in the `examples` folder to see some applications of the format.

</details>

<details>
  <summary>v3.4.6 (1.21) - 2024-07-5</summary>

### Changes:
- Deprecation warning for `Lore` -> `DisplayLore` is now more verbose, it'll tell you the prize name and crate file now.

</details>

<details>
  <summary>v3.4.5 (1.21) - 2024-07-5</summary>

### Changes:
- `verbose_logging` in `config.yml` now applies to everything including errors, turn it on if something isn't working.

</details>

<details>
  <summary>v3.4.4 (1.21) - 2024-07-1</summary>

### Fixed:
- Player Heads were not stacking previously, they should now.

</details>

<details>
  <summary>v3.4.3 (1.21) - 2024-06-24</summary>

### Fixed:
- `/crazycrates giveall` did not have a permission requirement.

</details>

<details>
  <summary>v3.3.1 (1.20.6) - 2024-06-25</summary>

### Fixed:
- `/crazycrates giveall` did not have a permission requirement.
- Items not stacking with vanilla items if obtained through `Items`

</details>

<details>
  <summary>v3.0-v3.3 (1.20.6) - 2024-06-25</summary>

### Added:
- 1.20.6 support

### Removed:
- 1.20.4 support

### Fixed:
- War Crate no longer bugs out if you close the inventory.
- Files were not loading properly on Linux.
- Uppercase player name in default files to avoid some stupid error.
- Virtual Keys were not being taken from offline players.
- add {key} placeholder to /crazycrates open-others
- Missing placeholder {key} in /crates mass-open
- Temporarily commented out code for direct ItemsAdder support until LoneDev is finished making any changes they would like to.
- New players if the config option Crate.StartingKeys is not 0 weren't getting keys.
- Removing a file from the crates folder and then running /crazycrates reload, It would yell about file not found.
- Lore was not properly handled when using /crates additem.
- Updated the message in commands, instead of misc.no-virtual-keys, It will be using misc.no-keys message.
- Send the message to the command sender instead of the player when using /crates forceopen.
- [b25b867](https://github.com/Crazy-Crew/CrazyCrates/commit/b25b867) Issue with the preview not working for casino/cosmic tiers (#726)
- [46e6dba](https://github.com/Crazy-Crew/CrazyCrates/commit/46e6dba) Add /crazycrates forceopen back (#715)
- [5940d29](https://github.com/Crazy-Crew/CrazyCrates/commit/5940d29) Compile issue with workflows (#724)
- [d9a9f49](https://github.com/Crazy-Crew/CrazyCrates/commit/d9a9f49) Cosmic crate (#716)
- [854efe6](https://github.com/Crazy-Crew/CrazyCrates/commit/854efe6) Error on player quit (#719)
- Issue with QuickCrate where holograms were stacking.
- Issue where the display name of the item above QuickCrate had [] around it i.e. [Diamond Helmet]
- open-others shouldn't be usable by everyone.
- Default casino crate.
- Update fallback sound.
### Added:
- Support for heads from HeadDatabase by Arcaniax! Check the CrateExample.yml in the examples/crates folder for how to use HeadDatabase heads.
### API:
- Bumped crazycrates api to 0.7
### Breaking Changes:
- All items ids used for potions, materials, blocks, trim materials/patterns and sounds etc. have all been changed.
  - A list of sounds: https://minecraft.wiki/w/Sounds.json#Java_Edition_values, **Custom Sounds from resource packs are also supported!**
- Enchantments instead of `PROTECTION_ENVIRONMENTAL` and `DAMAGE_ALL`, It would be `protection` and `sharpness`
### Changes:
- Updated to [2.2.0](https://modrinth.com/plugin/fancyholograms/version/2.2.0) FancyHolograms, they made a breaking change in the api so all previous versions of FancyHolograms will no longer work.
- Command / General Permissions have been updated!
  - You can find a list of permissions @ https://docs.crazycrew.us/docs/plugins/crazycrates/commands/permissions
- Update the order some if checks go in to prevent potentially unnecessarily heavy calls when not needed.
- Removed unsupported-settings from the example config.yml
- War/Cosmic Crate listeners for PlayerCloseEvent have been combined and had unnecessary method calls reduced.
- Ability to set update intervals for holograms, -1 disables it.
- /crazycrates migrate <crate> <type> which should update materials/enchants/trim materials to mojang mapped ids.
- /crazycrates set <crate> on a location already taken will tell you that you that a location already exists.
- Allow left/right-clicking for locations created by /crazycrates set Menu.
- Updated the /crazycrates additem command to support any item you can make in-game, it will still allow you to apply other configuration to the item added like your own custom lore.
- This change required a few other tweaks in some areas so please report any oddities to our Github Issues tab.
```yml
    '7':
      DisplayName: '<red>This is a name.'
      DisplayLore:
        - '<yellow>This is a lore.'
      DisplayEnchantments:
        - 'sharpness:5'
      DisplayData: H4sIAAAAAAAA/53PQQrCMBAF0N8mQo0giEtv4sqFZ3Bb0iRiaDNTkhS8vTaI1KV0OfDfzB8FCOyuOuubi8kzAftXg9pbnIInZ6K+53M3aNO36TENvYttx0+BjeGJMoBKQRkOI5OjnLY4LswwZT1PUc05KSDTwIVAQfrswufOYQHW68Bkf7ZfSr+vq1a6eqUT/7umvPUGDbb6oU0BAAA=
      MaxRange: 100
      Chance: 10
```
- Note: Items used still have different restrictions like Shulker Boxes do not glow for example. If you can't do it in Vanilla Minecraft through /minecraft:give, our plugin can't either.
- Deprecated usage of `Lore` in prizes in favor of `DisplayLore`, you will be given a warning in console if using. `Lore` will be removed in the next major version of Minecraft (1.22)

</details>

<details>
  <summary>v2.1.6 (1.20.4) - 2024-06-10</summary>

## Quick Note:
[Migrate your configurations to MiniMessage!](https://toolbox.helpch.at/converters/legacy/minimessage), this was changed in [Version 2.1](https://modrinth.com/plugin/crazycrates/version/2.1), please take a gander at our previous changelogs and as always, contact support if you need assistance.

### Fixed:
- The migrator for Editor-Items would cause an error on fresh install.

</details>

<details>
  <summary>v2.1.5 (1.20.4) - 2024-06-10</summary>

### Fixed:
- Editor-Items are now migrated on start-up to a new format to prevent an error.

</details>

<details>
  <summary>v2.1.4 (1.20.4) - 2024-05-25</summary>

### Fixed:
- Files were not loading properly on Linux.
- Holograms duplicated with QuickCrate.
- Prize Names that hovered above QuickCrate had [] around them.

</details>

<details>
  <summary>v2.1.3 (1.20.4) - 2024-05-14</summary>

### Added
- Translation keys on materials, so it respects client language settings.

### Fixed
- Startup errors preventing the use of the plugin.

</details>

<details>
  <summary>v2.1.2 (1.20.4) - 2024-05-9</summary>

### Fixed:
- Immutable error when doing /crates additem on a prize already existing.

</details>

<details>
  <summary>v2.1.1 (1.20.4) - 2024-05-8</summary>

### Fixed:
- /crazycrates debug/additem allowed you to use Menu as an argument.

</details>

<details>
  <summary>v2.1 (1.20.4) - 2024-04-27</summary>

[Migrate your configurations to MiniMessage!](https://toolbox.helpch.at/converters/legacy/minimessage)

## Previous Breaking Changes:
- Added minimessage support which replaces legacy color codes like &7 or &c
  - CMI/DecentHolograms do not support MiniMessage so you still have to use legacy color codes for that.

- [b290d54](https://github.com/Crazy-Crew/CrazyCrates/commit/b290d54) Updated /cc additem to be much more precise and friendly -> This will likely change again in the next version of CrazyCrates for 1.20.6, The item internals have changed.

> Previously added rewards will still work for now. The new /cc additem currently only supports 1 item at a time but it's an improvement from before. DisplayAmount is what tells the plugin how much of the item to give so that is required. It will default to 1!
>
> If you add an `Items:` section, It will use that instead for rewards and treat the nbt tag as if it was just purely for display.

## Previous Additions:
- Added [folia](https://github.com/Crazy-Crew/CrazyCrates/pull/658) support.
- Added the ability to color the background using hex colors of holograms. It only works with CMI and FancyHolograms.
  - You can check the examples/crates folder under Hologram section for an example of how to add colors.
  - `transparent` is an option that can be used as well for see through holograms which is the default.
- Add the option to HideItemFlags in filler glass and crate preview items.
- Add config option to turn off the auto updating of examples folder.

#### Extra Item Options:
```yml
    1:
      DisplayName: '<red>Porkchop'
      DisplayItem: 'PORKCHOP'
      DisplayAmount: 4
      Chance: 60
```

> If you simply want to give basic items without the need for using `Items:` or `Commands:`, You can configure a prize like this and it will give 4 porkchop.
>
> DisplayAmount defines how many items to give, DisplayItem defines the material to give to the player.
>
> You cannot have `commands` or `items` while using these type of format for giving items.

## Removal:
- Removed the config option crate.unsupported-settings.old-key-checks as a bug I fixed broke what this setting was used for.

## Previous Changes:
- Updated how holograms are handled. FancyHolograms should be less finnicky and CMI should perform better.
- Ability to set `Chance` in crate fields to -1 to use filler items.

#### Plugin Support:
- Add placeholder api support to broadcast message in each crate file.
- Added support for FancyHolograms by Oliver.
- Add support for PlaceholderAPI in key displayname/lores.

## Previous Fixes:
- Fixed double lines with decentholograms.
- Fixed an issue where list messages would have an extra line at the end.
- Fixed an issue with materials not being recognized.
- Fixed a bug where keys did not have lores.
- Fixed an issue where if display names matched, it would not give the right prize.
- Fixed an issue with casino crate where you could open a casino crate without the key.
- Temp fix for double message when trying to open a crate with key in off hand. This means for the time being, Keys cannot be used in off hand for physical crates.
</details>

<details>
  <summary>v2.0.5 (1.20.4) - 2024-04-25</summary>

### Fixed:
- Prizes were being picked incorrectly as we were checking if the display name of prizes matched when picking a prize.​

</details>

<details>
  <summary>v2.0.4 (1.20.4) - 2024-04-25</summary>

### Fixed:
- Casino Crate was allowed to be opened without a key.

</details>

<details>
  <summary>v2.0.3 (1.20.4) - 2024-04-17</summary>

### Fixed:
- Leather Armor not being colored.

</details>

<details>
  <summary>v2.0.2 (1.20.4) - 2024-04-17</summary>

### Fixed:
- Placeholder issue in commands like /key by using the right config path.
- Key Name if not found in the config would throw an NPE.

</details>

<details>
  <summary>v2.0.1 (1.20.4) - 2024-04-14</summary>

### Fixed:
- Setting crate type menu tried to create a hologram.

</details>

<details>
  <summary>v2.0 (1.20.4) - 2024-04-13</summary>

### Big Changes:
- Replaced all instances of NBT-API with PersistentDataContainer. Old Keys will no longer work.
- We do not check if a key has lore/names when checking if you have a valid key because that makes the point of PersistentDataContainer redundant.
- The ItemBuilder has been updated to a slightly more modern version in preparation for MiniMessage support.

### Removed:
- Temporarily disabled logging keys to console/file due to a weird issue with keys not being taken, the section handling logging is pending re-write

### Added:
- A warning on startup if the spawn protection in server.properties isn't 0.
- Extra placeholders to all messages. The messages.yml will update with new comments showing what each message can use.
- A toggle to allow legacy key checks for niche use cases.
- Default commands to run per crate if no prize commands are found similar to `Prize-Messages`.
- %reward_stripped% which returns a stripped version of the reward for plugins like DiscordSRV.
- Ability to select custom particles and colors for QuadCrates.
- Ability to add enchants to ENCHANTED_BOOK, so they function in anvil.

### Changes:
- Updated example files in the examples folder
- All internal placeholders used in config.yml and messages.yml have changed.
  - %player% is now {player}
  - %crate% is now {crate}
  - %amount% is now {amount}
  - %key% is now {key}
  - %keys% is now {keys}
  - %page% is now {page}
  - %prefix% is now {prefix}
  - %world% is now {world}
  - %cratetype% is now {cratetype}
  - %prize% is now {prize}
  - %number% is now {number}
  - %keytype% is now {keytype}
  - %usage% is now {usage}
  - %key-amount% is now {key_amount}
  - %crates_opened% is now {crates_opened}
  - %id% is now {id}
- Used correct message when a player has no keys using /cc open.

### Enhancements:
- Prevent pistons from moving blocks if they are a crate.
- Play sounds in /crazycrates admin when a player gets virtual/physical keys.
- Change from sending messages in chat for /crazycrates admin to using action bars.
- Optimize item meta checks.
- If the crate main menu is turned off, trying to do /crates set menu will send you a message saying you can't.
- If the crate main menu is disabled, /crates will simply open the help message instead.
- Only check if a player has physical keys if the config option is enabled.

### API:
- Deprecated and marked for removal `CrazyCratesService` and `ICrazyCrates`.

### Fixed:
- Remove player from crate/page/preview arrays on inventory close event as it wasn't before.
- Player kept getting a preview message if they weren't in the preview when you did /crates reload.
- CrateOnTheGo where the event would fire twice using 2 of your crates.
- Failing to take keys would fire multiple times.
- Issue with mass-open related to being added to opening list and not being removed if no keys found.
- Multiple issues with how the inventory are checked for keys.
- Issue with QuadCrates where if you set the `structure.random` to false, it would still be random.

</details>

<details>
  <summary>v1.22 (1.20.4) - 2024-03-6</summary>

### Added:
- Ability to override the menu button functionality to use your own menu through DeluxeMenus and any other gui plugin.

### Changes:
- Re-did how /crate admin handles giving keys, It expands the size of the inventory to 54 slots and adds a button at the bottom explaining how to get keys.

</details>

<details>
  <summary>v1.21 (1.20.4) - 2024-03-2</summary>

### Added:
- New crate type called `Casino`. (Idea by slimemcstew)
- Ability to have files categorized by folder.
- War Crate as a default generated crate.
- Ability to have holograms created using CMI or DecentHolograms have a configurable "block" range.
- Ability to configure sounds per crate.
- Configure the cycling sounds when crates are doing animations, the sounds played when a crate ends.
- Adjust the volume of sounds and the speed of the sounds.

### Changes:
- Cosmic Crate configurations have new options, [click me!](https://docs.crazycrew.us/docs/1.20.4/plugins/crazycrates/guides/crates/examples/cosmiccrate-example)
- `Color` has been replaced by `Item` as you could have always used any item, so it was misleading to name it `Color`.
- If `Item` is not found, it will fall back to LIME_STAINED_GLASS_PANE.
- You can choose between a pre-defined tier for each row or have it pick randomly between available tiers.
- Cosmic Crate when initially picking crates, all the ??? crates will have a tier bound to them, so it actually matters when you pick them.
- Updated slot checks for menu items to rely on PersistentDataContainer.
- Re-organized the default /crates gui.
- Check uuids for quad crate sessions over player objects.
- Update /crates additem to take input for tiers which only work for cosmic/casino, /crates additem <crate_name> <prize_number> [tier]
- Update /crates additem again to take input for custom chance, Note: The max range by default is still 100 so keep it under 100. /crates additem <crate_name> <prize_number> <chance> [tier].
- No longer create a snapshot of the holder when checking for InventoryHolders.
- Add a config option to switch to a faster implementation of picking numbers. It defaults to `false` which is the old way of doing random.
- All messages in chat, lore of preview items, gui names even filler items have `PlaceholderAPI` support.

### Fixed:
- Crates being broken in worlds created by world plugins.
- Refund event needed to be fired sync.
- Display damage where if you put a value that can't be parsed as an integer like 50f, it wouldn't be empty durability.
- In-game editor wouldn't add tiers to casino/cosmic crate.
- Stored the wrong value for PDC causing it to error when using QuadCrates.

</details>

<details>
  <summary>v1.20.2 (1.20.4) - 2024-01-21</summary>

### Changes:
- Removed the wildcard crazycrates.open.*
- Registered crazycrates.open.<crate_name> to server permissions on startup.
  - Each crate will have one registered, if you remove a crate. The permission will not be removed from the server until server restart.
- Updated cluster api version.

### Fixed:
- Permission checks were not accurate.

</details>

<details>
  <summary>v1.20.1 (1.20.4) - 2024-01-9</summary>

### Fixed:
- Error when a player left the server.

</details>

<details>
  <summary>v1.20 (1.20.4) - 2024-01-7</summary>

### Changes:
- We no longer download Adventure API on runtime using the libraries feature in the plugin.yml.

### Fixed:
- Remove all data related to the crate they opened if they leave.

</details>

<details>
  <summary>v1.19.3 (1.20.4) - 2023-12-30</summary>

### Added:
- Toggle for the cosmic crate timeout feature. `Settings.Cosmic-Crate-Timeout` will be automatically added to your config.

### Fixed:
- Cosmic Crate time out feature was not working as expected.

</details>

<details>
  <summary>v1.19.2 (1.20.4) - 2023-12-29</summary>

### Fixed:
- Players not being removed from opening crates on quit thus crates getting stuck in a limbo of "Player is already opening crate."

</details>

<details>
  <summary>v1.19.1 (1.20.4) - 2023-12-28</summary>

### Fixed:
- ItemBuilder was throwing an error in console.
- 
</details>

<details>
  <summary>v1.19 (1.20.4) - 2023-12-11</summary>

### Removed:
- plugin-config.yml as it was just weird, Options in there will migrate to config.yml automatically.

### Changed:
- Bumped to 1.20.4
- Bumped nbt api
- Nested placeholders now work
  - `%crazycrates_<player>_<crate>_opened%` must be done like `%crazycrates_{player_name}_your_crate_opened%`
  - {player_name} can be replaced with almost any player variable from https://github.com/PlaceholderAPI/PlaceholderAPI/wiki/Placeholders
- /crazycrates admin-help has been removed.
  - /crazycrates help now has a permission check if the player has crazycrates.admin-access
- The permission crazycrates.command.player.help has been changed to crazycrates.help which defaults to TRUE
- Console can now use /crazycrates help.

**Full Changelog**: https://github.com/Crazy-Crew/CrazyCrates/compare/v1.18.5...v1.19

</details>