## Changes
- Added a new toggle to CrateExample.yml under `Settings.Border.Glass-Border` (You can check in `examples/crates/CrateExample.yml`)
- All instances where a crate is opened should now respect when you disable a world.
- The toggle `use-different-random` has been removed.

## Fixes
- Fixed a bug where we tried to send action bar messages to the console sender.
- Fixed a bug with setting the damage component to the item.
- Fixed a bug with recursion depth not working in fusion.yml.
- Fixed a typo in AdvancedExample.yml.