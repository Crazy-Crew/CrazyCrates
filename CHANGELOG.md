### Added:
- Ability the ability to make items glow in `Items`
```yml
Items:
  - 'Item:spawner, Glowing:true'
```

### Fixed:
- The config option for `verbose-logging` was not applied to some parts of the plugin.

### Changes:
- No longer add the contents of `DisplayData` to the `Items` section on `/crazycrates reload`
- Lowercase shield pattern types and colors which also fixed a display issue, so previous shield pattern/color configs work. They no longer need to be typed like GRADIENT_UP:LIGHT_GRAY, you can simply type gradient_up:light_gray
- Deprecated `Patterns` in favor of `DisplayPatterns`, it will be removed in the next major version of Minecraft.
```yml
    '6':
      # The name of the item to display in the gui.
      DisplayName: "<green>Fancy Shield"
      # The enchants to display in the gui.
      DisplayItem: "shield"
      # A list of patterns: https://jd.papermc.io/paper/1.21/org/bukkit/block/banner/PatternType.html
      # The patterns don't need to be uppercased. you can type them lowercased along with the colors.
      # Patterns have to be laid out in a specific order, otherwise it won't look right.
      # This also applies to the Items section.
      DisplayPatterns:
        - "base:white"
        - "gradient_up:light_gray"
        - "straight_cross:light_blue"
        - "flower:light_blue" 
```