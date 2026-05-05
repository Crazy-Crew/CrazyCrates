## Changes 🔨
- Improved updating the `examples` folder.
- Improved performance & readability when building crate objects.
- Improved performance when loading/adding crate locations.
- Removed `LegacyItemBuilder` as maintaining 2 ItemBuilders was not fun.
  - There should be no need to change configurations unless you use `HideItemFlags: true`
  - If you do use `HideItemFlags`, [Click Me](http://localhost:4321/mods/crazycrates/faq/#2-hideitemflags-no-longer-works-what-do-i-use-now)
- Improved migration types by looping through the folders instead of the FileManager cache which led to unnecessary checks.
- Improved verbose logging across the plugin.
- Improved /crazyccrates teleport <crate_id>.
- Improved placeholder parsing throughout the plugin.
- Replaced {key_amount} from the not_enough_keys message with {amount}.
  - {key_amount} is no longer usable.
- Placeholders that normally could've only been used in the DisplayName of items can now be used in DisplayLore as well.
- Cleaned up code related to sending messages to the player, or broadcasting messages to the server.

## Configuration Changes
### Glowing
- Glowing no longer is true/false, however existing configurations using true/false will still work as we look for that internally as well.
```yaml
  # Should the item glow?
  # Available Types: add_glow, remove_glow, none
  Glowing: "none"
```
### Hidden Components
If you are using `Hidden-Components` or `Components` in places, It has been replaced by `flags.components`, Existing configurations will still work.

## Bugs Fixed 🐛
- Fixed an issue with paginated guis creating a second page despite there not being enough items for it.
- Fixed an issue with log messages potentially not being saved.
- Fixed an issue with the ExcellentCrates migrator.

As always, Report 🐛 to https://github.com/Crazy-Crew/CrazyCrates/issues