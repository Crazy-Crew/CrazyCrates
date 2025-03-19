# New Item Format
I've been working on a new format for the `Items` section as the current one is very restrictive, You can experiment with this by enabling it in your `config.yml`

- The command /crazycrates migrate -mt NewItemFormat will update all `Items` section to the best it can.
  - The migration is one way, Please be wary of this and take backups.
  - Editor-Items should be migrated as soon as possible when you feel like it as this is much more readable, I plan to do much more to expand on the in-game editor since the structure makes such a thing easier.

You can find an example of the `Items` format linked below
https://github.com/Crazy-Crew/CrazyCrates/blob/main/core/src/main/resources/crates/beta/NewCrate.yml

## Fixes
- ItemsAdder blocks were not working with left/right click
- A double message sent to admins when using /crazycrates giveall
- Holograms were not refreshing when using DecentHolograms
- Set the default item plugin option to "None" because it was causing issues
- Fixed a few other issues like ItemStacks not stacking properly

## Changes
- Removed an unused config option from the config.yml
  - The option this was replaced with has already been moved to fusion.yml previously
- Marked `use-new-permission-system` for removal
  - This option is being removed as crazycrates.open.<crate-name> will be the permission going forward.