### Added:
- Ability to migrate crate configurations from ExcellentCrates.
  - active locations from ExcellentCrates are also migrated!

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

### Changes:
- Check if the prizes section is empty before opening a crate, previews unneeded chance calculation.
- Overhauled the `/crazycrates migrate` command, sends a more detailed message of what was migrated. 
  - files that show up red failed to migrate while files that are green succeeded
  - it also tells you the migration type you picked when running the command