
## Additions:
### Crate Types:
* Added a new crate type called `Casino` ( Idea by slimemcstew )
   * This allows a player to win 3 prizes with 3 animations while opening a crate.
### Ability to have files categorized by folder.
* It will search for folders in `crates` such as `crates/sub_folder`
  * It will not search for folders inside `crates/sub_folder` such as `crates/sub_folder/secondary_folder`
### Other:
* Added War Crate as a default generated crate.
* Added the ability to have holograms created using CMI or DecentHolograms have a configurable "block" range.
```yml
  Hologram:
     # Toggle on and off the holograms for the crates.
     Toggle: true
     # The height of the hologram above the crate.
     Height: 1.5
     # The distance the hologram can be seen. Only works with CMI and DecentHolograms
     Range: 8
     # The message that will be displayed.
     Message:
        - '&7&lBasic Chest' 
 ```
* Added the ability to configure sounds per crate.
   * The places these sounds apply to are /crate, clicking buttons in /crate, clicking buttons in the crate previews like next/back or exit
* You can configure the cycling sounds when crates are doing animations, the sounds played when a crate ends.
* You can adjust the volume of sounds and the speed of the sounds.
* Clicking each prize in the preview will not produce a sound as that has no purpose.
#### An example of the configuration layout that you should add to your crate configurations, You can look in the `examples` folder for the default files.
```yml
Crate:
   sound:
      # The sound options when the animation is cycling.
      cycle-sound:
         # If sound should be enabled or not.
         toggle: true
         # The type of sound to use.
         # https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html
         value: 'BLOCK_NOTE_BLOCK_XYLOPHONE'
         # The volume of the pitch.
         volume: 1.0
         # The speed of the sound.
         pitch: 1.0
      # The sound options when an item is clicked.
      click-sound:
         # If sound should be enabled or not.
         toggle: true
         # The type of sound to use.
         # https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html
         value: 'UI_BUTTON_CLICK'
         # The volume of the pitch.
         volume: 1.0
         # The speed of the sound.
         pitch: 1.0
      # The sound options when a crate ends.
      stop-sound:
         # If sound should be enabled or not.
         toggle: true
         # The type of sound to use.
         # https://jd.papermc.io/paper/1.20/org/bukkit/Sound.html
         value: 'ENTITY_PLAYER_LEVELUP'
         # The volume of the pitch.
         volume: 1.0
         # The speed of the sound.
         pitch: 1.0
 ```

## Changes:
* A piece of the configuration for Cosmic Crate has been changed.
```yml
  tier-preview:
    # Turn on and off the preview for this crate.
    toggle: true
    # How many lines the Tier Preview should have. Including Header and Bottom (Between 3 and 6)
    rows: 5
    glass:
      # Turn the glass border in the preview on and off.
      toggle: true
      # The name of the border item.
      name: ' '
      # The item that shows in the border. Can be glass or any other item.
      item: 'RED_STAINED_GLASS_PANE'
  # Tier related settings only for Casino should be random.
  random:
    # If the tiers should be random.
    toggle: false
    # The rows with pre-defined tiers.
    types:
      # Row 1
      row-1: Basic
      # Row 2
      row-2: UnCommon
      # Row 3
      row-3: Rare
  # Tiers are available in Cosmic and Casino crate types.
  # The Tiers the rewards can be found in.
  Tiers:
    # The Config Name for the Crate
    Basic:
      # The in-game name of the tier.
      Name: '&8Basic Tier'
      # The in-game lore of the tier.
      Lore:
        - '&7A basic tier.'
      # The item used for the secondary gui when you right-click for the preview.
      Item: 'CHEST'
      # Chance of that item getting picked. It would be 80/100 chance because MaxRange is 100.
      Chance: 80
      # The max range that the chance will go though.
      MaxRange: 100
      # The slot this item will be in the secondary gui.
      Slot: 20
    UnCommon:
      Name: '&aUncommon Tier'
      Lore:
        - '&aAn uncommon tier.'
      Item: 'CHEST'
      Chance: 55
      MaxRange: 100
      Slot: 22
    Rare:
      Name: '&4Rare Tier'
      Lore:
        - '&cA rare tier.'
      Item: 'ENDER_CHEST'
      Chance: 20
      MaxRange: 100
      Slot: 24
```
* `Color` has been replaced by `Item` as you could have always used any item, so it was misleading to name it `Color`
* If `Item` is not found, It will fall back to LIME_STAINED_GLASS_PANE.
* You can choose between a pre-defined tier for each row or have it pick randomly between available tiers.
* Note: We only have 3 rows, so you obviously can only have 1 tier per row. This is not likely to change for this Crate Type... 

## Enhancements:
* Cosmic Crate when initially picking crates, All the ??? crates will have a tier bound to them, so it actually matters when you pick them.
* Updated slot checks for menu items to rely on PersistentDataContainer.
* Re-organized the default /crate gui
* Check uuids for quad crate sessions over player objects.
* Update /cc additem to take input for tiers which only work for cosmic/casino, /cc additem <crate_name> <prize_number> [tier]
* Update /cc additem again to take input for custom chance, Note: The max range by default is still 100 so keep it under 100. /cc additem <crate_name> <prize_number> <chance> [tier]
  * [tier] is an optional arg.
* No longer create a snapshot of the holder when checking for inventoryholders
* Add a config option to switch to a faster implementation of picking numbers. It defaults to `false` which is the old way of doing random.
* All messages in chat, lore of preview items, gui names even filler items have `PlaceholderAPI` support.

## Fixes:
 * Fixed issues with crates being broken in worlds created by world plugins.
 * Fixed a few other bugs I can't remember.
 * Fixed a bug where the refund event needed to be fired sync.
 * Fixed a bug with display damage where if you put a value that can't be parsed as an integer like 50f, It wouldn't just be empty durability.
 * Fixed a bug where cc additem wouldn't add tiers to casino/cosmic crate.
 * Fixed a bug where we stored the wrong value for PDC causing it to error when using QuadCrates.

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyCrates/issues)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyCrates/issues)
