- Removed all usage of Legacy ItemBuilder.
- Improved placeholder parsing across the whole plugin.
- Updated and improved the migration types.
- Updated multiple configuration files

## Changes
- Updated the remaining default internal placeholders to be wrapped in {} instead of %%, The previous placeholders using %% will still work.
- Removed all `HideItemFlag` options as we provide the ability to set Item Models which achieve the same result!
- Updated, and improved the migration types.
- Removed all old usage of the Legacy ItemBuilder.
- Removed the ability to create a Spawner with an entity type, It no longer worked. and Mojang provides a perfectly good command to create spawners.
- Removed unnecessary code that picked a prize 2,000+ times after it was already picked.

## Bug Fixed
- Fixed a bug where enchant levels were not being migrated properly when using `/crazycrates migrate -mt NewItemFormat`

## Configuration Changes
- Updated configuration for item displayed in /crates. [Example](https://github.com/Crazy-Crew/CrazyCrates/blob/3ae8c0eafcd3adbb8b5f6559ffe9fa4fb67ae3b8/paper/src/main/resources/crates/CrateExample.yml#L128)
    - You must run /crazycrates migrate -mt CratesDeprecated for the changes to reflect.
- `use-different-items-layout` if on a fresh install, is now true out of the box.
  - This is a warning, The old `Items` section is now considered stale, receives no new updates, and will likely be removed.