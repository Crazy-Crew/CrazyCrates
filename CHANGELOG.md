## Additions:
### Crate Types:
* Added a new crate type called `CSGOTriple` ( Idea by slimemcstew )
   * This allows a player to win 3 prizes with 3 animations while opening a crate.
### Other:
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
  # Tiers are available in Cosmic and Casino crate types.
  # The Tiers the rewards can be found in.
  Tiers:
     # The Config Name for the Crate
     Basic:
        # The in-game name of the tier
        Name: '&8Basic Tier'
        # The item used for the secondary gui when you right-click for the preview.
        Item: 'GRAY_STAINED_GLASS_PANE'
        # Chance of that item getting picked. It would be 80/100 chance because MaxRange is 100.
        Chance: 80
        # The max range that the chance will go though.
        MaxRange: 100
     UnCommon:
        Name: '&aUncommon Tier'
        Item: 'LIME_STAINED_GLASS_PANE'
        Chance: 55
        MaxRange: 100
     Rare:
        Name: '&4Rare Tier'
        Item: 'RED_STAINED_GLASS_PANE'
        Chance: 20
        MaxRange: 100 
```
* `Color` has been replaced by `Item` as you could have always used any item, so it was misleading to name it `Color`
* If `Item` is not found, It will fall back to LIME_STAINED_GLASS_PANE.

* Added WarCrate as a default generated crate.
* Re-organized the default /crate gui
* Cleaned up the package layout
* Fixed a few bugs

## Other:
* [Feature Requests](https://github.com/Crazy-Crew/CrazyCrates/issues)
* [Bug Reports](https://github.com/Crazy-Crew/CrazyCrates/issues)