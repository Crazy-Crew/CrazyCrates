## Breaking Changes:
- Added minimessage support which replaces legacy color codes like &7 or &c
  - CMI/DecentHolograms do not support MiniMessage so you still have to use legacy color codes for that.

- [b290d54](https://github.com/Crazy-Crew/CrazyCrates/commit/b290d54) Updated /cc additem to be much more precise and friendly

> Previously added rewards will still work until the next major version of Minecraft. The new /cc additem currently only supports 1 item at a time but it's an improvement from before. DisplayAmount is what tells the plugin how much of the item to give so that is required. It will default to 1!
>
> If you add an `Items:` section, It will use that instead for rewards and treat the nbt tag as if it was just purely for display.

## Additions:
- Added [folia](https://github.com/Crazy-Crew/CrazyCrates/pull/658) support.
- Added the ability to color the background using hex colors of holograms. It only works with CMI and FancyHolograms.
  - You can check the examples/crates folder under Hologram section for an example of how to add colors.
  - `transparent` is an option that can be used as well for see through holograms which is the default.
- Add the option to HideItemFlags in filler glass and crate preview items.
- Add config option to turn off the auto updating of examples folder.

## Extra Item Options:
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

## Plugin Support:
- Add placeholder api support to broadcast message in each crate file.
- Added support for FancyHolograms by Oliver.
- Add support for PlaceholderAPI in key displayname/lores.

## Changes:
- Updated how holograms are handled. FancyHolograms should be less finnicky and CMI should perform better.
- Ability to set `Chance` in crate fiels to -1 to use filler items.

## Fixes:
- Fixed double lines with decentholograms.
- Fixed an issue where list messages would have an extra line at the end.
- Fixed an issue with materials not being recognized.
- Fixed a bug where keys did not have lores.

## Previous Fixes:
- Fixed an issue where if display names matched, it would not give the right prize.
- Fixed an issue with casino crate where you could open a casino crate without the key.
- Temp fix for double message when trying to open a crate with key in off hand. This means for the time being, Keys cannot be used in off hand for physical crates.

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyCrates/discussions/categories/features)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyCrates/issues)
