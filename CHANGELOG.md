### Removed:
- All legacy color codes are removed, I do not want to maintain it anymore as I've figured out ways around needing it.
  - Migrating previous colors are easy, simply run /crazycrates debug <crate_name> before updating to this version of the plugin.
  - You can then run /crazycrates additem <crate> <prize> <chance> again.

### Fixed:
- CSGO Crate animation was delayed by 1 tick, for some reason.

### Changes:
- Removed sections of code related to giving a prize if `Editor-Items`, `Commands` or `Items` were all not found.
  - This would use the `DisplayItem`, `DisplayName`, `DisplayLore`, `DisplayEnchantments` and `DisplayAmount` as the prize.
- Updated the /crates additem command
  - CrazyCrates additem command now supports MiniMessage, regardless of the item format used.
  - This is only happening, as legacy colors have been removed and I did some research.
- Updated Vital API