## Changes 🔨
- Improved updating the `examples` folder.
- Removed `LegacyItemBuilder` as maintaining 2 ItemBuilders was not fun.
  - There should be no need to change configurations unless you use `HideItemFlags: true`
  - If you do use `HideItemFlags`, [Click Me](https://docs.crazycrew.us/mods/crazycrates/faq/#2-hideitemflags-no-longer-works-what-do-i-use-now)
- Improved migration types by looping through the folders instead of the FileManager cache which led to unnecessary checks.
- Improved verbose logging across the plugin.
- Improved placeholder migrations in config migrator.
- Improved `/crazycrates teleport <crate_id>`.
- Improved placeholder parsing throughout the plugin.
- Replaced `{key_amount}` from the not_enough_keys message with `{amount}`.
  - `{key_amount}` is no longer usable.
- Placeholders that normally could've only been used in the DisplayName of items can now be used in DisplayLore as well.
- Cleaned up code related to sending messages to the player, or broadcasting messages to the server.
- Removed an unnecessary prize loop for Cosmic Crate, if we do the math right initially... we shouldn't need this.
- Switched to using StringUtils#tryParseInt which uses an optional for better verbosity instead of Integer#parseInt.
- Updated default configuration files.
### Crate Changes
- Improved tracking code for crates.
- Improved performance & readability when building crate objects.
- Improved performance when loading/adding crate locations.
- Improved cleaning up crate tasks, and other caches when a crate opening ends.
  - This should fix a few other weird issues.

### Key Changes
- Fixed a logical issue with taking keys using `/crazycrates mass-open` or sneaking on QuickCrate after giving the prizes, it should be the other way around to minimize the free loot gain if something happens.
  - In addition to this, if they are keys *not* taken for any reason like your inventory being full, they should be refunded to you.

#### Crate Types
- CrateOnTheGo now passes through the openCrate method, so things like Required Keys, and other features the other crates have are now respected.

### Item Layout
- By default, `use-different-items-layout` is set to true now.
  - Existing servers with an up-to-date config will not have to change anything.
#### Item Changes
- Removed the option to configure the spawner preview.
  - This is pointless, because they all look the same in crate menus.
- Updated glow configuration, Glowing now accepts values like "add_glow", "remove_glow", or "none"
  - Existing true/false should work, but you should update it as soon ass possible.

## Removals
### Permissions
- Removed the `use-new-permission-system` toggle, and keep the original behaviour everyone liked.

## Additions
### Key Placeholders
- Added shorthand formatting to key placeholders, so 1,000 would become 1k and so on.

### Plugin Support
**Note: replace `<item_id>` with the correct identifier from the custom item plugins.**
**Second Note: Any place you can input `diamond_axe` allows you to use this format.**
**Third Note: You can still use `emerald_helmet` as in direct ids as well**

- Added support for HMCWraps.
  - `DisplayItem: hmcwraps@<item_id>`
- This introduces new formats for other plugins like Oraxen, Nexo, CraftEngine, and ItemsAdder
  - `DisplayItem: nexo@<item_id>`
  - `DisplayItem: oraxen@<item_id>`
  - `DisplayItem: itemsadder@<item_id>`
  - `DisplayItem: craftengine@<item_id>`

### New Commands
- Added /crates version which includes the git commit, previous git commit, and current version.
  - If any value is not found, it will return "N/A"

### Other
- Added custom item support in areas that didn't have it.
- Added text shadow support for FancyHolograms.
  - [#890](https://github.com/Crazy-Crew/CrazyCrates/pull/890)

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
- getPrize not filtering out invalid weights properly leading to weird prize winnings.
  - We did the correct check for the totalWeight variable, but when looping through prizes. Prizes with -1 weight were still present. 
  - If you add -1 to a value, it increases the value which can cause issues with prize chances. 
  - Any value with 0.0, 0, or numbers less than 0 will be considered filler items.
- Paginated guis creating a second page despite there not being enough items for it.
- Log messages potentially not being saved.
- Updated the ExcellentCrates migrator which fixed a few migration bugs
- Fixed an important issue with `/crazycrates transfer`.
  - If you cannot update, I recommend denying the permission to use this command.
- Fixed issues with `/crazycrates take/give` & `/crazycrates mass-open` by clamping the amount to be a required amount.
- CMI in recent updates, whether it's CMI Lib or CMI API, in short. the API broke, and I forked it, compiled it, published it to my repo... and it seems to work for now
  - It seems I shall be maintaining it until the heat death of the universe.
    - [#876](https://github.com/Crazy-Crew/CrazyCrates/issues/876)
    - [#9919](https://github.com/Zrips/CMI/issues/9919) CMI's issue on Zrips repository that is closed by a stupid bot.
- `/crazycrates debug` was not working properly.
- Use correct variables when logging messages.
- Fix multiple issues with custom heads/skulls not being built.
- Use correct message when a player has no keys when doing /keys.
- Fixed an issue with mass-open where if any of the rows were null, or the tiers were empty. the keys you would've spent would've been lost. They get refunded now.
- Message responses when running commands not being sent to console.

As always, Report 🐛 to https://github.com/Crazy-Crew/CrazyCrates/issues