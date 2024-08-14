### Added:
- Ability to migrate crate configurations from ExcellentCrates.
  - active locations from ExcellentCrates are also migrated!

#### Crate Config Changes:
- Crate.CrateName is deprecated, and has been replaced by Crate.Name
  - The options were duplicate, and one wasn't used which annoyed me.
  - Crate.CrateName will be removed in the next version of Minecraft!
  - You can run the /crazycrates migrate CratesDeprecated to migrate deprecated options.
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

### Changes:
- Check if the prizes section is empty before opening a crate, previews unneeded chance calculation.
- Overhauled the `/crazycrates migrate` command, sends a more detailed message of what was migrated. 
  - files that show up red failed to migrate while files that are green succeeded
  - it also tells you the migration type you picked when running the command