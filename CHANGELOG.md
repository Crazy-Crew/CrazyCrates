### Removed:
- All legacy color codes are removed, I do not want to maintain it anymore.

### Fixed:
- CSGO Crate animation was delayed by 1 tick, for some reason.

### Changes:
- Updated the /crates additem command
  - CrazyCrates now supports MiniMessage, regardless of the item format used.
  - This is only happening, as legacy colors have been removed and I did some research.
- Updated Vital API
- Removed the code, that if no `Items` or `Editor-Items` or `Commands` were found, that it would use the `DisplayItem`, `DisplayName` etc as a prize.