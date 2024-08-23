### Added:
- Ability to migrate crate configurations from ExcellentCrates.
  - active locations from ExcellentCrates are also migrated!
- Added a new feature where the `RequiredKeys` can also take that amount of keys [#755](https://github.com/Crazy-Crew/CrazyCrates/issues/755)
  - `use-required-keys` in `config.yml` has to be true for that to take effect.
- Added %chance% placeholders to prizes and tiers

#### Crate Config Changes:
- Crate.CrateName is deprecated, and has been replaced by Crate.Name
  - The options were duplicate, and one wasn't used which annoyed me.
  - Crate.CrateName will be removed in the next version of Minecraft!
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
- Crate.Preview-Name is deprecated, and has been replaced by Crate.Preview.Name
  - The option was meant to always be under Crate.Preview
  - Crate.Preview-Name will be removed in the next version of Minecraft!
  - You can run /crazycrates migrate CratesDeprecated to migrate deprecated options.
```yml
  Preview:
    # The name of the inventory for the preview menu.
    Name: "<green>Basic Crate Preview" # moved it under Preview
    # Turn on and off the preview for this crate.
    Toggle: true
    # How many lines the Crate Preview should have. Including Header and Bottom (Between 3 and 6)
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
- Removed all getItemMeta/hasItemMeta calls for checking PersistentDataContainer
  - We now check ItemStack#PersistentDataContainerView which no longer relies on ItemStack#getItemMeta
  - TLDR: stonks
- Right click now opens the crate menu as well.
- Checked location strings instead of object ids
- Simplified multiple location getters, don't need to get the same location 3 times if we aren't changing it.
- `{crate}` in messages will now return `Crate.Name` instead of the file name
  - Cleaned up internals related to sometimes, the file name being used along with bad naming schemes.
  - `Crate#getName()` is now `Crate#getFileName()` while `Crate#getCrateInventoryName()` is `Crate#getCrateName()`
- Check if the prizes section is empty before opening a crate, previews unneeded chance calculation.
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