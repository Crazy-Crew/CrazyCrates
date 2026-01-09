## Changes
- Updated the remaining default internal placeholders to be wrapped in {} instead of %%, The previous placeholders using %% will still work.
- Removed all `HideItemFlag` options as we provide the ability to set Item Models which achieve the same result!
- Updated, and improved the migration types.
- Removed all old usage of the Legacy ItemBuilder.
- Removed the ability to create a Spawner with an entity type, It no longer worked. and Mojang provides a perfectly good command to create spawners.
- Removed unnecessary code that picked a prize 2,000+ times after it was already picked.

## Bug Fixed
- Fixed a bug where enchant levels were not being migrated properly.

## Configuration Changes
- Updated configuration for item displayed in /crates. You must run /crazycrates migrate -mt CratesDeprecated.
- `use-different-items-layout` if on a fresh install, is now true out of the box.
    - The old `Items` section is now considered stale, receives no new updates, and will likely be removed.