## Changes
- Ability to add slot based items to the crate previews.
  - https://docs.crazycrew.us/mods/crazycrates/reference/menus/buttons/
- All instances where a crate is opened will now respect when you disable a world.
- `Settings.Border.Glass-Border.Air` is now a config option.
  - If the value is set to false, AIR is not set to these slots which allows pagination to use the bottom row.
  - This is in unique scenarios where you never exceed 27 slots, and pagination effectively won't function properly.
- Improved message handling internally.
- Replaced ConfigMe with our own "copy-cat" system of it wrapped around Configurate
  - You will notice in the config.yml that comments are preserved, and there is extra lines added as result which I cannot change this currently!
  - Automatic migration for very old config files has not been coded yet.
  - The files will automatically apply comments, and add missing/new config options.
- Improved location storage, and loading locations on startup. Existing locations should continue to work.
  - Locations are now stored with random uuids, which removes the need to weirdly increment a number like before.
  - If crate locations are broken, the names of them will be printed to console on startup or reload.
- /crazycrates fix is now a command, and has replaced the automatic variant of fixing crate locations on startup.
- Added {broken_locations} as a placeholder for /crazycrates list.
### Crate Changes
#### Configuration Changes
All existing options in the `config.yml` will be respected first if they exist. You can find update examples in `examples/crates` for updated usage! 
- `crate.knock-back` in `config.yml` can now be configured per crate, and no longer is added to `config.yml` in new setups.
- `crate.disabled-worlds` in `config.yml` can now be configured per crate, and no longer is added to `config.yml` in new setups.
- `crate.quad-crate.timer` in `config.yml` can now be configured per crate, and no longer is added to `config.yml` in new setups.
- `crate.types.csgo.cycling-material` in `config.yml` can now be configured per crate, and no longer is added to `config.yml` in new setups.
- `crate.types.csgo.finished-material` in `config.yml` can now be configured per crate, and no longer is added to `config.yml` in new setups.
- `crate.keys.key-sound.toggle` & `crates.keys.key-sound.name` in `config.yml` can now be configured per crate, and no longer is added to `config.yml` in new setups.
### API Changes
#### CrazyCrates API has been bumped to 1.0.0
- ISettings has been removed from the CrazyCrates API
  - You can rely on the ConfigManager class through CratesProvider#api()#getConfigManager(), but it is subject to be refactored at any point.
  - I did not like the impl of `ISettings`, and with how I do configurations. It's best to interface directly for now.
- IServer is deprecated, and marked for removal. Please use CrazyCrates instead of IServer
  - IServer usage should still work(you will lack anything new). Please let me know if it doesn't

### Known Issues
- FireCracker is not working properly.

### Removed
- `use-different-random` has been removed from the config.yml as internally it did not make a difference.

### Fixes
- Fixed incorrect spelling of netherite_chestplate in the AdvancedExample.yml.
- Fixed recursion depth not working in fusion.yml.
- Fixed damage component being applied to an item when not told to.
  - This causes issues with plugins that prevent damaged items from interacting with their plugins.
- No longer trying to action bar messages if the sender is console sender.
- No longer allow people to constantly exit the editor mode
  - Adds a new message to tell the player they are not in editor mode.
- No longer checking if the items of a prize are empty when running /crates debug <crate>