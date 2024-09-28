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
- Added the ability to respin prizes to each crate file, if the option is enabled.
```yml
    # Settings related to rewards.
    Rewards:
      # Should a yes/no popup be made, to ask if they want to keep the prize?
      Re-Roll-Spin: false
      # Should there be a limit to how many times they can re-roll?
      Permission:
        # Should this be enabled?
        Toggle: false
        # This will define how many permissions will be registered to the server per crate.
        # i.e. crazycrates.respin.<crate_name>.1-20
        # It will simply register multiple permissions, so it shows up in things like LuckPerms.
        Max-Cap: 20 
```
- You can view an example of this in `examples/crates/CrateExample.yml`
- You must have `Re-Roll-Spin` set to true to allow re-rolls.
- You must have `Permission.Toggle` set to true, for it to be permission based.
  - The higher the permission, the more spins they have.
  - Internally, we loop through a player's permissions.... and find the highest matching one with `crazycrates.respin.<crate_name>.<amount>`
  - The permissions will be registered on startup, and on /crates reload if not found.
    - We also unregister on /crates reload, if you set `Permissions.Toggle` to false.
  - `<amount>` is the `Max-Cap`, It will not go any higher... The higher that number is, the heavier the permission checks.
- Crate Types such as Cosmic Crate, Casino Crate, QuadCrate, and WarCrate do not have support for re-spins
  - Casino Crate has 3 prizes, the gui currently only supports 1 prize.
  - Cosmic Crate has 4 prizes that you pick, the gui currently only supports 1 prize.
  - QuadCrate has 4 prizes, the gui currently only supports 1 prize.
  - WarCrate is in a similar situation, where it's picked prizes.
- The complexity for these crate types above will take some time to add it in while it not being a train wreck.

### Fixed:
- Fixed an issue, where if the border was toggled off. and you didn't have a second page, a glass pane would be there.
  - It will simply be air now until the border is on.