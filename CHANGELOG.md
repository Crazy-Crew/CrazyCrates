### Removed:
- All legacy color codes are removed, I do not want to maintain it anymore as I've figured out ways around needing it.
  - You can run /crazycrates migrate LegacyColorAll which should migrate all values in `config.yml`, `messages.yml` and all `crate` files.

### Fixed:
- CSGO Crate animation was delayed by 1 tick, for some reason.
- Fixed spacing in migrate command usage.

### Changes:
- Improved /crazycrates migrate internally
  - ExcellentCrates Migrator has changed significantly, report any bugs you might find. It will convert legacy color codes to MiniMessage.
  - Properly warn the player/sender if the inputted migration type is not valid.
- Removed sections of code related to giving a prize if `Editor-Items`, `Commands` or `Items` were all not found.
  - This would use the `DisplayItem`, `DisplayName`, `DisplayLore`, `DisplayEnchantments` and `DisplayAmount` as the prize.
- Updated the /crazycrates additem command
  - CrazyCrates additem command now supports MiniMessage, regardless of the item format used.
  - This is only happening, as legacy colors have been removed and I did some research to improve things.
- Updated the config option `use-old-editor`, It is now migrated to `use-new-editor`
  - `true` uses the new editor, `false` uses the old one which is more readable. The option should be migrated on startup.
- Updated Vital API