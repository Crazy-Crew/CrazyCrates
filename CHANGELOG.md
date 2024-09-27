### Breaking Changes:
- The weight system has been merged in, which effectively replaces the Chance/Max Range system.
  - You must run the command /crazycrates migrate WeightMigration, which will convert your configurations.

### Additions:
- Added the ability to log the plugin actions to file i.e. crates.log and keys.log... A list of events currently tracked
  - event_key_given
  - event_key_sent
  - event_key_received
  - event_key_transferred
  - event_key_removed
  - event_key_taken
  - event_key_taken_multiple
  - event_crate_opened
  - event_crate_force_opened
  - event_crate_mass_opened
- If you notice anything not tracking right, or lacking tracking. Create an issue.
- The files will zip on /crazycrates reload, plugin shutdown, plugin startup.
  - This avoids the files getting large, and leading to issues with crashing the server.
  - It mimics how Paper handles their .log files...

### Changes:
- Added config options, which allow you to configure slots 4 and 22 above the prize in `csgo` crate
- Added the ability to run commands with the gui customizer.

### Fixed:
- Fixed an issue, where if the border was toggled off. and you didn't have a second page, a glass pane would be there.
  - It will simply be air now until the border is on.