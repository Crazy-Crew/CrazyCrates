### Breaking Changes:
- The weight system has been merged in, which effectively replaces the Chance/Max Range system.
  - You must run the command /crazycrates migrate WeightMigration, which will convert your configurations.

### Changes:
- Added config options, which allow you to configure slots 4 and 22 above the prize in `csgo` crate
- Added the ability to run commands with the gui customizer.

### Fixed:
- Fixed an issue, where if the border was toggled off. and you didn't have a second page, a glass pane would be there.
  - It will simply be air now until the border is on.