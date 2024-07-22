### Added:
- Ability to define spawner type in prizes
```yml
    '1':
      # The name of the item to display in the gui.
      DisplayName: "<green>Creeper Spawner"
      # The enchants to display in the gui.
      DisplayItem: "spawner"
      # Prize settings
      Settings:
        # The custom model data of the item, -1 is disabled.
        Custom-Model-Data: -1
        # The type of mob for the spawner.
        Mob-Type: creeper
      # The amount to display in the gui.
      DisplayAmount: 1
      # The max range i.e. 15/100 = 15% chance to win.
      MaxRange: 100
      # The chance to win i.e. 15%
      Chance: 23
      Items:
        - 'Item:spawner, Mob:creeper'
```

### Changes:
- Play knockback/sounds if they don't have the required keys.
- Removed runtime dependency loader.
#### Cosmic Crate:
- Cosmic Crate was changed back in March for the calculation of tiers to be handled when you open the inventory, so that when picking a mystery crate. The choice would actually matter!
- It has been brought to my attention of being able to skimp it using client side mods. this has been addressed, the mods will no longer be able to skimp it by seeing item differences.
- The calculation still happens but, the picks are stored internally to the player's uuid in a cache which clears when the player quits, the inventory closes or the crate task ends for X reason.

### Fixed:
- Issue with file not found on first install.
- Issue where examples folder wasn't being created properly.
- Issue with shields not getting color or patterns.
- `Wonder` crate type never playing the cycle sound.
- `Wheel` crate type playing the stop sound twice.
- `Wheel` crate type not playing the cycle sound as most people have the client music muted.